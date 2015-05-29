/*
 * Copyright (c) 2015, Turn Inc. All Rights Reserved.
 * Use of this source code is governed by a BSD-style license that can be found
 * in the LICENSE file.
 */

package com.turn.sorcerer.pipeline.executable.impl;

import com.turn.sorcerer.injector.SorcererInjector;
import com.turn.sorcerer.pipeline.executable.ExecutablePipeline;
import com.turn.sorcerer.pipeline.type.PipelineType;
import com.turn.sorcerer.status.StatusManager;
import com.turn.sorcerer.task.type.TaskType;

/**
 * Class Description Here
 *
 * @author tshiou
 */
public class AdhocPipeline extends ExecutablePipeline {

	private final boolean overwriteTasks;
	private final TaskType initTask;

	public AdhocPipeline(int id, PipelineType pipelineType, boolean overwriteTasks) {
		super(id, pipelineType);
		this.overwriteTasks = overwriteTasks;
		this.initTask = SorcererInjector.get().getTaskType(pipelineType.getInitTaskName());
	}

	/**
	 * Checks if pipeline is complete
	 *
	 */
	@Override
	public void updatePipelineCompletion() {
		if (overwriteTasks == false) {
			return;
		}

		// Check task completion
		for (String t : taskCompletionMap.keySet()) {
			if (StatusManager.get().isTaskComplete(
					SorcererInjector.get().getTaskType(t), this.iterationNum)) {
				taskCompletionMap.put(t, true);
			}
		}
	}

	@Override
	public boolean isCompleted() {
		for (boolean completed : taskCompletionMap.values()) {
			if (completed == false) {
				return false;
			}
		}
		return true;
	}

	@Override
	public String toString() {
		return pipelineType.getName() + ":" + initTask.getName() + ":" + this.iterationNum;
	}
}