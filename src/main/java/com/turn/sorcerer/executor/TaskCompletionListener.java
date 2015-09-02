/*
 * Copyright (c) 2015, Turn Inc. All Rights Reserved.
 * Use of this source code is governed by a BSD-style license that can be found
 * in the LICENSE file.
 */

package com.turn.sorcerer.executor;

import com.turn.sorcerer.executor.TaskExecutionResult.ExecutionStatus;
import com.turn.sorcerer.pipeline.executable.ExecutablePipeline;
import com.turn.sorcerer.status.Status;
import com.turn.sorcerer.status.StatusManager;
import com.turn.sorcerer.task.type.TaskType;
import com.turn.sorcerer.util.email.Emailer;

import java.util.concurrent.ConcurrentMap;

import com.google.common.util.concurrent.FutureCallback;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Class Description Here
 *
 * @author tshiou
 */
public class TaskCompletionListener implements FutureCallback<TaskExecutionResult> {
	private static final Logger logger =
			LoggerFactory.getLogger(TaskCompletionListener.class);

	private final TaskType task;
	private ConcurrentMap<String, TaskExecutor> runningTasks;
	private ExecutablePipeline pipeline;

	public TaskCompletionListener(TaskType taskType,
	                              ConcurrentMap<String, TaskExecutor> runningTasks,
	                              ExecutablePipeline pipeline) {
		this.task = taskType;
		this.runningTasks = runningTasks;
		this.pipeline = pipeline;

	}

	@Override
	public void onSuccess(TaskExecutionResult executionResult) {
		TaskType t = executionResult.getTaskType();
		ExecutionStatus executionStatus = executionResult.getStatus();
		logger.debug("Entering task callback method {} task={} status={}",
				pipeline, task.getName(), executionStatus);

		if (ExecutionStatus.SUCCESS.equals(executionStatus)) {
			// If success, then update pipeline status
			pipeline.updateTaskAsComplete(t);
		} else if (ExecutionStatus.ABORTED.equals(executionStatus)) {
			logger.info("Task {}:{} aborted", task, pipeline.getId());
		}

		// Update running tasks
		runningTasks.remove(t.getName());
	}

	@Override
	public void onFailure(Throwable throwable) {
		logger.error("{} - {} failed!", pipeline, task.getName(), throwable);
		runningTasks.remove(task.getName());
		StatusManager.get().commitTaskStatus(task, pipeline.getId(), Status.ERROR);

		new Emailer(pipeline + ":" + task.getName() + " failed", new Exception(throwable))
				.send();
	}

}
