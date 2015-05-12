/*
 * Copyright (C) 2015 Turn Inc. All Rights Reserved.
 * Proprietary and confidential.
 */

package com.turn.sorcerer.status;

import com.turn.sorcerer.injector.SorcererInjector;
import com.turn.sorcerer.pipeline.type.PipelineType;
import com.turn.sorcerer.task.type.TaskType;

import java.io.IOException;

import org.joda.time.DateTime;

/**
 * Class Description Here
 *
 * @author tshiou
 */
public class StatusManager {

	private static final StatusManager INSTANCE= new StatusManager();

	public static StatusManager get() {
		return INSTANCE;
	}

	StatusStorage taskStorage = SorcererInjector.get().getStorageInstance();
	StatusStorage pipelineStorage = SorcererInjector.get().getStorageInstance();

	private StatusManager() {

	}

	public void commitTaskStatus(TaskType type, int seq, Status status) {
		commitTaskStatus(type.getName(), seq, status, DateTime.now(), false);
	}

	public void commitTaskStatus(TaskType type, int seq, Status status, boolean overwrite) {
		commitTaskStatus(type.getName(), seq, status, DateTime.now(), overwrite);
	}

	private void commitTaskStatus(
			String taskName, int seq, Status status, DateTime time, boolean overwrite) {

		if (Status.DEFAULT.equals(status)) {
			try {
				taskStorage.clearAllStatuses(taskName, seq);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		try {
			taskStorage.commitStatus(taskName, seq, status, time, overwrite);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void removeInProgressTaskStatus(TaskType type, int seq) {
		removeInProgressTaskStatus(type.getName(), seq);
	}

	private void removeInProgressTaskStatus(String taskName, int seq) {
		try {
			taskStorage.removeStatus(taskName, seq, Status.IN_PROGRESS);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public boolean isTaskComplete(TaskType type, int seq) {
		try {
			return Status.SUCCESS.equals(taskStorage.checkStatus(type.getName(), seq));
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
	}

	public boolean isTaskRunning(TaskType type, int seq) {
		try {
			return Status.IN_PROGRESS.equals(taskStorage.checkStatus(type.getName(), seq));
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
	}

	public boolean isTaskInError(TaskType type, int seq) {
		try {
			return Status.ERROR.equals(taskStorage.checkStatus(type.getName(), seq));
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
	}

	public void clearTaskStatus(TaskType type, int seq) {
		try {
			taskStorage.clearAllStatuses(type.getName(), seq);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public DateTime getTaskLastSuccessTime(TaskType type, int seq) {
		return getTaskLastUpdateTimeForStatus(type, seq, Status.SUCCESS);
	}

	public DateTime getTaskLastSuccessTime(String taskName, int seq) {
		try {
			return taskStorage.getStatusUpdateTime(taskName, seq, Status.SUCCESS);
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}

	private DateTime getTaskLastUpdateTimeForStatus(TaskType type, int seq, Status status) {
		try {
			return taskStorage.getStatusUpdateTime(type.getName(), seq, status);
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}

	public int getCurrentIterationNumberForPipeline(PipelineType type) {
		return getCurrentIterationNumberForPipeline(type.getName());
	}

	public int getCurrentIterationNumberForPipeline(String pipelineName) {
		try {
			return pipelineStorage.getCurrentIterNo(pipelineName);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return -1;
	}

	public void commitPipelineStatus(PipelineType type, int seq, Status status) {
		commitPipelineStatus(type.getName(), seq, status, DateTime.now(), false);
	}

	public void commitPipelineStatus(PipelineType type, int seq, Status status, boolean overwrite) {
		commitPipelineStatus(type.getName(), seq, status, DateTime.now(), overwrite);
	}

	private void commitPipelineStatus(String taskName, int seq, Status status, DateTime time, boolean overwrite) {
		try {
			pipelineStorage.commitStatus(taskName, seq, status, time, overwrite);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public boolean isPipelineComplete(PipelineType type, int seq) {
		try {
			return Status.SUCCESS.equals(pipelineStorage.checkStatus(type.getName(), seq));
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
	}

	public void clearPipelineStatus(PipelineType type, int seq) {
		try {
			pipelineStorage.clearAllStatuses(type.getName(), seq);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}

