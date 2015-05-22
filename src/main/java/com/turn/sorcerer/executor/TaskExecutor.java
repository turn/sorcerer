/*
 * Copyright (C) 2015 Turn Inc. All Rights Reserved.
 * Proprietary and confidential.
 */

package com.turn.sorcerer.executor;

import com.turn.sorcerer.exception.SorcererException;
import com.turn.sorcerer.executor.TaskExecutionResult.ExecutionStatus;
import com.turn.sorcerer.metrics.MetricUnit;
import com.turn.sorcerer.metrics.MetricsMonitor;
import com.turn.sorcerer.task.Context;
import com.turn.sorcerer.task.type.TaskType;
import com.turn.sorcerer.task.executable.ExecutableTask;
import com.turn.sorcerer.task.executable.TaskFactory;

import java.text.SimpleDateFormat;
import java.util.concurrent.Callable;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Class Description Here
 *
 * @author tshiou
 */
public class TaskExecutor implements Callable<TaskExecutionResult> {
	private static final Logger LOGGER =
			LogManager.getFormatterLogger(TaskExecutor.class);

	private final TaskType taskType;
	private boolean adhoc = false;

	// dashboard monitor keys for every task
	public static final String TASK_START_TIME_METRIC = "Task_Start_Time";
	public static final String COMPLETION_TIME_METRIC = "Task_Completion_Time";
	public static final String SUCCESSFUL_FINISH_TIME_METRIC = "Successful_Finish_Time";

	protected long taskStartTime = 0;
	protected long taskFinishTime = 0;

	private int jobId;
	protected final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy_MM_dd");

	private String[] taskArgs;

	public TaskExecutor(TaskType t, int jobId, String[] taskArgs, boolean adhoc) {
		this.taskType = t;
		if (taskArgs != null) {
			this.taskArgs = taskArgs.clone();
		}
		this.jobId = jobId;
		this.adhoc = adhoc;
	}

	@Override
	public TaskExecutionResult call() throws Exception {

		LOGGER.debug("Attempting to start task %s", taskType.getName());

		TaskExecutionResult status = new TaskExecutionResult();
		status.setTask(taskType);
		status.setStatus(TaskExecutionResult.ExecutionStatus.SUCCESS);
		status.setThrowable(null);

		// Get instance of task
		ExecutableTask task = TaskFactory.get().getExecutableTask(this.taskType, this.jobId);

		// Parameterize the task
		task.parameterize(taskArgs);

		// Normal (non-adhoc) pipeline checks
		if (!adhoc) {
			// Skip if task is disabled.
			if (!taskType.isEnabled()) {
				status.setStatus(ExecutionStatus.DISABLED);
				LOGGER.debug("%s is disabled. Exiting", task.name());
				return status;
			}

			// Skip if task is already completed
			if (task.isCompleted()) {
				status.setStatus(ExecutionStatus.COMPLETED);
				LOGGER.debug("%s has already run successfully for job id %s. Exiting",
						task.name(), jobId);
				return status;
			}

			// Skip task if previous error has not been cleared
			if (task.hasError()) {
				status.setStatus(ExecutionStatus.ERROR);
				LOGGER.warn("%s has a previous uncleared error. Exiting", task.name());
				return status;
			}

			// Skip if dependencies are not met
			if (!task.checkDependencies()) {
				status.setStatus(ExecutionStatus.DEPENDENCY_FAILURE);
				LOGGER.debug("%s dependencies are not met. Exiting", task.name());
				return status;
			}

			// If another iteration of task is running, return an error
			if (task.isRunning()) {
				status.setStatus(ExecutionStatus.RUNNING);
				return status;
			}
		}

		// Execute task
		try {
			LOGGER.info("Executing %s", task.name());
			taskStartTime = System.currentTimeMillis();
			MetricUnit unit = MetricUnit.getMetricUnit(false, task.name(), TASK_START_TIME_METRIC);
			MetricsMonitor.getInstance().addGenericMetric(unit, taskStartTime);

			Context context = new Context(jobId);
			task.execute(context);

			taskFinishTime = System.currentTimeMillis();
			updateMetrics(task.name());

		} catch (SorcererException e) {
			status.setStatus(ExecutionStatus.ERROR);
			LOGGER.error(task.name() + "failed", e);

			throw e;
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
}
