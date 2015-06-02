/*
 * Copyright (c) 2015, Turn Inc. All Rights Reserved.
 * Use of this source code is governed by a BSD-style license that can be found
 * in the LICENSE file.
 */

package com.turn.sorcerer.dependency.impl;

import com.turn.sorcerer.dependency.Dependency;
import com.turn.sorcerer.exception.SorcererException;
import com.turn.sorcerer.injector.SorcererInjector;
import com.turn.sorcerer.status.StatusManager;
import com.turn.sorcerer.task.SorcererTask;
import com.turn.sorcerer.task.Task;
import com.turn.sorcerer.task.type.TaskType;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Task dependency on another task
 *
 * <p>
 * This implementation of {@link Dependency} represents a dependency on
 * another task. The requisite task can be from the same pipeline or another
 * pipeline.
 * </p>
 *
 * @author tshiou
 */
public class SorcererTaskDependency implements Dependency {

	private static final Logger logger =
			LogManager.getFormatterLogger(SorcererTaskDependency.class);

	// Requisite task type
	private final TaskType task;

	// Requisite task iteration number
	// If not provided the iteration number will be injected "just-in-time"
	private Integer customIterNo = null;

	/**
	 * Task dependency constructor based on provided TaskType
	 *
	 * <p>
	 * The iteration number of the requisite task will be injected at
	 * dependency checking runtime (see {@link #check(int)}).
	 * </p>
	 *
	 * @param type Task type
	 */
	public SorcererTaskDependency(TaskType type) {
		this.task = type;
	}

	/**
	 * Task dependency constructor based on provided TaskType
	 *
	 * @param type Task type
	 * @param iterNo Iteration number of task dependency
	 */
	public SorcererTaskDependency(TaskType type, int iterNo) {
		this(type);
		this.customIterNo = iterNo;
	}

	/**
	 * Task dependency can be based on a class that implements {@code Task}
	 *
	 * <p>
	 * The class that is provided must implement {@code Task}, have the
	 * {@code SorcererTask} annotation, and also be registered in Sorcerer.
	 * If any of these requirements are not met then this constructor will
	 * throw a SorcererException.
	 * </p>
	 *
	 * <p>
	 * The iteration number of the requisite task will be injected at
	 * dependency checking runtime (see {@link #check(int)}).
	 * </p>
	 *
	 * @param taskClass Class which implements Task
	 * @throws SorcererException
	 */
	public SorcererTaskDependency(Class<? extends Task> taskClass)
			throws SorcererException {

		SorcererTask anno = taskClass.getAnnotation(SorcererTask.class);
		if (anno == null) {
			logger.error("Annotation not found on " + taskClass.toString());
			this.task = null;
			throw new SorcererException(taskClass.getName());
		}

		String taskName = anno.name();
		if (taskName == null) {
			logger.error("No taskName found in annotation on " + taskClass.toString());
			this.task = null;
			return;
		}

		this.task = SorcererInjector.get().getTaskType(taskName);

		// Could not get task type from registry
		if (this.task == null) {
			throw new SorcererException(taskClass.getName());
		}
	}

	/**
	 * Task dependency can be based on a class that implements {@code Task}
	 *
	 * <p>
	 * The class that is provided must implement {@code Task}, have the
	 * {@code SorcererTask} annotation, and also be registered in Sorcerer.
	 * If any of these requirements are not met then this constructor will
	 * throw a SorcererException.
	 * </p>
	 *
	 * @param taskClass Class which implements Task
	 * @param iterNo Iteration number of task dependency
	 * @throws SorcererException
	 */
	public SorcererTaskDependency(Class<? extends Task> taskClass, int iterNo)
			throws SorcererException {
		this(taskClass);
		this.customIterNo = iterNo;
	}

	/**
	 * Task dependency constructor based on provided task name
	 *
	 * <p>
	 * The iteration number of the requisite task will be injected at
	 * dependency checking runtime (see {@link #check(int)}).
	 * </p>
	 *
	 * @param taskName Task name
	 */
	public SorcererTaskDependency(String taskName) {
		this.task = SorcererInjector.get().getTaskType(taskName);
	}

	/**
	 * Task dependency constructor based on provided task name
	 *
	 * @param taskName Task name
	 * @param iterNo Iteration number of task dependency
	 */
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
