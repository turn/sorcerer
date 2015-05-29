/*
 * Copyright (c) 2015, Turn Inc. All Rights Reserved.
 * Use of this source code is governed by a BSD-style license that can be found
 * in the LICENSE file.
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

	public void setTask(TaskType taskType) {
		this.taskType = taskType;
	}

	public void setStatus(ExecutionStatus status) {
		this.status = status;
	}

	public TaskType getTaskType() {
		return taskType;
	}

	public ExecutionStatus getStatus() {
		return status;
	}
}
