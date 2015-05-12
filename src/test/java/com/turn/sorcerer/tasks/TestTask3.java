/*
 * Copyright (C) 2014 Turn Inc.  All Rights Reserved.
 * Proprietary and confidential.
 */

package com.turn.sorcerer.tasks;

import com.turn.sorcerer.task.SorcererTask;

/**
 * Test task for audience workflow scheduler
 *
 * @author tshiou
 */

@SorcererTask(name = "test_task_3")
public class TestTask3 extends TestTask {
	public static final String TASK_NAME = TestTask3.class.getSimpleName();

	private static final int TASK_COUNT = 2;

	@Override
	public String name() {
		return TASK_NAME;
	}

	@Override
	protected int getTaskCount() {
		return TASK_COUNT;
	}
}
