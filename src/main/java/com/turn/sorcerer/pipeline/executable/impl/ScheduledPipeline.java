/*
 * Copyright (C) 2015 Turn Inc. All Rights Reserved.
 * Proprietary and confidential.
 */

package com.turn.sorcerer.pipeline.executable.impl;

import com.turn.sorcerer.injector.SorcererInjector;
import com.turn.sorcerer.pipeline.executable.ExecutablePipeline;
import com.turn.sorcerer.pipeline.type.PipelineType;
import com.turn.sorcerer.status.Status;
import com.turn.sorcerer.status.StatusManager;

import java.util.Map;

import com.google.common.collect.Maps;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Class Description Here
 *
 * @author tshiou
 */
public class ScheduledPipeline extends ExecutablePipeline {

	private static final Logger logger =
			LogManager.getFormatterLogger(ScheduledPipeline.class);

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