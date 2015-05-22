/*
 * Copyright (C) 2015 Turn Inc. All Rights Reserved.
 * Proprietary and confidential.
 */

package com.turn.sorcerer.task.impl;

import com.turn.sorcerer.exception.SorcererException;
import com.turn.sorcerer.dependency.Dependency;
import com.turn.sorcerer.task.Context;
import com.turn.sorcerer.task.SorcererTask;
import com.turn.sorcerer.task.Task;

import java.util.List;

/**
 * Class Description Here
 *
 * @author tshiou
 */
@SorcererTask(name = "_default_task")
public class DefaultTask implements Task {
	@Override
	public void init(Context context) {

	}

	@Override
	public void exec(Context context) throws SorcererException {	}

	@Override
	public List<Dependency> getDependencies(int iterNo) {
		return null;
	}
}
