/*
 * Copyright (C) 2014 Turn Inc.  All Rights Reserved.
 * Proprietary and confidential.
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

@SorcererTask(name = "test_task_4")
public class TestTask4 extends TestTask {
	public static final String TASK_NAME = TestTask4.class.getSimpleName();

	private static final int TASK_COUNT = 3;

	@Override
	public Collection<Dependency> getDependencies(int iterNo) {
		return ImmutableList.<Dependency>builder()
				.add(new SorcererTaskDependency("test_task_2"))
				.add(new SorcererTaskDependency("test_task_3"))
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
