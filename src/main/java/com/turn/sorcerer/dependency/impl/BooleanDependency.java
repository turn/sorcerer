/*
 * Copyright (C) 2015 Turn Inc. All Rights Reserved.
 * Proprietary and confidential.
 */

package com.turn.sorcerer.dependency.impl;

import com.turn.sorcerer.dependency.Dependency;

/**
 * Simple boolean dependency
 *
 * @author tshiou
 */
public class BooleanDependency implements Dependency {

	private final boolean success;

	public BooleanDependency(boolean success) {
		this.success = success;
	}

	@Override
	public boolean check(int iterNo) {
		return success;
	}
}
