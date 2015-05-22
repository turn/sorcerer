/*
 * Copyright (C) 2015 Turn Inc. All Rights Reserved.
 * Proprietary and confidential.
 */

package com.turn.sorcerer.dependency.impl;

import com.turn.sorcerer.dependency.Dependency;
import com.turn.sorcerer.injector.SorcererInjector;
import com.turn.sorcerer.status.StatusManager;
import com.turn.sorcerer.task.SorcererTask;
import com.turn.sorcerer.task.Task;
import com.turn.sorcerer.task.type.TaskType;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Class Description Here
 *
 * @author tshiou
 */
public class SorcererTaskDependency implements Dependency {

	private static final Logger logger =
			LogManager.getFormatterLogger(SorcererTaskDependency.class);


	private final TaskType task;
	private Integer customIterNo = null;

	public SorcererTaskDependency(TaskType type) {
		this.task = type;
	}

	public SorcererTaskDependency(TaskType type, int iterNo) {
		this(type);
		this.customIterNo = iterNo;
	}

	public SorcererTaskDependency(Class<? extends Task> taskClass) {

		SorcererTask anno = taskClass.getAnnotation(SorcererTask.class);
		if (anno == null) {
			logger.error("Annotation not found on " + taskClass.toString());
			this.task = null;
			return;
		}

		String taskName = anno.name();
		if (taskName == null) {
			logger.error("No taskName found in annotation on " + taskClass.toString());
			this.task = null;
			return;
		}

		this.task = SorcererInjector.get().getTaskType(taskName);
	}

	public SorcererTaskDependency(Class<? extends Task> taskClass, int iterNo) {
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
		return task != null && StatusManager.get().isTaskComplete(
				task, customIterNo == null ? iterNo : customIterNo);
	}
}
