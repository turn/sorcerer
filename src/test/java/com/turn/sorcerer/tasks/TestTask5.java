/*
 * Copyright (c) 2015, Turn Inc. All Rights Reserved.
 * Use of this source code is governed by a BSD-style license that can be found
 * in the LICENSE file.
 */

package com.turn.sorcerer.tasks;

import com.turn.sorcerer.dependency.Dependency;
import com.turn.sorcerer.dependency.impl.SorcererTaskDependency;
import com.turn.sorcerer.task.SorcererTask;

import java.util.Collection;

import com.google.common.collect.ImmutableList;

/**
 * Test task for audience workflow scheduler. Specifically a singleton task.
 *
 * @author tshiou
 */
@SorcererTask(name = "test_task_5")
public class TestTask5 extends TestTask {
	public static final String TASK_NAME = TestTask5.class.getSimpleName();

	private static final int TASK_COUNT = 8;

	@Override
	public Collection<Dependency> getDependencies(int iterNo) {
		return ImmutableList.<Dependency>builder()
				.add(new SorcererTaskDependency("test_task_4"))
				.build();
	}

	@Override
	public String name() {
		return TASK_NAME;
	}

	@Override
	protected int getTaskCount() {
		return TASK_COUNT;
	}
}
