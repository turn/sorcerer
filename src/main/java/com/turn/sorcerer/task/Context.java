/*
 * Copyright (c) 2015, Turn Inc. All Rights Reserved.
 * Use of this source code is governed by a BSD-style license that can be found
 * in the LICENSE file.
 */

package com.turn.sorcerer.task;

import com.turn.sorcerer.util.TypedDictionary;

import java.util.Map;

import com.google.common.collect.Maps;

/**
 * Class Description Here
 *
 * @author tshiou
 */
public class Context {

	private final int iterationNumber;
	private final TypedDictionary properties;
	private final Map<String, Long> metrics = Maps.newHashMap();

	public Context(int iterationNumber, TypedDictionary parameters) {
		properties = new TypedDictionary();
		this.iterationNumber = iterationNumber;
		properties.putAll(parameters);
	}

	public int getIterationNumber() {
		return this.iterationNumber;
	}

	public void addMetric(String key, Long value) {
		metrics.put(key, value);
	}

	public void putProperty(String key, Object value) {
		properties.put(key, value);
	}

	public TypedDictionary getProperties() {
		return this.properties;
	}
}
