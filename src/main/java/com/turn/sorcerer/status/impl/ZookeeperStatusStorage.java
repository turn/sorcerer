/*
 * Copyright (c) 2015, Turn Inc. All Rights Reserved.
 * Use of this source code is governed by a BSD-style license that can be found
 * in the LICENSE file.
 */

package com.turn.sorcerer.status.impl;

import com.turn.sorcerer.status.Status;
import com.turn.sorcerer.status.StatusStorage;

import java.io.IOException;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.google.inject.BindingAnnotation;
import com.google.inject.Inject;
import com.google.inject.name.Named;
import org.joda.time.DateTime;

/**
 * Zookeeper persistent storage layer implementation
 *
 * @author tshiou
 */
public class ZookeeperStatusStorage implements StatusStorage {

	private String type = "default";

	@Inject
	@ZookeeperStatusStorage.StorageRoot
	private String root;

	@Inject
	@ZookeeperStatusStorage.ConnectionString
	private String connectionString;

	@Inject(optional = true)
	@Named(SESSION_TIMEOUT)
	private Integer sessionTimeout = 60000;

	@Inject(optional = true)
	@Named(CONNECTION_TIMEOUT)
	private Integer connectionTimeout = 60000;

	@Inject(optional = true)
	@Named(RETRY_DURATION)
	private Integer retryDuration;

	@Inject(optional = true)
	@Named(RETRY_INTERVAL)
	private Integer retryInterval;

	@Override
	public void init() throws IOException {

	}

	@Override
	public StatusStorage setType(String type) {
		this.type = type;
		return this;
	}

	@Override
	public DateTime getLastUpdateTime(String identifier, int id) throws IOException {
		return null;
	}

	@Override
	public DateTime getStatusUpdateTime(String identifier, int id, Status status) throws IOException {
		return null;
	}

	@Override
	public int getCurrentIterNo(String identifier) throws IOException {
		return 0;
	}

	@Override
	public Status checkStatus(String identifier, int id) throws IOException {
		return null;
	}

	@Override
	public void clearAllStatuses(String identifier, int jobId) throws IOException {

	}

	@Override
	public void removeStatus(String identifier, int jobId, Status status) throws IOException {

	}

	@Override
	public void commitStatus(String identifier, int jobId, Status status, DateTime time, boolean overwrite)
			throws IOException {

	}

	@Retention(RetentionPolicy.RUNTIME)
	@Target({ElementType.FIELD, ElementType.PARAMETER})
	@BindingAnnotation
	public @interface ConnectionString {}

	@Retention(RetentionPolicy.RUNTIME)
	@Target({ElementType.FIELD, ElementType.PARAMETER})
	@BindingAnnotation
	public @interface StorageRoot {}

	public static final String SESSION_TIMEOUT = "zk_session_timeout";
	public static final String CONNECTION_TIMEOUT = "zk_connection_timeout";
	public static final String RETRY_DURATION = "zk_retry_duration";
	public static final String RETRY_INTERVAL = "zk_retry_interval";
}
