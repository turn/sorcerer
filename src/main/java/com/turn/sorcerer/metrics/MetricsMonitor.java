/*
 * Copyright (c) 2015, Turn Inc. All Rights Reserved.
 * Use of this source code is governed by a BSD-style license that can be found
 * in the LICENSE file.
 */

package com.turn.sorcerer.metrics;

import com.turn.sorcerer.status.StatusManager;

import java.util.Map;

import com.google.common.collect.Maps;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Class Description Here
 *
 * @author tshiou
 */
public class MetricsMonitor {
	private static final Logger logger =
			LoggerFactory.getLogger(MetricsMonitor.class);

	private final Map<MetricUnit, Long> METRICS = Maps.newConcurrentMap();

	/** Singleton */
	private static final MetricsMonitor instance = new MetricsMonitor();

	private MetricsMonitor() {

	}

	public static MetricsMonitor getInstance() {
		return instance;
	}

	public Map<String, Object> getGraphiteMetrics() {
		Map<String, Object> metrics = Maps.newTreeMap();
		for (Map.Entry<MetricUnit, Long> entry: METRICS.entrySet()) {
			metrics.put(entry.getKey().getGraphiteKey(), entry.getValue());
		}
		return metrics;
	}

	public void addGenericMetric(MetricUnit unit, Long value) {
		instance.METRICS.put(unit, value);
		instance.pushOutMetrics();
	}

	public void addSuccessMetric(MetricUnit unit, int todayDateKey) {
		instance.updateMetricsFromTaskStatus(unit, todayDateKey);
		instance.pushOutMetrics();
	}

	private void updateMetricsFromTaskStatus(MetricUnit unit, int todayDateKey) {
		String taskName = unit.getTaskName();
		if (taskName == null) {
			logger.error("Metrics don't have a task name!");
			return;
		}

		// Check status from status file
		Long statusSuccessTime;
		statusSuccessTime = StatusManager.get().getTaskLastSuccessTime(taskName, todayDateKey).toInstant().getMillis();


		// If no status file found then return
		if (statusSuccessTime == -1) {
			return;
		}

		// Get last success time
		Long serviceSuccessTime = -1L;
		if (METRICS.containsKey(unit)) {
			serviceSuccessTime = METRICS.get(unit);
		}

		// Update metrics with latest success time
		if (statusSuccessTime > serviceSuccessTime) {
			logger.debug("{} status file modified time greater than existing last success time. " +
					"Replacing metric.", taskName);
			METRICS.put(unit, statusSuccessTime);
		}
	}

	public void pushOutMetrics() {
		for (Map.Entry<MetricUnit, Long> entry: METRICS.entrySet()) {
			MetricUnit key = entry.getKey();

			// some metrics are graphite only
			if (!key.isGraphiteOnly()) {
				pushOutTSDBMetrics(key, entry.getValue());
			}
		}
	}

	private void pushOutTSDBMetrics(MetricUnit unit, Long value) {

	}
}
