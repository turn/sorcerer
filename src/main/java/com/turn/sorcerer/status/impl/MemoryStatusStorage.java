/*
 * Copyright (C) 2015 Turn Inc. All Rights Reserved.
 * Proprietary and confidential.
 */

package com.turn.sorcerer.status.impl;

import com.turn.sorcerer.status.Status;
import com.turn.sorcerer.status.StatusStorage;

import java.io.IOException;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.joda.time.DateTime;

/**
 * Class Description Here
 *
 * @author tshiou
 */
public class MemoryStatusStorage implements StatusStorage {

	private static final Logger logger =
			LogManager.getFormatterLogger(MemoryStatusStorage.class);

	private final Table<String, Integer, ConcurrentMap<Status, DateTime>> store;

	public MemoryStatusStorage() {
		logger.debug("New instance of memory status storage");
		store =	HashBasedTable.create();
	}

	@Override
	public DateTime getLastUpdateTime(String identifier, int id) throws IOException {
		ConcurrentMap<Status, DateTime> statuses = store.get(identifier, id);

		if (statuses == null) {
			return null;
		}

		DateTime lastDt = new DateTime().withYear(1970);

		for (DateTime dt : statuses.values()) {
			if (dt.isAfter(lastDt)) {
				lastDt = dt;
			}
		}

		return lastDt;
	}

	@Override
	public DateTime getStatusUpdateTime(String identifier, int id, Status status) throws IOException {
		ConcurrentMap<Status, DateTime> statuses = store.get(identifier, id);

		if (statuses != null) {
			return statuses.get(status);
		}

		return null;
	}

	@Override
	public int getCurrentIterNo(String identifier) throws IOException {
		int maxIterNo = 0;
		for (Integer i : store.columnKeySet()) {
			maxIterNo = Math.max(i, maxIterNo);
		}
		return maxIterNo;
	}

	@Override
	public Status checkStatus(String identifier, int id) throws IOException {
		ConcurrentMap<Status, DateTime> statuses = store.get(identifier, id);

		if (statuses == null || statuses.size() == 0) {
			return Status.DEFAULT;
		}

		Set<Status> stati = statuses.keySet();

		if (stati.contains(Status.SUCCESS)) {
			return Status.SUCCESS;
		}

		if (stati.contains(Status.IN_PROGRESS)) {
			return Status.IN_PROGRESS;
		}

		if (stati.contains(Status.ERROR)) {
			return Status.ERROR;
		}

		return Status.DEFAULT;
	}

	@Override
	public void clearAllStatuses(String identifier, int jobId) throws IOException {
		store.remove(identifier, jobId);
	}

	@Override
	public void removeStatus(String identifier, int jobId, Status status) throws IOException {
		ConcurrentMap<Status, DateTime> statuses = store.get(identifier, jobId);

		statuses.remove(status);
	}

	@Override
	public void commitStatus(String identifier, int jobId, Status status, DateTime time, boolean overwrite) throws IOException {
		ConcurrentMap<Status, DateTime> statuses = store.get(identifier, jobId);

		if (statuses == null || overwrite) {
			statuses = new ConcurrentHashMap<Status, DateTime>();
			store.put(identifier, jobId, statuses);
		}

		statuses.put(status, time);
	}
}
