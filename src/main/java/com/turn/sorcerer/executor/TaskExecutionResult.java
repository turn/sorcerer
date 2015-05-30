/*
 * Copyright (c) 2015, Turn Inc. All Rights Reserved.
 * Use of this source code is governed by a BSD-style license that can be found
 * in the LICENSE file.
 */

package com.turn.sorcerer.executor;

import com.turn.sorcerer.metrics.MetricUnit;
import com.turn.sorcerer.task.type.TaskType;

import java.util.List;

import com.google.common.collect.Lists;

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
	private List<MetricUnit> metrics = Lists.newArrayList();

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

	public void addMetric(MetricUnit metric) {
		this.metrics.add(metric);
	}
}
