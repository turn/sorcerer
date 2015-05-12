/*
 * Copyright (C) 2015 Turn Inc. All Rights Reserved.
 * Proprietary and confidential.
 */

package com.turn.sorcerer.dependency.impl;

import com.turn.sorcerer.dependency.Dependency;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.joda.time.DateTime;

/**
 * Class Description Here
 *
 * @author tshiou
 */
public class TimeDependency implements Dependency {
	private static final Logger logger =
			LogManager.getFormatterLogger(TimeDependency.class);

	private DateTime scheduledTime;

	public TimeDependency(DateTime dateTime) {
		this.scheduledTime = dateTime;
	}

	@Override
	public boolean check(int iterNo) {

		int scheduledHour = scheduledTime.getHourOfDay();
		int scheduledMinute = scheduledTime.getMinuteOfHour();

		// Compare with current time
		DateTime calendar = new DateTime();
		int currentHour = calendar.getHourOfDay();
		int currentMinute = calendar.getMinuteOfHour();

		if (currentHour > scheduledHour ||
				(currentHour == scheduledHour && currentMinute >= scheduledMinute)) {
			logger.info("Time dependency success at %s %s", currentHour, currentMinute);
			return true;
		}

		return false;
	}

	@Override
	public String toString() {
		return "Time : " + scheduledTime.getHourOfDay() + ":" + scheduledTime.getMinuteOfHour();
	}
}
