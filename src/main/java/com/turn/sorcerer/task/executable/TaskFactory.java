/*
 * Copyright (C) 2015 Turn Inc. All Rights Reserved.
 * Proprietary and confidential.
 */

package com.turn.sorcerer.task.executable;

import com.turn.sorcerer.injector.SorcererInjector;
import com.turn.sorcerer.task.Task;
import com.turn.sorcerer.task.impl.ForkTask;
import com.turn.sorcerer.task.impl.JoinTask;
import com.turn.sorcerer.task.type.TaskType;

/**
 * Class Description Here
 *
 * @author tshiou
 */
public class TaskFactory {

	private static TaskFactory INSTANCE = new TaskFactory();

	public static TaskFactory get() {
		return INSTANCE;
	}

	private TaskFactory() {

	}

	private Task getTask(TaskType type) {
		if (TaskType.FORK_EXEC.equals(type.getExec())) {
			return new ForkTask(type);
		}

		if (TaskType.JOIN_EXEC.equals(type.getExec())) {
			return new JoinTask(type.getName());
		}

		return SorcererInjector.get().getInstance(type);

	}

	public ExecutableTask getExecutableTask(TaskType type, int seq) {

		return new ExecutableTask(type, getTask(type), seq);
	}

}
