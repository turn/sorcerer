/*
 * Copyright (C) 2015 Turn Inc. All Rights Reserved.
 * Proprietary and confidential.
 */

package com.turn.sorcerer.executor;

import com.turn.sorcerer.pipeline.type.PipelineType;
import com.turn.sorcerer.pipeline.executable.ExecutablePipeline;
import com.turn.sorcerer.pipeline.executable.impl.PipelineFactory;
import com.turn.sorcerer.task.type.TaskType;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Class Description Here
 *
 * @author tshiou
 */
public class PipelineExecutor implements Runnable {


	private static final Logger logger =
			LogManager.getFormatterLogger(PipelineExecutor.class);

	private final ScheduledExecutorService executor;
	private final ExecutablePipeline pipeline;
	private final TaskScheduler taskScheduler;
	private final int interval;

	public PipelineExecutor(PipelineType pipelineType, int jobId) {
		this(pipelineType, jobId, new HashMap<TaskType, String[]>(), false, false);
	}

	public PipelineExecutor(PipelineType pipelineType,
	                        int jobId, Map<TaskType, String[]> taskArgMap,
	                        boolean adhoc, boolean overwriteTasks) {
		this.interval = pipelineType.getInterval();

		int threads = pipelineType.getThreads();
		executor= Executors.newScheduledThreadPool(threads);

		pipeline = PipelineFactory.get().getExecutablePipeline(pipelineType, jobId);

		// after we call getExecutablePipeline(), all parameters of pipelineFactory are cleared

		taskScheduler = new TaskScheduler(
				pipeline, pipeline.getId(), threads, taskArgMap, adhoc, overwriteTasks);
	}

	@Override
	public void run() {
		if (pipeline.isEnabled() == false) {
			logger.debug(pipeline.name() + " is disabled. Exiting.");
			return;
		}

		logger.info("Scheduling " + pipeline + " every "
				+ interval + " seconds");

		pipeline.updatePipelineCompletion();

		executor.scheduleWithFixedDelay(taskScheduler, 0, interval,	TimeUnit.SECONDS);

		while (pipeline.isCompleted() == false) {
			try {
				Thread.sleep(1000 * interval);
			} catch (InterruptedException e) {
				logger.warn("Sleep interrupted");
			}
		}

		abort();
	}

	public void abort() {
		logger.info("Shutting down pipeline executor for " + pipeline);
		taskScheduler.abort();
		executor.shutdown();
	}

}
