/*
 * Copyright (c) 2015, Turn Inc. All Rights Reserved.
 * Use of this source code is governed by a BSD-style license that can be found
 * in the LICENSE file.
 */

package com.turn.sorcerer.util;

import java.text.ParseException;

import org.joda.time.DateTime;

/**
 * Joda-time wrapper around quartz CronExpression
 *
 * @author tshiou
 */
public class CronExpression {

	private org.quartz.CronExpression cronExpression;

	public CronExpression(String cronString) {
		try {
			cronExpression = new org.quartz.CronExpression(cronString);
		} catch (ParseException e) {
			e.printStackTrace();
		}
	}

	public DateTime getNextTimeAfter(DateTime dt) {
		return new DateTime(cronExpression.getNextValidTimeAfter(dt.toDate()));
	}
}
