/*
 * Copyright (c) 2015, Turn Inc. All Rights Reserved.
 * Use of this source code is governed by a BSD-style license that can be found
 * in the LICENSE file.
 */

package com.turn.sorcerer.executor;

import com.turn.sorcerer.exception.SorcererException;
import com.turn.sorcerer.executor.TaskExecutionResult.ExecutionStatus;
import com.turn.sorcerer.metrics.MetricUnit;
import com.turn.sorcerer.metrics.MetricsMonitor;
import com.turn.sorcerer.status.Status;
import com.turn.sorcerer.status.StatusManager;
import com.turn.sorcerer.task.Context;
import com.turn.sorcerer.task.executable.ExecutableTask;
import com.turn.sorcerer.task.executable.TaskFactory;
import com.turn.sorcerer.task.type.TaskType;
import com.turn.sorcerer.util.Constants;
import com.turn.sorcerer.util.TypedDictionary;

import java.text.SimpleDateFormat;
import java.util.Map;
import java.util.concurrent.Callable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Class Description Here
 *
 * @author tshiou
 */
public class TaskExecutor implements Callable<TaskExecutionResult>, Abortable {
	private static final Logger LOGGER =
			LoggerFactory.getLogger(TaskExecutor.class);

	private final TaskType taskType;
	private boolean adhoc = false;

	// dashboard monitor keys for every task
	public static final String TASK_START_TIME_METRIC = "Task_Start_Time";
	public static final String COMPLETION_TIME_METRIC = "Task_Completion_Time";
	public static final String SUCCESSFUL_FINISH_TIME_METRIC = "Successful_Finish_Time";

	protected long taskStartTime = 0;
	protected long taskFinishTime = 0;

	private ExecutableTask task = null;
	private int jobId;
	private boolean abortSignalReceived = false;
	protected final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy_MM_dd");

	private TypedDictionary taskArgs;

	public TaskExecutor(TaskType t, int jobId, Map<String, String> taskArgs, boolean adhoc) {
		this.taskType = t;

		this.taskArgs = new TypedDictionary();
		this.taskArgs.putAll(taskArgs);
		this.taskArgs.put(Constants.ADHOC, adhoc);
		this.jobId = jobId;
		this.adhoc = adhoc;
	}

	@Override
	public TaskExecutionResult call() throws Exception {

		LOGGER.debug("Attempting to start task {}", taskType.getName());

		TaskExecutionResult status = new TaskExecutionResult();
		status.setTask(taskType);
		status.setStatus(TaskExecutionResult.ExecutionStatus.SUCCESS);

		// Get instance of task
		this.task = TaskFactory.get().getExecutableTask(this.taskType, this.jobId);

		Context context = new Context(jobId, taskArgs);

		// Normal (non-adhoc) pipeline checks
		if (!adhoc) {
			// Skip if task is disabled.
			if (!taskType.isEnabled()) {
				status.setStatus(ExecutionStatus.DISABLED);
				LOGGER.debug("{} is disabled. Exiting", task.name());
				return status;
			}

			// If another iteration of task is running, return an error
			if (task.isRunning()) {
				status.setStatus(ExecutionStatus.RUNNING);
				LOGGER.debug("{} is already running for job id {}. Exiting",
						task.name(), jobId);
				return status;
			}

			// Skip if task is already completed
			if (task.isCompleted()) {
				status.setStatus(ExecutionStatus.COMPLETED);
				LOGGER.debug("{} has already run successfully for job id {}. Exiting",
						task.name(), jobId);
				return status;
			}

			// Skip task if previous error has not been cleared
			if (task.hasError()) {
				status.setStatus(ExecutionStatus.ERROR);
				LOGGER.warn("{} has a previous uncleared error. Exiting", task);
				return status;
			}
		}

		// Parameterize the task
		LOGGER.debug("Parameterizing {}", task.name());
		task.initialize(context);

		if (!adhoc) {
			// Skip if dependencies are not met
			if (!task.checkDependencies()) {
				status.setStatus(ExecutionStatus.DEPENDENCY_FAILURE);
				LOGGER.debug("{} dependencies are not met. Exiting", task);
				return status;
			}
		}

		// Execute task
		try {
			LOGGER.info("Executing Task {}", task.name());
			taskStartTime = System.currentTimeMillis();
			MetricUnit unit = MetricUnit.getMetricUnit(false, task.name(), TASK_START_TIME_METRIC);
			MetricsMonitor.getInstance().addGenericMetric(unit, taskStartTime);

			StatusManager.get().commitTaskStatus(taskType, jobId, Status.IN_PROGRESS);
			task.execute(context);

			taskFinishTime = System.currentTimeMillis();
			updateMetrics(task.name());

		} catch (SorcererException e) {
			status.setStatus(ExecutionStatus.ERROR);
			LOGGER.error(task.name() + " failed", e);
			StatusManager.get().commitTaskStatus(taskType, jobId, Status.ERROR);

			throw e;
		} finally {
			StatusManager.get().removeInProgressTaskStatus(taskType, jobId);
		}

		if (abortSignalReceived) {
			status.setStatus(ExecutionStatus.ABORTED);
		} else {
			StatusManager.get().commitTaskStatus(taskType, jobId, Status.SUCCESS, true);
		}

		return status;
	}

	private void updateMetrics(String taskName) {
		boolean graphiteOnly = false;
		MetricUnit unit = MetricUnit.getMetricUnit(graphiteOnly, taskName, SUCCESSFUL_FINISH_TIME_METRIC);
		MetricsMonitor.getInstance().addSuccessMetric(unit, jobId);
		unit = MetricUnit.getMetricUnit(graphiteOnly, taskName, COMPLETION_TIME_METRIC);
		MetricsMonitor.getInstance().addGenericMetric(unit, taskFinishTime - taskStartTime);
	}

	/**
	 * Attempt to gracefully abort task
	 */
	public void abort() {
		this.abortSignalReceived = true;
		LOGGER.info("Abort signal received, attempting to abort {}", task.toString());
		if (this.task != null) {
			this.task.abort();
		}
	}
}
