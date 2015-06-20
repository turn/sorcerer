/*
 * Copyright (c) 2015, Turn Inc. All Rights Reserved.
 * Use of this source code is governed by a BSD-style license that can be found
 * in the LICENSE file.
 */

package com.turn.sorcerer.config.impl;

import com.turn.sorcerer.config.AnnotationProcessor;
import com.turn.sorcerer.config.ConfigReader;
import com.turn.sorcerer.exception.SorcererException;
import com.turn.sorcerer.injector.SorcererInjector;
import com.turn.sorcerer.pipeline.type.PipelineType;
import com.turn.sorcerer.task.type.TaskType;

import java.io.File;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import com.google.common.collect.Lists;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.Marker;
import org.slf4j.MarkerFactory;

/**
* Class Description Here
*
* @author tshiou
*/
public class SorcererConfiguration {

	private static final Logger logger =
			LoggerFactory.getLogger(SorcererConfiguration.class);
	public static final Marker FATAL = MarkerFactory.getMarker("FATAL");
	
	private static final SorcererConfiguration INSTANCE = new SorcererConfiguration();

	private ConfigReader configReader = new YamlConfigReader();

	private Collection<File> configFiles = Lists.newArrayList();
	private Collection<String> packages = Lists.newArrayList();

	private SorcererConfiguration() {

	}

	public static SorcererConfiguration get() {
		return INSTANCE;
	}

	public SorcererConfiguration addConfigs(Collection<String> configPaths) {
		findAndAddConfigFiles(configPaths);
		return this;
	}

	private Collection<File> findAndAddConfigFiles(Collection<String> paths) {
		for (String path : paths) {
			File f = new File(path);
			if (f.isFile()) {
				configFiles.add(f);
			}

			if (f.isDirectory()) {
				Collections.addAll(configFiles, f.listFiles(configReader.getFileFilter()));
			}
		}

		logger.debug("Found config files : " + configFiles);

		return configFiles;
	}

	public SorcererConfiguration addPackages(Collection<String> pkgs) {
		this.packages.addAll(pkgs);
		return this;
	}

	@SuppressWarnings("unchecked")
	private void process() throws SorcererException {
		Collection<String> morePkgs = configReader.read(configFiles);

		AnnotationProcessor annotationProcessor = new InfomasAnnotationProcessor();
		annotationProcessor.process(packages, morePkgs);
	}

	private boolean reconcile() {

		boolean success = true;

		// Should only be one module
		if (SorcererRegistry.get().getModules().size() != 1) {
			logger.error(FATAL, "Invalid number of modules found! " +
					"Exactly one module should be specified but found " +
					SorcererRegistry.get().getModules().size());

			if (SorcererRegistry.get().getModules().size() > 0) {
				logger.error(FATAL, "Found modules " + SorcererRegistry.get().getModules());
			}

			success = false;
		}

		// Reconcile Tasks
		for (Map.Entry<String, TaskType> entry : SorcererRegistry.get().getTasks().entrySet()) {
			String taskName = entry.getKey();

			// If {fork, join} then task class not needed
			if (TaskType.SUPPORTED_EXEC.containsKey(entry.getValue().getExec())) {
				continue;
			}

			if (SorcererRegistry.get().getTaskClasses().containsKey(taskName) == false) {
				logger.error(FATAL, "Could not find a class for task " + taskName);
				success = false;
			}

			// Check SLA strings
			try {
				entry.getValue().initSLA();
			} catch (SorcererException se) {
				logger.error(FATAL, "Task SLA could not be initialized", se);
				success = false;
			}
		}

		// Reconcile Pipelines
		for (PipelineType pipeline : SorcererRegistry.get().getPipelines().values()) {

			List<String> tasks = Lists.newArrayList(pipeline.getInitTaskName());

			// Recursively check tasks in DAG
			while(tasks.size() > 0) {

				String task = tasks.remove(0);

				// If no definition found for current task (should only apply for initial task)
				if (SorcererRegistry.get().getTasks().containsKey(task) == false) {
					logger.error(FATAL, "Initial task " + task + " found for pipeline " +
							pipeline.getName() + " but no definition found!");
					success = false;
					continue;
				}

				List<String> nextTasks = SorcererRegistry.get().getTasks().get(task).getNextTaskNames();

				if (nextTasks == null) {
					continue;
				}

				for (String t : nextTasks) {

					// If no definition found for child task
					if (SorcererRegistry.get().getTasks().containsKey(t) == false) {
						logger.error(FATAL, "Task " + t + " found as a child of " + task +
								" but no definition found!");
						success = false;
						continue;
					}

					// Valid task found
					tasks.add(t);
				}
			}

		}

		return success;

	}

	public SorcererInjector getInjector() throws SorcererException {

		process();

		if (reconcile()) {

			SorcererInjector.get().configure(
					new SorcererAbstractModule(
							SorcererRegistry.get().getTasks(),
							SorcererRegistry.get().getTaskClasses(),
							SorcererRegistry.get().getPipelines(),
							SorcererRegistry.get().getPipelineClasses(),
							SorcererRegistry.get().getModules().get(0)));

			return SorcererInjector.get();
		}

		throw new SorcererException("Configuration failed!");
	}
}
