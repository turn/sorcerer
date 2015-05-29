/*
 * Copyright (c) 2015, Turn Inc. All Rights Reserved.
 * Use of this source code is governed by a BSD-style license that can be found
 * in the LICENSE file.
 */

package com.turn.sorcerer.config.impl;

import com.turn.sorcerer.config.AnnotationProcessor;
import com.turn.sorcerer.pipeline.Pipeline;
import com.turn.sorcerer.pipeline.SorcererPipeline;
import com.turn.sorcerer.task.SorcererTask;
import com.turn.sorcerer.task.Task;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.util.Collection;
import java.util.List;

import com.google.common.collect.Lists;
import eu.infomas.annotation.AnnotationDetector;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Class Description Here
 *
 * @author tshiou
 */
public class InfomasAnnotationProcessor implements AnnotationProcessor {

	private static final Logger logger =
			LogManager.getFormatterLogger(InfomasAnnotationProcessor.class);

	public void process(Collection<String> ... pkgs) {

		List<String> packages = Lists.newArrayList();

		for (Collection<String> p : pkgs) {
			packages.addAll(p);
		}

		final AnnotationDetector cf = new AnnotationDetector(new TaskAnnotationReporter());
		try {

			// Full classpath
			if (packages == null || packages.size() == 0) {
				logger.debug("Processing Sorcerer annotations in full classpath");
				cf.detect();
				return;
			}

			for (String pkg : packages) {
				logger.debug("Processing Sorcerer annotations in package " + pkg);
				cf.detect(pkg);
			}

		} catch (IOException e) {
			logger.error(e);
		}
	}

	/**
	 * Class Description Here
	 *
	 * @author tshiou
	 */
	public static class TaskAnnotationReporter implements AnnotationDetector.TypeReporter {

		@SuppressWarnings("unchecked")
		@Override
		public Class<? extends Annotation>[] annotations() {
			return new Class[] {
					SorcererTask.class,
					SorcererPipeline.class
			};
		}

		@SuppressWarnings("unchecked")
		@Override
		public void reportTypeAnnotation(Class<? extends Annotation> a, String s) {
			if (a == SorcererTask.class) {
				Class c;
				try {
					c = Class.forName(s);
				} catch (ClassNotFoundException e) {
					logger.error("Could not find class " + s);
					return;
				}

				Annotation anno = c.getAnnotation(SorcererTask.class);
				SorcererTask sorcererAnno = (SorcererTask) anno;

				// Annotation must provide name
				if (sorcererAnno.name() == null) {
					logger.error("Sorcerer task annotation found on class " + s +
							" but no name provided!");
					return;
				}

				// Class must extend Task
				if (Task.class.isAssignableFrom(c) == false) {
					logger.error("Sorcerer task annotation found but class " + s +
							" does not extend Task!");
					return;
				}

				SorcererRegistry.get().registerTaskClass(sorcererAnno.name(), c);
			} else if (a == SorcererPipeline.class) {
				Class c;
				try {
					c = Class.forName(s);
				} catch (ClassNotFoundException e) {
					logger.error("Could not find class " + s);
					return;
				}

				Annotation anno = c.getAnnotation(SorcererPipeline.class);
				SorcererPipeline sorcererAnno = (SorcererPipeline) anno;

				// Annotation must provide name
				if (sorcererAnno.name() == null) {
					logger.error("Sorcerer pipeline annotation found on class " + s +
							" but no name provided!");
					return;
				}

				// Class must extend Task
				if (Pipeline.class.isAssignableFrom(c) == false) {
					logger.error("Sorcerer pipeline annotation found but class " + s +
							" does not extend pipeline!");
					return;
				}

				SorcererRegistry.get().registerPipelineClass(sorcererAnno.name(), c);
			} else {
				logger.error("Found invalid annotation " + a);
			}
		}
	}
}
