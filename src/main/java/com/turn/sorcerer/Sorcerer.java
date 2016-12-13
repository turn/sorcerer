/*
 * Copyright (c) 2015, Turn Inc. All Rights Reserved.
 * Use of this source code is governed by a BSD-style license that can be found
 * in the LICENSE file.
 */

package com.turn.sorcerer;

import com.turn.sorcerer.config.impl.SorcererConfiguration;
import com.turn.sorcerer.exception.SorcererException;
import com.turn.sorcerer.executor.Abortable;
import com.turn.sorcerer.executor.PipelineExecutor;
import com.turn.sorcerer.executor.PipelineScheduler;
import com.turn.sorcerer.executor.TaskExecutor;
import com.turn.sorcerer.injector.SorcererInjector;
import com.turn.sorcerer.pipeline.Pipeline;
import com.turn.sorcerer.pipeline.type.PipelineType;
import com.turn.sorcerer.status.Status;
import com.turn.sorcerer.status.StatusManager;
import com.turn.sorcerer.task.type.TaskType;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import org.apache.log4j.BasicConfigurator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Represents a Sorcerer instance and provides the public API's to the
 * Sorcerer module
 *
 * @author tshiou
 */
public class Sorcerer {

	private static final Logger logger =
			LoggerFactory.getLogger(Sorcerer.class);

	// 30 second retry
	private static final int STORAGE_RETRY_MILLIS = 30000;

	/**
	 * Sorcerer injector containing bindings for all Sorcerer member variables
 	 */
	private final SorcererInjector injector;

	/**
	 * List of pipeline scheduling threads
	 */
	private List<ExecutorService> pipelineThreads = Lists.newArrayList();
	private List<PipelineScheduler> scheduledPipelines = Lists.newArrayList();
	private Map<String, Abortable> adhocTasks = Maps.newHashMap();
	private Map<String, Abortable> adhocPipelines = Maps.newHashMap();

	/**
	 * Private constructor to prevent public instantiation.
	 * Should only be used by the Sorcerer builder.
	 *
	 * @param injector An instance of a Sorcerer Injector will be
	 *                 used to inject all member variables.
	 */
	private Sorcerer(SorcererInjector injector) {
		this.injector = injector;
	}

	/**
	 * Starts the Sorcerer service
	 *
	 * <p>
	 * Since Sorcerer relies on the storage layer for pipeline and task states
	 * it is imperative that the storage layer is initialized before anything
	 * is scheduled. Therefore Sorcerer will try to initialize the storage
	 * layer and continue retrying if it fails.
	 * </p>
	 *
	 * <p>
	 * After the storage layer is initialized, Sorcerer will proceed to
	 * instantiating pipeline scheduling threads and submitting them.
	 * </p>
	 */
	public void start() {
		logger.info("Starting Sorcerer");

		// Check if the storage layer is initialized
		while (StatusManager.get().initialized() == false) {
			try {
				Thread.sleep(STORAGE_RETRY_MILLIS);
			} catch (InterruptedException e) {
				logger.debug("Storage retry interrupted");
			}
		}

		// Schedule and run pipelines
		scheduleAndRunPipelines();
	}

	/**
	 * Creates instances of
	 * {@link com.turn.sorcerer.executor.PipelineScheduler} and submits them
	 * for execution. A Single thread per scheduler.
	 */
	private void scheduleAndRunPipelines() {

		if (injector.getPipelines() == null) {
			logger.info("No pipelines found.");
			return;
		}

		for (PipelineType pipeline: injector.getPipelines()) {

			if (pipeline == null) {
				logger.error("Could not instantiate pipeline.");
				return;
			}

			schedulePipeline(pipeline);
		}

		logger.debug("Exiting Sorcerer scheduling thread");
	}

	/**
	 * Schedules pipeline by creating pipeline scheduler thread
	 */
	private void schedulePipeline(PipelineType pipeline) {
		logger.info("Starting pipeline {}", pipeline.toString());

		PipelineScheduler pipelineScheduler = new PipelineScheduler(pipeline);

		ExecutorService pipelineThread = Executors.newSingleThreadExecutor();
		pipelineThread.submit(pipelineScheduler);
		pipelineThreads.add(pipelineThread);

		logger.debug("Successfully scheduled pipeline {}", pipeline.toString());

		scheduledPipelines.add(pipelineScheduler);
	}

	/**
	 * Abort scheduled pipeline
	 */
	private void abortScheduledPipeline(PipelineType pipeline) {
		logger.info("Aborting pipeline {}", pipeline.toString());

		for (Iterator<PipelineScheduler> iterator = scheduledPipelines.iterator(); iterator.hasNext();) {
			PipelineScheduler scheduledPipeline = iterator.next();
			if (scheduledPipeline.getPipelineType().getName().equals(pipeline.getName())) {
				scheduledPipeline.abort();
				// Remove the current element from the iterator and the list.
				iterator.remove();
			}
		}
		logger.debug("Successfully aborted pipeline {}", pipeline.toString());
	}

	/**
	 * Stops the Sorcerer service by initiating the shutdown of all pipeline
	 * scheduling threads.
	 */
	public void stop() {
		for (ExecutorService thread : pipelineThreads) {
			thread.shutdown();
		}
	}

	/**
	 * Provides a List of scheduled pipelines in this instance of a Sorcerer module
	 *
	 * @return Returns a List of pipelines
	 */
	public Collection<String> getPipelines() {

		if (scheduledPipelines == null) {
			return null;
		}

		List<String> pipelineNames = Lists.newArrayList();
		for (PipelineScheduler scheduler : scheduledPipelines) {
			pipelineNames.add(scheduler.getPipelineType().getName());
		}

		return pipelineNames;
	}

	/**
	 * Provides the current iteration number of a pipeline.
	 *
	 * @see     com.turn.sorcerer.pipeline.Pipeline#getCurrentIterationNumber()
	 * @param   pipelineName Name of pipeline
	 * @return  Current iteration number of pipeline
	 */
	public int getCurrentIterNoForPipeline(String pipelineName) {
		Pipeline p = injector.getInstance(injector.getPipelineType(pipelineName));
		return p.getCurrentIterationNumber();
	}

	/**
	 * Builds and provides the workflow DAG of a pipeline
	 *
	 * <p>
	 * This method recursively builds the workflow DAG of a pipeline. This is
	 * represented by a Map of a task name as the key and a list of next task
	 * names as the value.
	 * </p>
	 *
	 * @param pipelineName Name of pipeline
	 * @return Workflow DAG of pipeline represented by a Map
	 */
	public Map<String, Set<String>> getTasksForPipeline(String pipelineName) {
		PipelineType pipelineType = injector.getPipelineType(pipelineName);

		Map<String, Set<String>> map = Maps.newLinkedHashMap();

		buildGraph(map, pipelineType.getInitTaskName());

		return map;
	}

	/**
	 * Build the workflow DAG
	 *
	 * Breadth first search was chosen since it is creates a more intuitive
	 * return graph
	 */
	private void buildGraph(Map<String, Set<String>> graph, String initTask) {
		List<String> queue = Lists.newArrayList(initTask);

		while (queue.isEmpty() == false) {
			String taskName = queue.remove(0);

			if (taskName == null) {
				return;
			}

			Set<String> nextTasks = graph.get(taskName);

			if (nextTasks == null) {
				nextTasks = Sets.newHashSet();
				graph.put(taskName, nextTasks);
			}

			TaskType type = injector.getTaskType(taskName);
			List<String> next = type.getNextTaskNames();

			if (next == null) {
				continue;
			}

			nextTasks.addAll(next);
			queue.addAll(next);
		}
	}

	/**
	 * Provides the status of a task
	 *
	 * @param taskName  Name of task to check status
	 * @param iterNo    Iteration number of task to check status
	 * @return          Status of task (See {@link Status})
	 */
	public Status getTaskStatus(String taskName, int iterNo) {
		return StatusManager.get().checkTaskStatus(taskName, iterNo);
	}

	/**
	 * Provides the status of a pipeline
	 *
	 * @param pipelineName Name of pipeline to check status
	 * @param iterNo        Iteration number of pipeline to check status
	 * @return              Status of pipeline (See {@link Status})
	 */
	public Status getPipelineStatus(String pipelineName, int iterNo) {
		return StatusManager.get().checkPipelineStatus(pipelineName, iterNo);
	}

	/**
	 * Sets the status of a task
	 *
	 * @param taskName  Name of task to set status
	 * @param iterNo    Iteration number of task to set status
	 * @param status    Status to set (See {@link Status})
	 */
	public void setTaskStatus(String taskName, int iterNo, Status status) {
		TaskType type = injector.getTaskType(taskName);

		StatusManager.get().commitTaskStatus(type, iterNo, status);
	}

	/**
	 * Sets the status of a pipeline
	 *
	 * @param pipelineName  Name of pipeline to set status
	 * @param iterNo        Iteration number of pipeline to set status
	 * @param status        Status to set
	 */
	public void setPipelineStatus(String pipelineName, int iterNo, Status status) {
		PipelineType type = injector.getPipelineType(pipelineName);

		StatusManager.get().commitPipelineStatus(type, iterNo, status);
	}

	/**
	 * Abort task
	 *
	 * @param taskName   Name of task type
	 * @param iterNo      Iteration number
	 * @param scheduled  If true, sorcerer will search scheduled pipelines for task
	 */
	public void abortTask(String taskName, int iterNo, boolean scheduled)
			throws IllegalArgumentException {
		if (scheduled) {
			// TODO: not supported yet
		} else {
			if (this.adhocTasks.containsKey(taskName + iterNo)) {
				this.adhocTasks.get(taskName + iterNo).abort();
			} else {
				throw new IllegalArgumentException("Could not find running task for " + taskName);
			}
		}
	}

	/**
	 * Abort pipeline
	 *
	 * @param pipelineName Name of pipeline type
	 * @param iterNo        Iteration number
	 * @param scheduled     If true, sorcerer will search scheduled pipelines
	 */
	public void abortPipeline(String pipelineName, int iterNo, boolean scheduled) {
		if (scheduled) {
			PipelineType pipelineType = injector.getPipelineType(pipelineName);
			if (pipelineType == null) {
				throw new IllegalArgumentException("Could not find pipeline for " + pipelineName);
			}
			abortScheduledPipeline(pipelineType);
		} else {
			if (this.adhocPipelines.containsKey(pipelineName + iterNo)) {
				this.adhocPipelines.get(pipelineName + iterNo).abort();
			} else {
				throw new IllegalArgumentException("Could not find running pipeline for " + pipelineName);
			}
		}
	}

	/**
	 * Schedule pipeline
	 *
	 * @param pipelineName Name of pipeline
	 * @param iterNo        Iteration number
	 * @param adhoc         Adhoc pipeline
	 */
	public void schedulePipeline(String pipelineName, int iterNo, boolean adhoc)
			throws IllegalArgumentException {

		PipelineType type = injector.getPipelineType(pipelineName);

		if (type == null) {
			throw new IllegalArgumentException("Could not find pipeline definition for name " + pipelineName);
		}

		if (adhoc) {
			runPipeline(pipelineName, iterNo, new HashMap<String, Map<String, String>>(), false);
		} else {
			schedulePipeline(type);
		}

	}

	/**
	 * Executes a single task
	 *
	 * @param taskName      Name of task to execute
	 * @param iterNo        Iteration number of task to execute
	 * @param arguments     Map of arguments to pass to task
	 */
	public void runTask(String taskName, int iterNo, Map<String, String> arguments) {
		TaskType type = injector.getTaskType(taskName);

		TaskExecutor executor = new TaskExecutor(type, iterNo, arguments, true);

		Executors.newSingleThreadExecutor().submit(executor);
		this.adhocTasks.put(taskName + iterNo, executor);
	}

	/**
	 * Executes a pipeline
	 *
	 * @param pipelineName  Name of pipeline to execute
	 * @param iterNo        Iteration number of pipeline to execute
	 * @param argMap        Map of task arguments
	 * @param overwrite     If set to true then Sorcerer will execute tasks that
	 *                         have already been completed
	 */
	public void runPipeline(String pipelineName, int iterNo,
	                        Map<String, Map<String, String>> argMap, boolean overwrite) {
		PipelineType type = injector.getPipelineType(pipelineName);

		Map<TaskType, Map<String, String>> taskArgMap = Maps.newHashMap();

		PipelineExecutor executor = new PipelineExecutor(type, iterNo, taskArgMap, true, overwrite);

		Executors.newSingleThreadExecutor().submit(executor);
		this.adhocPipelines.put(pipelineName + iterNo, executor);
	}

	/**
	 * Main method for starting up a standalone instance of Sorcerer
	 *
	 * @param args Arguments
	 *
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception{
		BasicConfigurator.configure();
		Sorcerer module = Sorcerer.builder()
				.addConfigPath(args[0])
				.build();
		module.start();
	}

	/**
	 * Get a <i>new</i> builder instance for a Sorcerer module
	 */
	public static SorcererModuleBuilder builder() {
		return new SorcererModuleBuilder();
	}

	/**
	 * Builds and initializes the Sorcerer module
	 *
	 * <p>
	 * The Sorcerer builder provides an API for building a Sorcerer instance
	 * and also takes care of the Sorcerer module initialization. If any
	 * initialization steps fail, a Sorcerer module instance will not be
	 * returned and instead a SorcererException will be thrown. This will
	 * prevent providing an invalid instance of a Sorcerer module.
	 *
	 * </p>
	 */
	public static class SorcererModuleBuilder {

		// List of java packages to scan for Sorcerer object implementations
		private List<String> pkgs = Lists.newArrayList();

		// List of configuration file paths
		private List<String> configs = Lists.newArrayList();

		public SorcererModuleBuilder addPackage(String pkg) {
			pkgs.add(pkg);
			return this;
		}

		public SorcererModuleBuilder addConfigPath(String path) {
			configs.add(path);
			return this;
		}

		public Sorcerer build() throws SorcererException {
			logger.info("Configuring sorcerer module");

			return new Sorcerer(
					SorcererConfiguration.get()
							.addConfigs(configs)
							.addPackages(pkgs)
							.getInjector());
		}
	}

}
