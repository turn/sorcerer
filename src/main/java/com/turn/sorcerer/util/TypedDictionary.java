/*
 * Copyright (c) 2015, Turn Inc. All Rights Reserved.
 * Use of this source code is governed by a BSD-style license that can be found
 * in the LICENSE file.
 */

package com.turn.sorcerer.util;

import java.util.Map;

import com.google.common.collect.Maps;

/**
 * Class Description Here
 *
 * @author tshiou
 */
public class TypedDictionary {
	private Map<String, Object> map;

	public TypedDictionary() {
		map = Maps.newHashMap();
	}

	public <E> E get(String key, Class<E> type) {
		if (!map.containsKey(key)) return null;
		Object o = map.get(key);
		if (o == null) return null;
		return type.cast(o);
	}

	public boolean getBool(String key) {
		return get(key, Boolean.class);
	}

	public long getLong(String key) {
		return get(key, Long.class);
	}

	public String getString(String key) {
		return get(key, String.class);
	}

	public double getDouble(String key) {
		return get(key, Double.class);
	}

	public boolean getBool(String key, boolean def) {
		Boolean i = get(key, Boolean.class);
		return i == null? def: i;
	}

	public long getLong(String key, long def) {
		Long i = get(key, Long.class);
		return i == null? def: i;
	}

	public String getString(String key, String def) {
		String i = get(key, String.class);
		return i == null? def: i;
	}

	public double getDouble(String key, double def) {
		Double i = get(key, Double.class);
		return i == null? def: i;
	}

	public void put(String key, Object o) {
		map.put(key, o);
	}

	public void putAll(Map<String, ? extends Object> props) {
		if (props == null || props.size() == 0) {
			return;
		}

		this.map.putAll(props);
	}

	public void putAll(TypedDictionary other) {
		if (other == null || other.size() == 0) {
			return;
		}

		this.map.putAll(other.map);
	}

	public int size() {
		return map.size();
	}
}
