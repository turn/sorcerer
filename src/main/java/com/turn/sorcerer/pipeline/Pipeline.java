/*
 * Copyright (c) 2015, Turn Inc. All Rights Reserved.
 * Use of this source code is governed by a BSD-style license that can be found
 * in the LICENSE file.
 */

package com.turn.sorcerer.pipeline;

/**
 * Represents a workflow of tasks in Sorcerer. Should be implemented by any class
 * whose instances will be used to trigger the execution of a pipeline.
 *
 *
 * <p>
 * Classes that implement this interface should also be annotated with
 * {@code SorcererPipeline(name)} for Sorcerer to register the implementation.
 * The {@code name} field of the {@code SorcererPipeline} annotation will be
 * used to map the implementation to the corresponding pipeline configuration
 * of the same name.
 * </p>
 *
 * <p>
 * If Sorcerer processes a pipeline configuration but does not find a pipeline
 * implementation of the same name, it will create a default pipeline
 * implementation which will generate a new iteration number for every interval
 * defined by the pipeline configuration {@code interval} field.
 * </p>
 *
 * @author  tshiou
 * @see     com.turn.sorcerer.executor.PipelineScheduler
 * @see     com.turn.sorcerer.pipeline.type.PipelineType
 */
public interface Pipeline {

	/**
	 * Provides the current iteration number of a pipeline
	 *
	 * <p>
	 * This method should be implemented to trigger the scheduling of a
	 * pipeline based on the iteration number. Sorcerer will use the value
	 * returned by this method to schedule pipelines. Each time a new iteration
	 * number is provided by this method, Sorcerer will instantiate and
	 * schedule a new pipeline.
	 * </p>
	 *
	 * <p>
	 * Note: This method will be called periodically (as defined by the
	 * {@code interval} field of a pipeline configuration) and so it should
	 * be stateless. Sorcerer instantiates a Pipeline instance using its
	 * injector and then calls this method to generate the current iteration
	 * number.
	 * </p>
	 *
	 * @return Current iteration number
	 */
	Integer getCurrentIterationNumber();

	Integer getPreviousIterationNumber(int curr, int prev);
}
