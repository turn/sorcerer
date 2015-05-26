/*
 * Copyright (C) 2015 Turn Inc. All Rights Reserved.
 * Proprietary and confidential.
 */

package com.turn.sorcerer.pipeline.executable;

import com.turn.sorcerer.injector.SorcererInjector;
import com.turn.sorcerer.pipeline.type.PipelineType;
import com.turn.sorcerer.status.Status;
import com.turn.sorcerer.status.StatusManager;
import com.turn.sorcerer.task.type.TaskType;

import java.util.Map;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Maps;
import com.google.common.collect.SetMultimap;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Class Description Here
 *
 * @author tshiou
 */
public abstract class ExecutablePipeline {
	private final Logger logger =
			LogManager.getFormatterLogger(ExecutablePipeline.class);
	protected final int iterationNum;
	protected final PipelineType pipelineType;

	protected Map<String, Boolean> taskCompletionMap;

	// Map representation of workflow graph
	protected SetMultimap<String, TaskType> taskGraph;

	// This should never be used
	private ExecutablePipeline() {
		this.iterationNum = -1;
		this.pipelineType = null;
	}

	public ExecutablePipeline(int id, PipelineType pipelineType) {
		this.iterationNum = id;
		this.pipelineType = pipelineType;

		TaskType initTask = SorcererInjector.get().getTaskType(pipelineType.getInitTaskName());

		if (taskCompletionMap == null) {
			taskCompletionMap = Maps.newConcurrentMap();
		}
		if (taskGraph == null) {
			taskGraph = HashMultimap.create();
			taskGraph.put(initTask.getName(), null);
		}

		// Initialize list of tasks in pipeline
		initTask(initTask);
		updatePipelineCompletion();
	}

	public boolean isEnabled() {
		return true;
	}

	public String name() {
		return pipelineType.getName();
	}

	public int getId() {
		return iterationNum;
	}

	// recursive init task
	private void initTask(TaskType task) {

		if (task == null) {
			return;
		}

		taskCompletionMap.put(task.getName(), false);
//		taskGraph.put(task, null);

		if (task.getNextTaskNames() == null) {
			return;
		}

		for (String nextTaskName : task.getNextTaskNames()) {

			if (nextTaskName == null) {
				continue;
			}

			TaskType nextTask = SorcererInjector.get().getTaskType(nextTaskName);
			taskGraph.put(nextTask.getName(), task);

			initTask(nextTask);
		}
	}

	public void updateTaskAsComplete(TaskType task) {
		taskCompletionMap.put(task.getName(), true);
		updatePipelineCompletion();
	}

	public boolean isCompleted() {
		return StatusManager.get().isPipelineComplete(this.pipelineType, this.iterationNum);
	}

	/**
	 * Commits status
	 *
	 * <p>
	 * Commits status of {@link Status} type.
	 * If status {@link Status#PENDING}, then any existing status is cleared
	 * </p>
	 *
	 * @param status {@link Status}
	 */
	public void commitStatus(Status status) {

		if (Status.PENDING.equals(status)) {
			StatusManager.get().clearPipelineStatus(this.pipelineType, this.iterationNum);
		} else {
			StatusManager.get().commitPipelineStatus(this.pipelineType, this.iterationNum, status);
		}

	}

	public Map<String, Boolean> getTaskCompletionMap() {
		return taskCompletionMap;
	}

	public SetMultimap<String, TaskType> getTaskGraph() {
		return taskGraph;
	}

	public String toString() {
		return pipelineType.getName() + "-" + iterationNum;
	}

	public abstract void updatePipelineCompletion();
}

