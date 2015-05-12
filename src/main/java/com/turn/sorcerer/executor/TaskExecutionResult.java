/*
 * Copyright (C) 2015 Turn Inc. All Rights Reserved.
 * Proprietary and confidential.
 */

package com.turn.sorcerer.executor;

import com.turn.sorcerer.task.type.TaskType;

/**
 * Class Description Here
 *
 * @author tshiou
 */
public class TaskExecutionResult {

	// Task execution result status
	public enum ExecutionStatus {
		DISABLED,
		SUCCESS,
		COMPLETED,
		DEPENDENCY_FAILURE,
		RUNNING,
		ERROR
	}

	private TaskType taskType;
	private ExecutionStatus status;
	private Throwable throwable;

	public void setTask(TaskType taskType) {
		this.taskType = taskType;
	}

	public void setStatus(ExecutionStatus status) {
		this.status = status;
	}

	public void setThrowable(Throwable throwable) {
		this.throwable = throwable;
	}

	public TaskType getTaskType() {
		return taskType;
	}

	public ExecutionStatus getStatus() {
		return status;
	}

	public Throwable getThrowable() {
		return throwable;
	}
}
