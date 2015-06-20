/*
 * Copyright (c) 2015, Turn Inc. All Rights Reserved.
 * Use of this source code is governed by a BSD-style license that can be found
 * in the LICENSE file.
 */

package com.turn.sorcerer.executor;

import com.turn.sorcerer.injector.SorcererInjector;
import com.turn.sorcerer.pipeline.executable.ExecutablePipeline;
import com.turn.sorcerer.status.Status;
import com.turn.sorcerer.status.StatusManager;
import com.turn.sorcerer.task.type.TaskType;
import com.turn.sorcerer.util.email.Emailer;

import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;

import com.google.common.collect.Sets;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.MoreExecutors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Class Description Here
 *
 * @author tshiou
 */
class TaskScheduler implements Runnable {

	private static final Logger logger =
			LoggerFactory.getLogger(TaskScheduler.class);

	private Map<TaskType, Map<String, String>> taskArgMap;
	private Set<String> runningTasks;

	private final ExecutablePipeline pipeline;
	private final int jobId;

	private boolean ignoreTaskComplete = false;
	private boolean adhoc = false;

	private ListeningExecutorService executionPool;

	public TaskScheduler(ExecutablePipeline pipeline,
	                     int jobId,
	                     int numOfThreads,
	                     Map<TaskType, Map<String, String>> taskArgMap,
	                     boolean adhoc,
	                     boolean overwriteTasks
	) {
		this.pipeline = pipeline;
		this.taskArgMap = taskArgMap;
		this.adhoc = adhoc;
		this.jobId = jobId;
		this.ignoreTaskComplete = overwriteTasks;

		if (numOfThreads > 0) {
			executionPool = MoreExecutors.listeningDecorator(
					Executors.newFixedThreadPool(numOfThreads));
		} else {
			executionPool = MoreExecutors.listeningDecorator(Executors.newCachedThreadPool());
		}
		runningTasks = Sets.newSetFromMap(new ConcurrentHashMap<String, Boolean>());

	}

	@Override
	public void run() {
		logger.debug("Scheduling tasks for {}", pipeline);
		logger.debug("pipeline: {} - Task map size: {}",
				pipeline, pipeline.getTaskGraph().size());

		int submittedTasks = 0;

		graphloop:
		for(Map.Entry<String, Collection<TaskType>> entry :
				pipeline.getTaskGraph().asMap().entrySet()) {

			// Check if all task dependencies are satisfied
			TaskType t = SorcererInjector.get().getTaskType(entry.getKey());

			if (runningTasks.contains(t.getName())) {
				continue;
			}

			if (StatusManager.get().isTaskComplete(t, jobId)) {
				continue;
			}

			// If no task dependencies, run task
			for (TaskType taskDependency : entry.getValue()) {
				if (taskDependency == null) {
					continue;
				}

				if (ignoreTaskComplete) {
					if (pipeline.getTaskCompletionMap()
							.get(taskDependency.getName()) == false) {
						logger.debug("pipeline: {} - dependency for " +
										"{}, {} not complete",
								pipeline,
								t.getName(),
								taskDependency.getName());
						continue graphloop;
					}
				} else {
					if (StatusManager.get().isTaskComplete(taskDependency, jobId) == false) {
						logger.debug("pipeline: {} - dependency for " +
										"{}, {}:{} not complete",
								pipeline,
								t.getName(),
								jobId,
								taskDependency.getName());
						continue graphloop;
					}
				}
			}

			// If adhoc pipeline, then rely on task completion map
			// to check if task should be run again
			if (adhoc && pipeline.getTaskCompletionMap().get(t.getName())) {
				continue;
			}

			// If the task returns that it is currently running, but it isn't in the running task
			// list, we need to resolve it notify job owners
			// This will happen if the PipelineExecutor thread was restarted while the task
			// was still in progress. We don't want to spawn multiple instances of the same
			// Task so return Error. This needs to be resolved manually.
			if (StatusManager.get().isTaskRunning(t, jobId)
					&& runningTasks.contains(t.getName()) == false) {
				logger.warn("{}:{} has a previous iteration running. Exiting",
						t.getName(), jobId);
				new Emailer(t.getName() + ":" + jobId + " has a previous iteration running",
						"This needs to be resolved manually").send();

				// Commit error status
				StatusManager.get().commitTaskStatus(t, jobId, Status.ERROR);
				continue;
			}

			// Submit task for execution
			logger.debug("pipeline:{} - Submitting task {}",
					pipeline.name(), t.getName());

			TaskExecutor executor = new TaskExecutor(t, jobId, taskArgMap.get(t), adhoc);
			ListenableFuture<TaskExecutionResult> future = executionPool.submit(executor);
			runningTasks.add(t.getName());
			TaskCompletionListener callback = new TaskCompletionListener(t, runningTasks, pipeline);
			Futures.addCallback(future, callback);
			submittedTasks++;
		}

		logger.debug("pipeline:{} - Submitted {} tasks",
				pipeline, submittedTasks);
	}

	public void abort() {
		logger.debug("Shutting down pipeline executor for {}", pipeline);
		executionPool.shutdown();
	}
}
