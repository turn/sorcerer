/*
 * Copyright (C) 2015 Turn Inc. All Rights Reserved.
 * Proprietary and confidential.
 */

package com.turn.sorcerer.dependency.impl;

import com.turn.sorcerer.exception.SorcererException;
import com.turn.sorcerer.dependency.Dependency;
import com.turn.sorcerer.injector.SorcererInjector;
import com.turn.sorcerer.status.StatusManager;
import com.turn.sorcerer.task.SorcererTask;
import com.turn.sorcerer.task.Task;
import com.turn.sorcerer.task.type.TaskType;

/**
 * Class Description Here
 *
 * @author tshiou
 */
public class SorcererTaskDependency implements Dependency {

	private final TaskType task;
	private Integer customIterNo = null;

	public SorcererTaskDependency(TaskType type) {
		this.task = type;
	}

	public SorcererTaskDependency(TaskType type, int iterNo) {
		this(type);
		this.customIterNo = iterNo;
	}

	public SorcererTaskDependency(Class<? extends Task> taskClass)
			throws SorcererException {

		SorcererTask anno = taskClass.getAnnotation(SorcererTask.class);
		if (anno == null) {
			throw new SorcererException("Annotation not found on " + taskClass.toString());
		}

		String taskName = anno.name();
		if (taskName == null) {
			throw new SorcererException("No taskName found in annotation on " +
					taskClass.toString());
		}

		this.task = SorcererInjector.get().getTaskType(taskName);
	}

	public SorcererTaskDependency(Class<? extends Task> taskClass, int iterNo)
			throws SorcererException {
		this(taskClass);
		this.customIterNo = iterNo;
	}

	public SorcererTaskDependency(String taskName) {
		this.task = SorcererInjector.get().getTaskType(taskName);
	}

	public SorcererTaskDependency(String taskName, int iterNo) {
		this(taskName);
		this.customIterNo = iterNo;
	}

	@Override
	public boolean check(int iterNo) {
		return StatusManager.get().isTaskComplete(
				task, customIterNo == null ? iterNo : customIterNo);
	}
}
