/*
 * Copyright (c) 2015, Turn Inc. All Rights Reserved.
 * Use of this source code is governed by a BSD-style license that can be found
 * in the LICENSE file.
 */

package com.turn.sorcerer.dependency;

/**
 * Represents a task dependency
 *
 * <p>
 * Should be implemented by any class that represents a task dependency. A task
 * can require multiple Dependency objects to be fulfilled.
 * </p>
 *
 * @author tshiou
 * @see     com.turn.sorcerer.task.Task
 * @see     com.turn.sorcerer.task.executable.ExecutableTask#checkDependencies()
 */
public interface Dependency {

	/**
	 * Checks if the required dependency is fulfilled
	 *
	 * <p>
	 * Implementation of this method should represent a dependency that a task
	 * requires.
	 * </p>
	 *
	 * @param iterNo Current iteration number
	 * @return Return true if dependency requirement is fulfilled
	 */
	boolean check(int iterNo);
}
