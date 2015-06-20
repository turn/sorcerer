/*
 * Copyright (c) 2015, Turn Inc. All Rights Reserved.
 * Use of this source code is governed by a BSD-style license that can be found
 * in the LICENSE file.
 */

package com.turn.sorcerer.status.type.impl;

import com.turn.sorcerer.status.StatusStorage;
import com.turn.sorcerer.status.impl.ZookeeperStatusStorage;
import com.turn.sorcerer.status.type.StatusStorageType;

/**
 * Zookeeper persistent storage layer type
 *
 * @author tshiou
 */
public class ZookeeperStatusStorageType implements StatusStorageType {

	private String connection;

	// Default to root
	private String root = "/";

	// Default session timeout = 1 min
	private int session_timeout = 60000;

	// Default connection timeout = 1 min
	private int connection_timeout = 60000;

	// Default retry interval = 5 sec
	private int retry_interval = 5000;

	// Default retry duration = 1 min
	private int retry_duration = 60000;

	public String getRoot() {
		return this.root;
	}

	public String getConnectionString() {
		return this.connection;
	}

	public int getSessionTimeout() {
		return this.session_timeout;
	}

	public int getConnectionTimeout() {
		return this.connection_timeout;
	}

	public int getRetryInterval() {
		return this.retry_interval;
	}

	public int getRetryDuration() {
		return this.retry_duration;
	}

	@Override
	public Class<? extends StatusStorage> getStorageClass() {
		return ZookeeperStatusStorage.class;
	}

	@Override
	public String name() {
		return "Zookeeper@"  + root;
	}
}
