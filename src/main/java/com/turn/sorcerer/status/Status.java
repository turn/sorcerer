/*
 * Copyright (c) 2015, Turn Inc. All Rights Reserved.
 * Use of this source code is governed by a BSD-style license that can be found
 * in the LICENSE file.
 */

package com.turn.sorcerer.status;

/**
 * Sorcerer state status enumerations
 *
 * <p>
 * Sorcerer pipelines and tasks use these enumerations to keep track of the
 * state of the object.
 * </p>
 *
 * <p>
 * Pipelines have a very simple state diagram, either they are scheduled and
 * therefore considered in-progress, or they have successfully completed.
 * </p>
 *
 * <p>
 * Tasks have more states to their state diagram. They start in a pending state
 * before being scheduled into a running state. Then depending on the result
 * of execution they can end up in a successful state or error state.
 * </p>
 *
 * @author tshiou
 */
public enum Status {

	/**
	 * Represents a task that is a part of a scheduled pipeline but is not yet
	 * eligible for execution, or more specifically the previous tasks in the
	 * workflow have not completed or one of
	 * {@link com.turn.sorcerer.task.Task#getDependencies(int)} is returning
	 * false
	 */
	PENDING("PENDING"),

	/**
	 * Represents either:
	 *
	 * <li>A task that is in-progress, or more specifically
	 * {@link com.turn.sorcerer.task.Task#exec} method is running
	 * </li>
	 *
	 * <li>
	 * A pipeline whose initial task has been launched but the pipeline is not
	 * complete, or more specifically all of its memeber tasks have not
	 * completed successfully.
	 * </li>
	 */
	IN_PROGRESS("RUNNING"),

	/**
	 * Represents either:
	 *
	 * <li>
	 * A successfully completed task, or more specifically
	 * {@link com.turn.sorcerer.task.Task#exec} has completed with no
	 * exceptions
	 * </li>
	 *
	 * <li>
	 * A successfully completed pipeline, or more specifically a pipeline
	 * with all member tasks successfully completed.
	 * </li>
	 */
	SUCCESS("SUCCESS"),

	/**
	 * Represents a task with a previous iteration run that resulted in an
	 * error state, or more specifically
	 * {@link com.turn.sorcerer.task.Task#exec} was executed with an exception
	 * thrown
	 */
	ERROR("ERROR"),
	;

	// String representation of status
	private String string;

	private Status(String string) {
		this.string = string;
	}

	/**
	 * Returns string representation of state.
	 */
	public String getString() {
		return string;
	}
}
