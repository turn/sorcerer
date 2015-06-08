/*
 * Copyright (c) 2015, Turn Inc. All Rights Reserved.
 * Use of this source code is governed by a BSD-style license that can be found
 * in the LICENSE file.
 */

package com.turn.sorcerer.injector;

import com.turn.sorcerer.config.impl.SorcererAbstractModule;
import com.turn.sorcerer.module.ModuleType;
import com.turn.sorcerer.pipeline.Pipeline;
import com.turn.sorcerer.pipeline.type.PipelineType;
import com.turn.sorcerer.status.StatusStorage;
import com.turn.sorcerer.task.Task;
import com.turn.sorcerer.task.type.TaskType;
import com.turn.sorcerer.util.email.EmailType;

import java.util.Set;

import com.google.inject.ConfigurationException;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.TypeLiteral;
import com.google.inject.name.Names;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Wrapper around Guice injector
 *
 * @author tshiou
 */
public class SorcererInjector {
	private static final Logger logger =
			LoggerFactory.getLogger(SorcererInjector.class);

	// We use Guice as the underlying binder
	private Injector INJECTOR;

	/**
	 * Singleton pattern. A single instance of Sorcerer runs per JVM.
	 */
	private static SorcererInjector INSTANCE = new SorcererInjector();

	public static SorcererInjector get() {
		return INSTANCE;
	}

	// Private constructor to prevent instantiation
	private SorcererInjector() {}

	private final TypeLiteral<Set<PipelineType>> setOfPipelines =
			new TypeLiteral<Set<PipelineType>>() {};

	/**
	 * Configure the SorcererInjector. Should be called once per instance of
	 * Sorcerer.
	 *
	 * <p>
	 * If the underlying injector is configured and created, this method will
	 * log a warning and return without doing anything. Sorcerer currently does
	 * not support reconfiguration.
	 * </p>
	 *
	 * @param module SorcererAbstractModule defining the bindings
	 */
	public void configure(SorcererAbstractModule module) {

		if (INJECTOR != null) {
			logger.warn("Injector configuration method called but injector is already configured");
			return;
		}

		INJECTOR = Guice.createInjector(module);
	}

	/**
	 * Provides the task type registered to a specific task name
	 *
	 * <p>
	 * Uses the underlying injector to get the task type instance binding with
	 * the provided task name. <b>If the binding is not found the method will
	 * return null.</b> Therefore the client should handle the case if the
	 * binding doesn't exist.
	 * </p>
	 *
	 * @param name Name of task
	 * @return Returns TaskType instance registered to the provided task name.
	 * Returns null if no task type was found with the provided name.
	 */
	public TaskType getTaskType(final String name){
		try {
			return INJECTOR.getInstance(
					Key.get(TaskType.class, Names.named(name)));
		} catch (ConfigurationException ce) {
			logger.error("Injector could not get instance", ce);
			return null;
		}
	}

	/**
	 * Provides a Task implementation instance of the task type
	 *
	 * <p>
	 * Uses the underlying injector to get an instance of the Task
	 * implementation that is registered to the provided task type. If no
	 * binding is found for the task type, the method will return null.
	 * This should never happen since Sorcerer does eager task type
	 * reconciliation at initialization. However, it is possible (though
	 * unsupported) that some unregistered task type was created outside of
	 * Sorcerer. In any case, the client should handle the return value of
	 * null if no Task implementation was found for the task type.
	 * </p>
	 *
	 * @param type Task type
	 * @return Returns a Task instance registered to the provided task name.
	 * Returns null if no task implementation was found for the task type.
	 */
	public Task getInstance(TaskType type) {
		try {
			return INJECTOR.getInstance(
					Key.get(Task.class, Names.named(type.getName())));
		} catch (ConfigurationException ce) {
			logger.error("Injector could not get instance", ce);
			return null;
		}
	}

	/**
	 * Provides the pipeline type registered to a specific task name
	 *
	 * <p>
	 * Uses the underlying injector to get the pipeline type instance binding
	 * with the provided pipeline name. <b>If the binding is not found the
	 * method will return null.</b> Therefore the client should handle the case
	 * if the binding doesn't exist.
	 * </p>
	 *
	 * @param name Name of pipeline
	 * @return Returns PipelineType instance registered to the provided
	 * pipeline name
	 */
	public PipelineType getPipelineType(final String name) {
		try {
			return INJECTOR.getInstance(
					Key.get(PipelineType.class, Names.named(name)));
		} catch (ConfigurationException ce) {
			logger.error("Injector could not get instance", ce);
			return null;
		}
	}

	/**
	 * Provides a Pipeline implementation instance of the pipeline type
	 *
	 * <p>
	 * Uses the underlying injector to get an instance of the Pipeline
	 * implementation that is registered to the provided pipeline type. If no
	 * binding is found for the pipeline type, the method will return null.
	 * This should never happen since Sorcerer does eager pipeline type
	 * reconciliation at initialization. However, it is possible (though
	 * unsupported) that some unregistered pipeline type was created outside of
	 * Sorcerer. In any case, the client should handle the return value of
	 * null if no Pipeline implementation was found for the pipeline type.
	 * </p>
	 *
	 * @param type pipeline type
	 * @return Returns a Pipeline instance registered to the provided pipeline
	 * name. Returns null if no Pipeline implementation was found for the
	 * pipeline type.
	 */
	public Pipeline getInstance(PipelineType type) {
		try {
			return INJECTOR.getInstance(
					Key.get(Pipeline.class, Names.named(type.getName())));
		} catch (ConfigurationException ce) {
			logger.error("Injector could not get instance", ce);
			return null;
		}
	}

	/**
	 * Checks if a Pipeline implementation binding exists for a pipeline type
	 */
	public boolean bindingExists(PipelineType type) {
		return INJECTOR.getExistingBinding(
				Key.get(Pipeline.class, Names.named(type.getName()))
		) != null;
	}

	/**
	 * Provides the set of pipeline types to be scheduled in the module
	 */
	public Set<PipelineType> getPipelines() {
		return INJECTOR.getInstance(Key.get(setOfPipelines));
	}

	/**
	 * Provides the module type representing the current Sorcerer module
	 */
	public ModuleType getModule() {
		return INJECTOR.getInstance(ModuleType.class);
	}

	/**
	 * Provides email information defined in the module
	 */
	public EmailType getAdminEmail() {
		return INJECTOR.getInstance(EmailType.class);
	}

	/**
	 * Provides an instance of the storage layer
	 */
	public StatusStorage getStorageInstance() {
		return INJECTOR.getInstance(StatusStorage.class);
	}

}
