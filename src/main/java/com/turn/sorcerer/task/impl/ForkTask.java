/*
 * Copyright (c) 2015, Turn Inc. All Rights Reserved.
 * Use of this source code is governed by a BSD-style license that can be found
 * in the LICENSE file.
 */

package com.turn.sorcerer.task.impl;

import com.turn.sorcerer.dependency.Dependency;
import com.turn.sorcerer.exception.SorcererException;
import com.turn.sorcerer.task.Context;
import com.turn.sorcerer.task.Task;
import com.turn.sorcerer.task.type.TaskType;

import java.util.List;

import com.google.common.collect.Lists;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Implements Task to fork next tasks in workflow
 *
 * @author tshiou
 */
public class ForkTask implements Task {
	private static final Logger logger =
			LoggerFactory.getLogger(ForkTask.class);

	private final TaskType type;

	public ForkTask(TaskType type) {
		this.type = type;
	}

	@Override
	public void init(Context context) {

	}

	@Override
	public void exec(Context context) throws SorcererException {
		logger.info("Fork task {}:{} forking tasks {}",
				type.getName(), context.getIterationNumber(), type.getNextTaskNames());
	}

	@Override
	public List<Dependency> getDependencies(int iterNo) {
		return Lists.newArrayList();
	}

	@Override
	public void abort() {
		// do nothing
	}
}
