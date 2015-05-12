/*
 * Copyright (C) 2015 Turn Inc. All Rights Reserved.
 * Proprietary and confidential.
 */

package com.turn.sorcerer.config.impl;

import com.turn.sorcerer.module.ModuleType;
import com.turn.sorcerer.pipeline.Pipeline;
import com.turn.sorcerer.pipeline.type.PipelineType;
import com.turn.sorcerer.task.Task;
import com.turn.sorcerer.task.type.TaskType;

import java.util.List;
import java.util.Map;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Class Description Here
 *
 * @author tshiou
 */
public class SorcererRegistry {

	private static final Logger logger =
			LogManager.getFormatterLogger(SorcererRegistry.class);

	private Map<String, TaskType> tasks = Maps.newHashMap();
	private Map<String, PipelineType> pipelines = Maps.newHashMap();
	private List<ModuleType> modules = Lists.newArrayList();
	private Map<String, Class<? extends Task>> taskClasses = Maps.newHashMap();
	private Map<String, Class<? extends Pipeline>> pipelineClasses = Maps.newHashMap();
	private List<String> packages = Lists.newArrayList();

	private static final SorcererRegistry INSTANCE = new SorcererRegistry();

	protected static SorcererRegistry get() {
		return INSTANCE;
	}

	private SorcererRegistry() {

	}

	protected void registerTaskClass(String name, Class<? extends Task> clazz) {
		if (taskClasses.containsKey(name)) {
			logger.error("Task name " + name + " is mapped to multiple classes!\n" +
							name + " is already mapped to " + taskClasses.get(name) +
							" but another mapping " + clazz + " found." +
							"Task names should be unique to one class.\n"
			);
			return;
		}
		logger.debug("Registering task " + name);
		logger.debug(name + " mapped to " + clazz);
		taskClasses.put(name, clazz);
	}

	protected void registerTask(TaskType taskType) {
		String name = taskType.getName();

		if (tasks.containsKey(name)) {
			logger.error("Task name " + name + " is mapped to multiple classes!\n" +
					name + " is already mapped to " + tasks.get(name) +
					" but another mapping " + taskType + " found." +
					"Task names should be unique to one class.\n"
			);
			return;
		}
		logger.debug("Registering task " + name);
		logger.debug(name + " mapped to " + taskType);
		tasks.put(name, taskType);
	}

	protected void registerPipelineClass(String name, Class<? extends Pipeline> clazz) {
		if (pipelineClasses.containsKey(name)) {
			logger.error("Pipeline name " + name + " is mapped to multiple classes!\n" +
							name + " is already mapped to " + pipelineClasses.get(name) +
							" but another mapping " + clazz + " found." +
							"Pipeline names should be unique to one class.\n"
			);
			return;
		}
		logger.debug("Registering pipeline " + name);
		logger.debug(name + " mapped to " + clazz);
		pipelineClasses.put(name, clazz);
	}

	protected void registerPipeline(PipelineType pipelineType) {
		String name = pipelineType.getName();

		if (pipelines.containsKey(name)) {
			logger.error("Task name " + name + " is mapped to multiple classes!\n" +
							name + " is already mapped to " + pipelines.get(name) +
							" but another mapping " + pipelineType + " found." +
							"Task names should be unique to one class.\n"
			);
			return;
		}
		logger.debug("Registering pipeline " + name);
		logger.debug(name + " mapped to " + pipelineType);
		pipelines.put(name, pipelineType);
	}

	protected void registerModule(ModuleType moduleType) {
		modules.add(moduleType);

		// Register addPackages
		if (moduleType.getPackages() != null) {
			packages.addAll(moduleType.getPackages());
		}
	}

	protected Map<String, TaskType> getTasks() {
		return this.tasks;
	}

	protected Map<String, Class<? extends Task>> getTaskClasses() {
		return this.taskClasses;
	}

	protected Map<String, PipelineType> getPipelines() {
		return this.pipelines;
	}

	protected List<ModuleType> getModules() {
		return this.modules;
	}

	protected Map<String, Class<? extends Pipeline>> getPipelineClasses() {
		return this.pipelineClasses;
	}
}
