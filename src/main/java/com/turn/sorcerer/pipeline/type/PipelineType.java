/*
 * Copyright (c) 2015, Turn Inc. All Rights Reserved.
 * Use of this source code is governed by a BSD-style license that can be found
 * in the LICENSE file.
 */

package com.turn.sorcerer.pipeline.type;

import com.google.common.base.Objects;

/**
 * Represents a pipeline definition. Instances are automatically created and
 * fields are populated by the Sorcerer configuration processor.
 *
 * @author tshiou
 */
public class PipelineType {
	private String name;

	private String init;

	private Integer interval;

	private Integer lookback = 0;

	private Integer threads = 0;

	public String getName() {
		return this.name;
	}

	public String getInitTaskName() {
		return this.init;
	}

	public Integer getInterval() {
		return this.interval;
	}

	public Integer getLookback() {
		return this.lookback;
	}

	public Integer getThreads() {
		return this.threads;
	}

	@Override
	public String toString() {
		return Objects.toStringHelper(this)
				.add("name", this.name)
				.add("init", this.init)
				.toString();
	}

	/**
	 * The name field is used as pipeline's unique identifier so we use it for hashing
	 */
	@Override
	public int hashCode() {
		return name.hashCode();
	}

	@Override
	public boolean equals(Object o) {
		return o instanceof PipelineType
				&& this.getName().equals(((PipelineType) o).getName());

	}
}
