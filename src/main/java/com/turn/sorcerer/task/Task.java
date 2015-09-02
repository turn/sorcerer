/*
 * Copyright (c) 2015, Turn Inc. All Rights Reserved.
 * Use of this source code is governed by a BSD-style license that can be found
 * in the LICENSE file.
 */

package com.turn.sorcerer.task;

import com.turn.sorcerer.dependency.Dependency;

import java.util.Collection;

/**
 * Represents the smallest unit of action in the Sorcerer workflow. Should
 * be implemented by any class whose instances are intended to be executed
 * in a pipeline by Sorcerer.
 *
 * <p>
 * This interface should be implemented to provide Sorcerer with the
 * initialization and execution code of a task in the workflow.
 * </p>
 *
 * <p>
 * The execution of the implemented methods are such:
 * <ol>
 *     <li>
 *         <b>Initialization</b> by calling {@code init()}.
 *     </li>
 *     <li>
 *         <b>Check dependencies</b> by calling {@code check()} method of each
 *         {@code Dependency} instance provided in {@code getDependencies()}.
 *     </li>
 *     <li>
 *         <b>Execution by calling</b> {@code exec()}
 *     </li>
 *  </ol>
 *  See {@link com.turn.sorcerer.executor.TaskExecutor} for specific details on
 *  task execution.
 * </p>
 *
 * <p>
 * Classes that implement this interface should be annotated with
 * {@code SorcererTask(name)} for Sorcerer to register the implementation.
 * The {@code name} field in the SorcererTask annotation will be used to map
 * the implementation to the corresponding task configuration of the same name.
 * </p>
 *
 * @author  tshiou
 * @see     com.turn.sorcerer.executor.TaskExecutor
 * @see     com.turn.sorcerer.task.executable.ExecutableTask
 * @see     com.turn.sorcerer.dependency.Dependency
 */
public interface Task {

	/**
	 * Initializes the task
	 *
	 * <p>
	 * First method called by Sorcerer in the execution of a task. Any task
	 * initialization code should be put in the implementation of this
	 * method.
	 * </p>
	 *
	 * @see com.turn.sorcerer.task.executable.ExecutableTask#initialize
	 * @see com.turn.sorcerer.executor.TaskExecutor#call
	 *
	 * @param context Context object containing task execution context.
	 *                See {@link com.turn.sorcerer.task.Context}.
	 */
	void init(final Context context);

	/**
	 * Executes the task
	 *
	 * <p>
	 * Implementation of this method should contain the "meat" of the task.
	 * This method will not be called if the dependency checking phase returns
	 * false. This of course means that this method will be called after both
	 * {@code init()} and {@code checkDependencies()}. The successful execution
	 * of this method will result in the task being committed to a successful
	 * completed state.
	 * </p>
	 *
	 * <p>
	 * Obviously if an exception is thrown the task will be committed to an
	 * ERROR state. Additionally, exceptions thrown by this method will be
	 * caught by Sorcerer, wrapped in a {code SorcererException}, logged,
	 * and then an email will be sent to the admins with the exception as the
	 * email body (if enabled).
	 * </p>
	 *
	 * @see com.turn.sorcerer.task.executable.ExecutableTask#execute
	 * @see com.turn.sorcerer.executor.TaskExecutor#call
	 *
	 * @param context Context object containing task execution context.
	 *                See {@link com.turn.sorcerer.task.Context}.
	 * @throws Exception Will be wrapped by a {@code SorcererException} before
	 * being thrown into a higher context.
	 */
	void exec(final Context context) throws Exception;

	/**
	 * Provides a Collection of this task's dependencies
	 *
	 * <p>
	 * This method should provide a collection of all the dependencies of this
	 * task. Sorcerer will iterate over the collection, and call the
	 * {@code check()} method of the {@code Dependency} instance. Once any of
	 * the Dependencies return false, Sorcerer will consider the task
	 * ineligible to be scheduled.
	 * </p>
	 *
	 * <p>
	 * Note that Sorcerer calls this method <i>after</i> {@code init()}.
	 * </p>
	 *
	 * <p>
	 * If any complex dependency logic is required (for example if the user
	 * desires a task be executed if one of many dependencies are fulfilled)
	 * then the logic should be contained in a custom implementation of the
	 * {@link com.turn.sorcerer.dependency.Dependency} class.
	 * </p>
	 *
	 * @see com.turn.sorcerer.task.executable.ExecutableTask#checkDependencies
	 * @see com.turn.sorcerer.executor.TaskExecutor#call
	 *
	 * @param iterNo Iteration number of the task being executed
	 * @return Collection of Dependency objects
	 */
	Collection<Dependency> getDependencies(int iterNo);

	void abort();
}
