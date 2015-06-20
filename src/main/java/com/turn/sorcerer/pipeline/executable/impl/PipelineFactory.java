/*
 * Copyright (c) 2015, Turn Inc. All Rights Reserved.
 * Use of this source code is governed by a BSD-style license that can be found
 * in the LICENSE file.
 */

package com.turn.sorcerer.pipeline.executable.impl;

import com.turn.sorcerer.injector.SorcererInjector;
import com.turn.sorcerer.pipeline.Pipeline;
import com.turn.sorcerer.pipeline.executable.ExecutablePipeline;
import com.turn.sorcerer.pipeline.impl.CronPipeline;
import com.turn.sorcerer.pipeline.impl.DefaultPipeline;
import com.turn.sorcerer.pipeline.type.PipelineType;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Class Description Here
 *
 * @author tshiou
 */
public class PipelineFactory {
	private static final Logger logger =
			LoggerFactory.getLogger(PipelineFactory.class);

	private static PipelineFactory INSTANCE = new PipelineFactory();

	public static PipelineFactory get() {
		return INSTANCE;
	}

	private Table<String, Integer, ExecutablePipeline> pipelineInstances = HashBasedTable.create();

	private PipelineFactory() {

	}

	public Pipeline getPipeline(PipelineType type) {

		if (type.getCronString() != null || type.getCronString().length() > 0) {
			return new CronPipeline(type.getCronString());
		}

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
