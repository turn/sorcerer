/*
 * Copyright (C) 2015 Turn Inc. All Rights Reserved.
 * Proprietary and confidential.
 */

package com.turn.sorcerer.status;

import java.io.IOException;

import org.joda.time.DateTime;

/**
 * Class Description Here
 *
 * @author tshiou
 */
public interface StatusStorage {

	String toString();

	void init() throws IOException;

	StatusStorage setType(String type);

	DateTime getLastUpdateTime(String identifier, int id) throws IOException;

	DateTime getStatusUpdateTime(String identifier, int id, Status status) throws IOException;

	int getCurrentIterNo(String identifier) throws IOException;

	Status checkStatus(String identifier, int id) throws IOException;

	void clearAllStatuses(String identifier, int jobId) throws IOException;

	void removeStatus(String identifier, int jobId, Status status) throws IOException;

	void commitStatus(String identifier, int jobId, Status status, DateTime time, boolean overwrite) throws IOException;

}
