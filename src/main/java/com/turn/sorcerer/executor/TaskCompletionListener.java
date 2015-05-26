/*
 * Copyright (C) 2015 Turn Inc. All Rights Reserved.
 * Proprietary and confidential.
 */

package com.turn.sorcerer.executor;

import com.turn.sorcerer.executor.TaskExecutionResult.ExecutionStatus;
import com.turn.sorcerer.pipeline.executable.ExecutablePipeline;
import com.turn.sorcerer.status.Status;
import com.turn.sorcerer.status.StatusManager;
import com.turn.sorcerer.task.type.TaskType;
import com.turn.sorcerer.util.email.Emailer;

import java.util.Set;

import com.google.common.util.concurrent.FutureCallback;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


/**
 * Class Description Here
 *
 * @author tshiou
 */
public class TaskCompletionListener implements FutureCallback<TaskExecutionResult> {
	private static final Logger logger =
			LogManager.getLogger(TaskCompletionListener.class);

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
		logger.debug("Entering task callback method " + pipeline + " task="
				+ t.getName() + " status=" + executionStatus);


		if (ExecutionStatus.SUCCESS.equals(executionStatus)) {
			// If success, then update pipeline status
			pipeline.updateTaskAsComplete(t);
			StatusManager.get().commitTaskStatus(t, pipeline.getId(), Status.SUCCESS, true);
		}

		// Update running tasks
		runningTasks.remove(t.getName());
	}

	@Override
	public void onFailure(Throwable throwable) {
		logger.debug(pipeline + " - " + task.getName() + " failed!");
		logger.error(new Exception(throwable));
		StatusManager.get().commitTaskStatus(task, pipeline.getId(), Status.ERROR);
		StatusManager.get().removeInProgressTaskStatus(task, pipeline.getId());
		runningTasks.remove(task.getName());

		new Emailer(task.getName() + " failed", new Exception(throwable)).send();
	}
}
