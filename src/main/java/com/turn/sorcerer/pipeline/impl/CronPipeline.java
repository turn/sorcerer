/*
 * Copyright (c) 2015, Turn Inc. All Rights Reserved.
 * Use of this source code is governed by a BSD-style license that can be found
 * in the LICENSE file.
 */

package com.turn.sorcerer.pipeline.impl;

import com.turn.sorcerer.pipeline.Pipeline;
import com.turn.sorcerer.util.CronExpression;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

/**
 * Cron implementation of Pipeline
 *
 * @author tshiou
 */
public class CronPipeline implements Pipeline {

	private static final DateTimeFormatter dtf = DateTimeFormat.forPattern("yyMMdd");

	private final CronExpression cronExp;
	private DateTime next;
	private int currIterNo;

	public CronPipeline(String cronString) {
		this.cronExp = new CronExpression(cronString);

		next = cronExp.getNextTimeAfter(DateTime.now());

		currIterNo = -1;
	}

	@Override
	public Integer getCurrentIterationNumber() {
		DateTime now = DateTime.now();

		if (now.isAfter(next)) {
			currIterNo = generateIterationNumber(now);

			next = cronExp.getNextTimeAfter(now);
		}

		return currIterNo;
	}

	@Override
	public Integer getPreviousIterationNumber(int curr, int prev) {
		DateTime currDate = getDateTime(curr);
		int currIterNo = getDayIterNo(curr);

		int iterationsLeft = prev;

		while(iterationsLeft >= 0) {

			if (currIterNo - iterationsLeft > 0) {
				return getIterationNumber(currDate, currIterNo - iterationsLeft);
			}

			iterationsLeft -= currIterNo;
			currDate = new DateTime(currDate).minusDays(1);
			currIterNo = getLastIterNoForDate(currDate);
		}

		return null;

	}

	private int generateIterationNumber(DateTime dt) {
		return getIterationNumber(dt, getDayIterNoForDateTime(dt));
	}

	private int getIterationNumber(DateTime dt, int dayIterNo) {
		int year = dt.getYear();
		int month = dt.getMonthOfYear();
		int day = dt.getDayOfMonth();

		return (((year % 100) * 10000) + (month * 100) + day) * 10000 + dayIterNo;
	}

	private DateTime getDateTime(int iterNo) {
		return dtf.parseDateTime(String.valueOf(iterNo / 10000));
	}

	private int getDayIterNo(int iterNo) {
		return iterNo % 10000;
	}

	private int getLastIterNoForDate(DateTime dt) {
		int iterNo = 0;
		DateTime _next = new DateTime()
				.withYear(dt.getYear())
				.withDayOfYear(dt.getDayOfYear())
				.withHourOfDay(0)
				.withMinuteOfHour(0)
				.withSecondOfMinute(0);

		while(_next.getDayOfMonth() == dt.getDayOfMonth()) {
			_next = cronExp.getNextTimeAfter(_next);
			iterNo++;
		}

		return iterNo;
	}

	private int getDayIterNoForDateTime(DateTime dt) {
		int iterNo = 0;
		DateTime _next = new DateTime()
				.withYear(dt.getYear())
				.withDayOfYear(dt.getDayOfYear())
				.withHourOfDay(0)
				.withMinuteOfHour(0)
				.withSecondOfMinute(0);

		while(_next.isBefore(dt)) {
			_next = cronExp.getNextTimeAfter(_next);
			iterNo++;
		}

		return iterNo;
	}
}
