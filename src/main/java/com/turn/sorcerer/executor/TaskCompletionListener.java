/*
 * Copyright (c) 2015, Turn Inc. All Rights Reserved.
 * Use of this source code is governed by a BSD-style license that can be found
 * in the LICENSE file.
 */

package com.turn.sorcerer.executor;

import com.turn.sorcerer.executor.TaskExecutionResult.ExecutionStatus;
import com.turn.sorcerer.pipeline.executable.ExecutablePipeline;
import com.turn.sorcerer.task.type.TaskType;
import com.turn.sorcerer.util.email.Emailer;

import java.util.Set;

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
	private Set<String> runningTasks;
	private ExecutablePipeline pipeline;

	public TaskCompletionListener(TaskType taskType,
	                              Set<String> runningTasks,
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
		}

		// Update running tasks
		runningTasks.remove(t.getName());
	}

	@Override
	public void onFailure(Throwable throwable) {
		logger.error("{} - {} failed!", pipeline, task.getName(), throwable);
		runningTasks.remove(task.getName());

		new Emailer(task.getName() + " failed", new Exception(throwable)).send();
	}

}
