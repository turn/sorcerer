/*
 * Copyright (c) 2015, Turn Inc. All Rights Reserved.
 * Use of this source code is governed by a BSD-style license that can be found
 * in the LICENSE file.
 */

package com.turn.sorcerer.metrics;

import java.util.Map;

import com.google.common.base.Joiner;
import com.google.common.collect.Maps;

/**
 * MetricUnit contains all information needed to generate metrics
 * for different monitoring dashboards
 *
 * @author mzhang
 */
public class MetricUnit {

	private static final String MODULE_NAME = "Audience_Reports";
	private static final String SERVICE_PREFIX = "Workflow";

	public static final String METRIC_NAME_KEY = "Metric_Name";
	public static final String TASK_NAME_KEY = "Task_Name";
	public static final String PIPELINE_NAME_KEY = "Pipeline_Name";

	private static final String GRAPHITE_KEY = "Graphite_Key";
	private static final String SUCCESSFUL_FINISH_TIME_KEY = "Successful_Finish_Time";

	// TODO: remove once graphite is retired
	private boolean graphiteOnly = false;

	private Map<String, String> keyUnits;

	public MetricUnit(boolean graphiteOnly) {
		this.graphiteOnly = graphiteOnly;
		keyUnits = Maps.newHashMap();
	}

	public void setMetricNameForTask(String metricName, String taskName) {
		if (graphiteOnly) {
			keyUnits.put(GRAPHITE_KEY, metricName);
		} else {
			keyUnits.put(METRIC_NAME_KEY, metricName);
			keyUnits.put(TASK_NAME_KEY, taskName);
		}
	}

	public void setMetricNameForPipeline(String metricName, String pipelineName) {
		if (graphiteOnly) {
			keyUnits.put(GRAPHITE_KEY, metricName);
		} else {
			keyUnits.put(METRIC_NAME_KEY, metricName);
			keyUnits.put(PIPELINE_NAME_KEY, pipelineName);
		}
	}

	public void addTags(String tagKey, String tagValue) {
		keyUnits.put(tagKey, tagValue);
	}
	/**
	 * Utility class to generate keys for different monitoring dashboard
	 *
	 */
	public String getGraphiteKey() {
		if (graphiteOnly) {
			return keyUnits.get(GRAPHITE_KEY);
		}

		String tagName = "";
		if (keyUnits.get(TASK_NAME_KEY) != null) {
			tagName = keyUnits.get(TASK_NAME_KEY);
		} else if (keyUnits.get(PIPELINE_NAME_KEY) != null) {
			tagName = keyUnits.get(PIPELINE_NAME_KEY);
		}
		Joiner joiner = Joiner.on('.');
		return joiner.join(SERVICE_PREFIX, tagName, keyUnits.get(METRIC_NAME_KEY));
	}

	public String getModuleName() {
		return MODULE_NAME;
	}

	public String getTSDBKey() {
		return Joiner.on('.').join(SERVICE_PREFIX, keyUnits.get(METRIC_NAME_KEY));
	}

	public String getTSDBTags() {
		String tags = "";
		if (keyUnits.get(TASK_NAME_KEY) != null) {
			tags = Joiner.on('=').join(TASK_NAME_KEY, keyUnits.get(TASK_NAME_KEY));
		} else if (keyUnits.get(PIPELINE_NAME_KEY) != null) {
			tags = Joiner.on('=').join(PIPELINE_NAME_KEY, keyUnits.get(PIPELINE_NAME_KEY));
		}
		return tags;
	}

	public boolean isGraphiteOnly() {
		return graphiteOnly;
	}

	public String getTaskName() {
		return keyUnits.get(TASK_NAME_KEY);
	}

	public static MetricUnit getMetricUnit(boolean graphiteOnly, String taskName, String metricName) {
		MetricUnit unit = new MetricUnit(graphiteOnly);
		unit.setMetricNameForTask(metricName, taskName);
		return unit;
	}

	@Override
	public int hashCode() {
		return keyUnits.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof MetricUnit) {
			MetricUnit unit = (MetricUnit) obj;
			for (String key: this.keyUnits.keySet()) {
				if (!unit.keyUnits.containsKey(key)) {
					return false;
				} else if (unit.keyUnits.get(key).equals(this.keyUnits.get(key))){
					return false;
				}
			}
			return true;
		}
		return false;
	}
}
