/*
 * Copyright (c) 2015, Turn Inc. All Rights Reserved.
 * Use of this source code is governed by a BSD-style license that can be found
 * in the LICENSE file.
 */

package com.turn.sorcerer.pipeline.impl;

import org.joda.time.DateTime;
import org.joda.time.DateTimeUtils;
import org.testng.Assert;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

/**
 * CronPipeline tests
 *
 * @author tshiou
 */
@Test
public class CronPipelineTest {

	// Every hour on the hour
	private static final String CRON_EXP = "0 0 * * * ?";

	private CronPipeline testPipeline;

	@BeforeTest
	public void setupTest() {

		// now = 01/15/2015 12:00:00
		DateTime now = new DateTime()
				.withYear(2015)
				.withMonthOfYear(1)
				.withDayOfMonth(15)
				.withHourOfDay(12)
				.withMinuteOfHour(0)
				.withSecondOfMinute(0);

		DateTimeUtils.setCurrentMillisFixed(now.getMillis());


		testPipeline = new CronPipeline(CRON_EXP);

		Assert.assertEquals(testPipeline.getCurrentIterationNumber().intValue(), -1);
	}

	@Test
	public void testCurrentIterationNumberFirstSuccess() {

		DateTime now = new DateTime()
				.withYear(2015)
				.withMonthOfYear(1)
				.withDayOfMonth(15)
				.withHourOfDay(14)
				.withMinuteOfHour(0)
				.withSecondOfMinute(1);

		DateTimeUtils.setCurrentMillisFixed(now.getMillis());

		Assert.assertEquals(testPipeline.getCurrentIterationNumber().intValue(), 1501150015);
	}

	@Test
	public void testCurrentIterationNumberDayBoundary() {
		DateTime now = new DateTime()
				.withYear(2015)
				.withMonthOfYear(1)
				.withDayOfMonth(16)
				.withHourOfDay(0)
				.withMinuteOfHour(0)
				.withSecondOfMinute(1);

		DateTimeUtils.setCurrentMillisFixed(now.getMillis());

		Assert.assertEquals(testPipeline.getCurrentIterationNumber().intValue(), 1501160001);
	}

	@Test
	public void testPreviousIterationNumber() {
		Assert.assertEquals(
				testPipeline.getPreviousIterationNumber(1501150003, 1).intValue(), 1501150002);
	}

	@Test
	void testPreviousIterationNumberDayBoundary() {
		Assert.assertEquals(
				testPipeline.getPreviousIterationNumber(1501150001, 1).intValue(), 1501140024);
		Assert.assertEquals(
				testPipeline.getPreviousIterationNumber(1501150001, 3).intValue(), 1501140022);
	}

	@Test
	void testPreviousIterationNumberMultipleDayBoundary() {
		Assert.assertEquals(
				testPipeline.getPreviousIterationNumber(1501150001, 25).intValue(), 1501130024);
		Assert.assertEquals(
				testPipeline.getPreviousIterationNumber(1501150001, 50).intValue(), 1501120023);
	}

}
