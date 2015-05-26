/*
 * Copyright (C) 2015 Turn Inc. All Rights Reserved.
 * Proprietary and confidential.
 */

package com.turn.sorcerer.pipeline.executable.impl;

import com.turn.sorcerer.injector.SorcererInjector;
import com.turn.sorcerer.pipeline.Pipeline;
import com.turn.sorcerer.pipeline.executable.ExecutablePipeline;
import com.turn.sorcerer.pipeline.impl.DefaultPipeline;
import com.turn.sorcerer.pipeline.type.PipelineType;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Class Description Here
 *
 * @author tshiou
 */
public class PipelineFactory {
	private static final Logger logger =
			LogManager.getFormatterLogger(PipelineFactory.class);

	private static PipelineFactory INSTANCE = new PipelineFactory();

	public static PipelineFactory get() {
		return INSTANCE;
	}

	private Table<String, Integer, ExecutablePipeline> pipelineInstances = HashBasedTable.create();

	private PipelineFactory() {

	}

	public Pipeline getPipeline(PipelineType type) {

		if (SorcererInjector.get().bindingExists(type)) {
			return SorcererInjector.get().getInstance(type);
		}

		return new DefaultPipeline(type);
	}

	public ExecutablePipeline getExecutablePipeline(PipelineType type, int seq) {
		ExecutablePipeline p = pipelineInstances.get(type.getName(), seq);

		if (p == null) {
			p = new ScheduledPipeline(seq, type);
			pipelineInstances.put(type.getName(), seq, p);
		}

		return p;
	}

	public void expireSequenceNumber(PipelineType type, int seq) {
		ExecutablePipeline p = pipelineInstances.get(type, seq);

		if (p == null) {
			return;
		}

		pipelineInstances.remove(type, seq);
	}

}
