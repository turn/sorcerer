/*
 * Copyright (C) 2015 Turn Inc. All Rights Reserved.
 * Proprietary and confidential.
 */

package com.turn.sorcerer.task.type;

import com.turn.sorcerer.task.Task;
import com.turn.sorcerer.task.impl.ForkTask;
import com.turn.sorcerer.task.impl.JoinTask;

import java.util.List;

import com.google.common.base.MoreObjects;
import com.google.common.collect.ImmutableMap;

/**
 * Class Description Here
 *
 * @author tshiou
 */
public class TaskType {

	public static final String FORK_EXEC = "fork";
	public static final String JOIN_EXEC = "join";

	public static final ImmutableMap<String, Class<? extends Task>> SUPPORTED_EXEC =
			ImmutableMap.<String, Class<? extends Task>>builder()
			.put(FORK_EXEC, ForkTask.class)
			.put(JOIN_EXEC, JoinTask.class)
			.build();

	public static enum CRITICALITY {
		HIGH,
		LOW
	}

	private boolean enabled = true;

	private String name;

	private List<String> next;

	private String exec = "class";

	private CRITICALITY criticality = CRITICALITY.HIGH;

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

	public String toString() {
		return MoreObjects.toStringHelper(this)
				.add("name", this.name)
				.add("next", this.next)
				.add("exec", this.exec)
				.add("criticality", this.criticality.name())
				.toString();
	}

	@Override
	public int hashCode() {
		return name.hashCode();
	}

	@Override
	public boolean equals(Object o) {

		return o instanceof TaskType && this.getName().equals(((TaskType) o).getName());

	}
}
