/*
 * Copyright (C) 2015 Turn Inc. All Rights Reserved.
 * Proprietary and confidential.
 */

package com.turn.sorcerer.pipeline.type;

import com.google.common.base.MoreObjects;

/**
 * Class Description Here
 *
 * @author tshiou
 */
public class PipelineType {
	private String name;

	private String init;

	private Integer interval;

	private Integer lookback = 0;

	private Integer threads = 1;

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

	public String toString() {
		return MoreObjects.toStringHelper(this)
				.add("name", this.name)
				.add("init", this.init)
				.toString();
	}

	@Override
	public int hashCode() {
		return name.hashCode();
	}

	@Override
	public boolean equals(Object o) {

		return o instanceof PipelineType && this.getName().equals(((PipelineType) o).getName());

	}
}
