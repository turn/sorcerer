/*
 * Copyright (C) 2015 Turn Inc. All Rights Reserved.
 * Proprietary and confidential.
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

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.TypeLiteral;
import com.google.inject.name.Names;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Wrapper around Guice injector
 *
 * @author tshiou
 */
public class SorcererInjector {
	private static final Logger logger =
			LogManager.getFormatterLogger(SorcererInjector.class);

	private final TypeLiteral<Set<PipelineType>> setOfPipelines =
			new TypeLiteral<Set<PipelineType>>() {};

	private static SorcererInjector INSTANCE = new SorcererInjector();

	private Injector INJECTOR;

	public void configure(SorcererAbstractModule module) {

		if (INJECTOR != null) {
			logger.error("Injector configuration method called but injector is already configured");
			return;
		}

		INJECTOR = Guice.createInjector(module);
	}

	public static SorcererInjector get() {
		return INSTANCE;
	}

	private SorcererInjector() {}


	public TaskType getTaskType(final String name) {
		return INJECTOR.getInstance(
				Key.get(TaskType.class, Names.named(name)));
	}

	public Task getInstance(TaskType type) {
		return INJECTOR.getInstance(
				Key.get(Task.class, Names.named(type.getName())));
	}

	public boolean bindingExists(PipelineType type) {
		return INJECTOR.getExistingBinding(
				Key.get(Pipeline.class, Names.named(type.getName()))
		) != null;
	}

	private boolean bindingExists(final String name) {
		return INJECTOR.getExistingBinding(
				Key.get(Pipeline.class, Names.named(name))
		) != null;
	}

	public PipelineType getPipelineType(final String name) {
		return INJECTOR.getInstance(
				Key.get(PipelineType.class, Names.named(name)));
	}

	public Set<PipelineType> getPipelines() {
		return INJECTOR.getInstance(Key.get(setOfPipelines));
	}

	public Pipeline getInstance(PipelineType type) {
		return INJECTOR.getInstance(
				Key.get(Pipeline.class, Names.named(type.getName())));
	}

	public ModuleType getModule() {
		return INJECTOR.getInstance(ModuleType.class);
	}

	public EmailType getAdminEmail() {
		return INJECTOR.getInstance(EmailType.class);
	}

	public StatusStorage getStorageInstance() {
		return INJECTOR.getInstance(StatusStorage.class);
	}

}
