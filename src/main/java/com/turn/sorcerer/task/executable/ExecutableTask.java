/*
 * Copyright (c) 2015, Turn Inc. All Rights Reserved.
 * Use of this source code is governed by a BSD-style license that can be found
 * in the LICENSE file.
 */

package com.turn.sorcerer.task.executable;

import com.turn.sorcerer.dependency.Dependency;
import com.turn.sorcerer.exception.SorcererException;
import com.turn.sorcerer.status.Status;
import com.turn.sorcerer.status.StatusManager;
import com.turn.sorcerer.task.Context;
import com.turn.sorcerer.task.Task;
import com.turn.sorcerer.task.type.TaskType;
import com.turn.sorcerer.util.Constants;

import com.google.common.collect.ImmutableList;

/**
 * Class Description Here
 *
 * @author tshiou
 */
public class ExecutableTask {

	private boolean adhoc = false;

	private final int sequenceNumber;
	private final TaskType type;
	private final Task task;

	protected ExecutableTask(TaskType type, Task task, int seq) {
		this.type = type;
		this.task = task;
		this.sequenceNumber = seq;
	}

	public void parameterize(Context context) {

		this.adhoc = context.getProperties().getBool(Constants.ADHOC);

		task.init(context);
	}

	public boolean checkDependencies() {
		// If the task doesn't have dependencies, return true
		if (task.getDependencies(sequenceNumber) == null ||
				task.getDependencies(sequenceNumber).size() == 0) {
			return true;
		}
		ImmutableList<Dependency> dependencies =
				ImmutableList.copyOf(task.getDependencies(sequenceNumber));

		// If any dependencies not met, return false
		for (Dependency dependency : dependencies) {
			if (dependency.check(sequenceNumber) == false) {
				return false;
			}
		}

		return true;
	}

	public void execute(Context context) throws SorcererException {
		try {
			StatusManager.get().commitTaskStatus(this.type, sequenceNumber, Status.IN_PROGRESS);

			task.exec(context);

		} catch (Exception e) {
			StatusManager.get()
					.removeInProgressTaskStatus(this.type, sequenceNumber);
			if (!adhoc) {
				StatusManager.get().commitTaskStatus(
						this.type, sequenceNumber, Status.ERROR, true);
			}
			throw new SorcererException(e);
		}

		StatusManager.get().commitTaskStatus(this.type, sequenceNumber, Status.SUCCESS, true);
		StatusManager.get().removeInProgressTaskStatus(this.type, sequenceNumber);

	}

	public boolean isCompleted() {
		return StatusManager.get().isTaskComplete(this.type, this.sequenceNumber);
	}

	public boolean isRunning() {
		return StatusManager.get().isTaskRunning(this.type, this.sequenceNumber);
	}

	public boolean hasError() {
		return StatusManager.get().isTaskInError(this.type, this.sequenceNumber);
	}

	public String name() {
		return this.type.getName();
	}

	@Override
	public String toString() {
		return name() + ":" + sequenceNumber;
	}
}
