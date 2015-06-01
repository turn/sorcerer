/*
 * Copyright (c) 2015, Turn Inc. All Rights Reserved.
 * Use of this source code is governed by a BSD-style license that can be found
 * in the LICENSE file.
 */

package com.turn.sorcerer.tasks;

import com.turn.sorcerer.task.SorcererTask;

/**
 * Test task for audience workflow scheduler
 *
 * @author tshiou
 */
@SorcererTask(name = "test_task_2")
public class TestTask2 extends TestTask {
	public static final String TASK_NAME = TestTask2.class.getSimpleName();

	private static final int TASK_COUNT = 9;

	@Override
	public String name() {
		return TASK_NAME;
	}

	@Override
	protected int getTaskCount() {
		return TASK_COUNT;
	}
}
