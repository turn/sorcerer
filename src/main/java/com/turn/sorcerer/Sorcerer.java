/*
 * Copyright (C) 2015 Turn Inc. All Rights Reserved.
 * Proprietary and confidential.
 */

package com.turn.sorcerer;

import com.turn.sorcerer.config.impl.SorcererConfiguration;
import com.turn.sorcerer.exception.SorcererException;
import com.turn.sorcerer.executor.PipelineScheduler;
import com.turn.sorcerer.injector.SorcererInjector;
import com.turn.sorcerer.pipeline.type.PipelineType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.google.common.collect.Lists;
import com.google.inject.BindingAnnotation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Class Description Here
 *
 * @author tshiou
 */
public class Sorcerer {

	private static final Logger logger =
			LogManager.getFormatterLogger(Sorcerer.class);

	private final SorcererInjector injector;

	private Sorcerer(SorcererInjector injector) {
		this.injector = injector;
	}

	private List<ExecutorService> pipelineThreads = Lists.newArrayList();

	public void run() throws SorcererException {
		scheduleAndRunPipeline();
	}

	private void scheduleAndRunPipeline() {

		logger.info("Starting Sorcerer");

		for (PipelineType pipeline: injector.getPipelines()) {

			if (pipeline == null) {
				logger.error("Could not instantiate pipeline.");
				return;
			}

			logger.info("Starting pipeline " + pipeline);

			PipelineScheduler pipelineScheduler = new PipelineScheduler(pipeline);

			ExecutorService pipelineThread = Executors.newSingleThreadExecutor();
			pipelineThread.submit(pipelineScheduler);
			pipelineThreads.add(pipelineThread);
		}

		logger.debug("Exiting Sorcerer startup thread");
	}

	public void stop() {
		for (ExecutorService thread : pipelineThreads) {
			thread.shutdown();
		}
	}

	public static SorcererModuleBuilder builder() {
		return new SorcererModuleBuilder();
	}

	public static class SorcererModuleBuilder {

		private List<String> pkgs = Lists.newArrayList();
		private List<String> configs = Lists.newArrayList();

		public SorcererModuleBuilder addPackage(String pkg) {
			pkgs.add(pkg);
			return this;
		}

		public SorcererModuleBuilder addConfigPath(String path) {
			configs.add(path);
			return this;
		}

		public Sorcerer create() throws SorcererException {
			return new Sorcerer(
					SorcererConfiguration.get()
							.addConfigs(configs)
							.addPackages(pkgs)
							.getInjector());
		}
	}

	public static void main(String[] args) throws Exception{
		logger.info("Configuring sorcerer module");
		Sorcerer module = Sorcerer.builder()
				.addConfigPath(args[0])
				.create();
		module.run();
	}

	@Retention(RetentionPolicy.RUNTIME)
	@Target({ElementType.FIELD, ElementType.PARAMETER})
	@BindingAnnotation
	public @interface Pipelines {}
}
