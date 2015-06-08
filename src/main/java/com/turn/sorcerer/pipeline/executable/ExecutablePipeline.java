/*
 * Copyright (c) 2015, Turn Inc. All Rights Reserved.
 * Use of this source code is governed by a BSD-style license that can be found
 * in the LICENSE file.
 */

package com.turn.sorcerer.pipeline.executable;

import com.turn.sorcerer.injector.SorcererInjector;
import com.turn.sorcerer.pipeline.type.PipelineType;
import com.turn.sorcerer.status.StatusManager;
import com.turn.sorcerer.task.type.TaskType;

import java.util.Map;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Maps;
import com.google.common.collect.SetMultimap;

/**
 * Class Description Here
 *
 * @author tshiou
 */
public abstract class ExecutablePipeline {
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

	protected ExecutablePipeline(int id, PipelineType pipelineType) {
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

	public Map<String, Boolean> getTaskCompletionMap() {
		return taskCompletionMap;
	}

	public SetMultimap<String, TaskType> getTaskGraph() {
		return taskGraph;
	}

	public String toString() {
		return pipelineType.getName() + ":" + iterationNum;
	}

	public abstract void updatePipelineCompletion();
}

