/*
 * Copyright (c) 2015, Turn Inc. All Rights Reserved.
 * Use of this source code is governed by a BSD-style license that can be found
 * in the LICENSE file.
 */

package com.turn.sorcerer.pipeline.executable.impl;

import com.turn.sorcerer.injector.SorcererInjector;
import com.turn.sorcerer.pipeline.executable.ExecutablePipeline;
import com.turn.sorcerer.pipeline.type.PipelineType;
import com.turn.sorcerer.status.Status;
import com.turn.sorcerer.status.StatusManager;

import java.util.Map;

import com.google.common.collect.Maps;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Class Description Here
 *
 * @author tshiou
 */
public class ScheduledPipeline extends ExecutablePipeline {

	private static final Logger logger =
			LoggerFactory.getLogger(ScheduledPipeline.class);

	protected ScheduledPipeline(int id, PipelineType t) {
		super(id, t);
	}

	/**
	 * Checks if pipeline is complete
	 *
	 */
	@Override
	public void updatePipelineCompletion() {
		Map<String, Boolean> newTaskCompletionMap = Maps.newConcurrentMap();

		for (String t : taskCompletionMap.keySet()) {
			newTaskCompletionMap.put(t, false);
			if (StatusManager.get().isTaskComplete(
					SorcererInjector.get().getTaskType(t), this.iterationNum)) {
				newTaskCompletionMap.put(t, true);
			}
		}

		taskCompletionMap = newTaskCompletionMap;

		for (boolean completed : taskCompletionMap.values()) {
			if (!completed) {
				return;
			}
		}

		logger.debug(pipelineType.getName() + " for " + getId() + " is complete. ");


		if (StatusManager.get().isPipelineComplete(this.pipelineType, this.iterationNum) == false) {
			logger.info("Committing status for " + pipelineType.getName() + ":" + getId());
			StatusManager.get().commitPipelineStatus(this.pipelineType, this.iterationNum, Status.SUCCESS);
		}

	}

}