/*
 * Copyright (C) 2015 Turn Inc. All Rights Reserved.
 * Proprietary and confidential.
 */

package com.turn.sorcerer.task;

import java.util.Map;

import com.google.common.collect.Maps;

/**
 * Class Description Here
 *
 * @author tshiou
 */
public class Context {

	private final int iterationNumber;
	private final Map<String, Long> metrics = Maps.newHashMap();

	public Context(int iterationNumber) {
		this.iterationNumber = iterationNumber;
	}

	public int getIterationNumber() {
		return this.iterationNumber;
	}

	public void addMetric(String key, Long value) {
		metrics.put(key, value);
	}
}
