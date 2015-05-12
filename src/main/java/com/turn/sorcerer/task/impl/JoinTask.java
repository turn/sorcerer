/*
 * Copyright (C) 2015 Turn Inc. All Rights Reserved.
 * Proprietary and confidential.
 */

package com.turn.sorcerer.task.impl;

import com.turn.sorcerer.exception.SorcererException;
import com.turn.sorcerer.dependency.Dependency;
import com.turn.sorcerer.task.Context;
import com.turn.sorcerer.task.Task;

import java.util.List;

import com.google.common.collect.Lists;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Class Description Here
 *
 * @author tshiou
 */
public class JoinTask implements Task {
	private static final Logger logger =
			LogManager.getLogger(JoinTask.class);

	private final String name;

	public JoinTask(String name) {
		this.name = name;
	}

	@Override
	public void exec(Context context) throws SorcererException {
		logger.info("Placeholder join task " + name);
	}

	@Override
	public List<Dependency> getDependencies(int iterNo) {
		return Lists.newArrayList();
	}
}
