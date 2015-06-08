/*
 * Copyright (c) 2015, Turn Inc. All Rights Reserved.
 * Use of this source code is governed by a BSD-style license that can be found
 * in the LICENSE file.
 */

package com.turn.sorcerer.tasks;

import com.turn.sorcerer.dependency.Dependency;
import com.turn.sorcerer.task.Context;
import com.turn.sorcerer.task.Task;

import java.util.Collection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Base task class for sorcerer workflow scheduler testing
 *
 * @author tshiou
 */
public abstract class TestTask implements Task {
	private static final Logger logger =
			LoggerFactory.getLogger(TestTask.class);

	@Override
	public void init(Context context) {}

	@Override
	public void exec(Context context) throws Exception {
		for (int i = getTaskCount(); i >= 0 ; i--) {
			logger.info(name() + " count " + i + " for sequence number " + context.getIterationNumber());
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	protected abstract String name();

	protected abstract int getTaskCount();

	@Override
	public Collection<Dependency> getDependencies(int iterNo) {
		return null;
	}
}
