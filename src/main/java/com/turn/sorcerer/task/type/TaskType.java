/*
 * Copyright (c) 2015, Turn Inc. All Rights Reserved.
 * Use of this source code is governed by a BSD-style license that can be found
 * in the LICENSE file.
 */

package com.turn.sorcerer.task.type;

import com.turn.sorcerer.exception.SorcererException;
import com.turn.sorcerer.task.Task;
import com.turn.sorcerer.task.impl.ForkTask;
import com.turn.sorcerer.task.impl.JoinTask;

import java.util.List;

import com.google.common.base.Objects;
import com.google.common.collect.ImmutableMap;

/**
 * Represents a task definition. Instances are automatically created and fields
 * are populated by the Sorcerer configuration processor
 *
 * @author tshiou
 */
public class TaskType {

	private boolean enabled = true;

	private String name;

	private List<String> next;

	private String exec = "class";

	private CRITICALITY criticality = CRITICALITY.HIGH;

	private String sla;

	private Integer _sla_seconds;

	private boolean external = false;

	public TaskType() {}

	// For mocking and testing
	// SHOULD NEVER BE CALLED in production
	public TaskType(String name, List<String> next, String exec) {
		this.name = name;
		this.next = next;
		this.exec = exec == null ? this.exec : exec;
	}

	public boolean isEnabled() {
		return this.enabled;
	}

	public String getName() {
		return this.name;
	}

	public String getExec() {
		return this.exec.toLowerCase();
	}

	public List<String> getNextTaskNames() {
		return this.next;
	}

	public CRITICALITY getCriticality() {
		return this.criticality;
	}

	public boolean isExternalJob() {
		return this.external;
	}

	public int getSLA() {
		if (_sla_seconds == null) {
			try {
				initSLA();
			} catch (SorcererException e) {
				_sla_seconds = -1;
			}
		}

		return _sla_seconds;
	}

	public void initSLA() throws SorcererException {
		try {
			_sla_seconds = parseSLA(sla);
		} catch (Exception e) {
			throw new SorcererException("Incorrectly formatted SLA field for task " + this.name, e);
		}
	}

	public static int parseSLA(String slaString) throws Exception {
		if (slaString == null || slaString.trim().length() == 0) {
			return -1;
		}

		String trimmed = slaString.trim().toLowerCase();

		char unit = trimmed.charAt(trimmed.length() - 1);

		int num = Integer.parseInt(trimmed.substring(0, trimmed.length() - 2));

		switch (unit) {
			case 'd':
				return num * SEC_IN_DAY;
			case 'h':
				return num * SEC_IN_HOUR;
			case 'm':
				return num * SEC_IN_MINUTE;
			case 's':
				return num;
			default:
				throw new Exception("Invalid sla unit provided: " + unit);
		}
	}

	public String toString() {
		return Objects.toStringHelper(this)
				.add("name", this.name)
				.add("next", this.next)
				.add("exec", this.exec)
				.add("criticality", this.criticality.name())
				.add("sla", this.sla)
				.toString();
	}

	/**
	 * The name field will be unique among the tasks so we use it for hashing
	 */
	@Override
	public int hashCode() {
		return name.hashCode();
	}

	@Override
	public boolean equals(Object o) {
		return o instanceof TaskType
				&& this.getName().equals(((TaskType) o).getName());

	}


	/**
	 * Supported task execution types
	 */
	public static final String FORK_EXEC = "fork";
	public static final String JOIN_EXEC = "join";

	public static final ImmutableMap<String, Class<? extends Task>> SUPPORTED_EXEC =
			ImmutableMap.<String, Class<? extends Task>>builder()
					.put(FORK_EXEC, ForkTask.class)
					.put(JOIN_EXEC, JoinTask.class)
					.build();

	/**
	 * Task criticality
	 */
	public enum CRITICALITY {
		HIGH,
		LOW
	}

	/**
	 *
	 */
	private static final int SEC_IN_DAY = 86400;
	private static final int SEC_IN_HOUR = 3600;
	private static final int SEC_IN_MINUTE = 60;
}
