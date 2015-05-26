/*
 * Copyright (C) 2015 Turn Inc. All Rights Reserved.
 * Proprietary and confidential.
 */

package com.turn.sorcerer;

import com.turn.sorcerer.config.impl.SorcererConfiguration;
import com.turn.sorcerer.exception.SorcererException;
import com.turn.sorcerer.executor.PipelineScheduler;
import com.turn.sorcerer.injector.SorcererInjector;
import com.turn.sorcerer.pipeline.Pipeline;
import com.turn.sorcerer.pipeline.type.PipelineType;
import com.turn.sorcerer.status.Status;
import com.turn.sorcerer.status.StatusManager;
import com.turn.sorcerer.task.type.TaskType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.inject.BindingAnnotation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Class Description Here
 *
 * @author tshiou
 */
public class Sorcerer {

	private static final Logger logger =
			LogManager.getFormatterLogger(Sorcerer.class);

	private final SorcererInjector injector;

	private Sorcerer(SorcererInjector injector) {
		this.injector = injector;
	}

	private List<ExecutorService> pipelineThreads = Lists.newArrayList();

	public void run() throws SorcererException {
		scheduleAndRunPipeline();
	}

	private void scheduleAndRunPipeline() {

		logger.info("Starting Sorcerer");

		for (PipelineType pipeline: injector.getPipelines()) {

			if (pipeline == null) {
				logger.error("Could not instantiate pipeline.");
				return;
			}

			logger.info("Starting pipeline " + pipeline);

			PipelineScheduler pipelineScheduler = new PipelineScheduler(pipeline);

			ExecutorService pipelineThread = Executors.newSingleThreadExecutor();
			pipelineThread.submit(pipelineScheduler);
			pipelineThreads.add(pipelineThread);
		}

		logger.debug("Exiting Sorcerer startup thread");
	}

	public void stop() {
		for (ExecutorService thread : pipelineThreads) {
			thread.shutdown();
		}
	}

	// Return pipelines in the module
	public Collection<String> getPipelines() {

		Set<PipelineType> pipelines = injector.getPipelines();

		if (pipelines == null) {
			return null;
		}

		List<String> pipelineNames = Lists.newArrayList();
		for (PipelineType p : pipelines) {
			pipelineNames.add(p.getName());
		}

		return pipelineNames;
	}

	public int getCurrentIterNoForPipeline(String pipelineName) {
		Pipeline p = injector.getInstance(injector.getPipelineType(pipelineName));
		return p.getCurrentIterationNumber();
	}

	public Map<String, List<String>> getTasksForPipeline(String pipelineName) {
		PipelineType pipelineType = injector.getPipelineType(pipelineName);

		Map<String, List<String>> map = Maps.newHashMap();

		recursiveBuildGraph(map, pipelineType.getInitTaskName());

		return map;
	}

	private void recursiveBuildGraph(Map<String, List<String>> graph, String taskName) {
		if (taskName == null) {
			return;
		}

		List<String> nextTasks = graph.get(taskName);

		if (nextTasks == null) {
			nextTasks = Lists.newArrayList();
			graph.put(taskName, nextTasks);
		}

		TaskType type = injector.getTaskType(taskName);
		List<String> next = type.getNextTaskNames();

		if (next == null) {
			return;
		}

		nextTasks.addAll(next);

		for (String t : next) {
			recursiveBuildGraph(graph, t);
		}
	}

	public Status getTaskStatus(String taskName, int iterNo) {
		return StatusManager.get().checkTaskStatus(taskName, iterNo);
	}

	public Status getPipelineStatus(String pipelineName, int iterNo) {
		return StatusManager.get().checkPipelineStatus(pipelineName, iterNo);
	}

	public void setTaskStatus(String taskName, int iterNo, Status status) {
		TaskType type = injector.getTaskType(taskName);

		StatusManager.get().commitTaskStatus(type, iterNo, status);
	}

	public void setPipelineStatus(String pipelineName, int iterNo, Status status) {
		PipelineType type = injector.getPipelineType(pipelineName);

		StatusManager.get().commitPipelineStatus(type, iterNo, status);
	}

	public static SorcererModuleBuilder builder() {
		return new SorcererModuleBuilder();
	}

	public static class SorcererModuleBuilder {

		private List<String> pkgs = Lists.newArrayList();
		private List<String> configs = Lists.newArrayList();

		public SorcererModuleBuilder addPackage(String pkg) {
			pkgs.add(pkg);
			return this;
		}

		public SorcererModuleBuilder addConfigPath(String path) {
			configs.add(path);
			return this;
		}

		public Sorcerer create() throws SorcererException {
			return new Sorcerer(
					SorcererConfiguration.get()
							.addConfigs(configs)
							.addPackages(pkgs)
							.getInjector());
		}
	}

	public static void main(String[] args) throws Exception{
		logger.info("Configuring sorcerer module");
		Sorcerer module = Sorcerer.builder()
				.addConfigPath(args[0])
				.create();
		module.run();
	}

	@Retention(RetentionPolicy.RUNTIME)
	@Target({ElementType.FIELD, ElementType.PARAMETER})
	@BindingAnnotation
	public @interface Pipelines {}
}
