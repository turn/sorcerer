/*
 * Copyright (c) 2015, Turn Inc. All Rights Reserved.
 * Use of this source code is governed by a BSD-style license that can be found
 * in the LICENSE file.
 */

package com.turn.sorcerer.dependency.impl;

import com.turn.sorcerer.dependency.Dependency;

import org.joda.time.DateTime;

/**
 * Time of day task dependency.
 *
 * <p>
 * This implementation of {@link Dependency} should be used if a task should
 * be scheduled after a certain time of day. This implementation uses the
 * Joda-Time library.
 * </p>
 *
 * @author tshiou
 */
public class TimeDependency implements Dependency {

	private final DateTime scheduledTime;

	public TimeDependency(DateTime scheduledTime) {
		this.scheduledTime = scheduledTime;
	}

	@Override
	public boolean check(int iterNo) {
		return scheduledTime.isBeforeNow();
	}

	@Override
	public String toString() {
		return "Time : " + scheduledTime.getHourOfDay() + ":" + scheduledTime.getMinuteOfHour();
	}
}
