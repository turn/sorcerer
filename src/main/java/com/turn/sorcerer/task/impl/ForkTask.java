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
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Class Description Here
 *
 * @author tshiou
 */
public class ForkTask implements Task {
	private static final Logger logger =
			LogManager.getLogger(ForkTask.class);

	private final TaskType type;

	public ForkTask(TaskType type) {
		this.type = type;
	}

	@Override
	public void init(Context context) {

	}

	@Override
	public void exec(Context context) throws SorcererException {
		logger.info("Task " + type.getName() + " forking tasks " + type.getNextTaskNames());
	}

	@Override
	public List<Dependency> getDependencies(int iterNo) {
		return Lists.newArrayList();
	}


}
