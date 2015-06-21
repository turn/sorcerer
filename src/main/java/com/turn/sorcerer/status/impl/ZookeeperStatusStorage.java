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
import java.util.List;

import com.google.common.base.Joiner;
import com.google.inject.BindingAnnotation;
import com.google.inject.Inject;
import com.google.inject.name.Named;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.RetryUntilElapsed;
import org.apache.curator.utils.ZKPaths;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Zookeeper persistent storage layer implementation
 *
 * @author tshiou
 */
public class ZookeeperStatusStorage implements StatusStorage {
	private static final Logger logger =
			LoggerFactory.getLogger(ZookeeperStatusStorage.class);

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

	private CuratorFramework curator;
	private boolean initialized = false;

	private static final Joiner PATH = Joiner.on('/').skipNulls();

	@Override
	public void init() throws IOException {
		logger.debug("Initializing Zookeeper storage: {}", connectionString);

		curator = CuratorFrameworkFactory.newClient(
				connectionString, sessionTimeout, connectionTimeout,
				new RetryUntilElapsed(retryDuration, retryInterval)
		);
		curator.start();
		initialized = true;

		try {
			ZKPaths.mkdirs(curator.getZookeeperClient().getZooKeeper(), ZKPaths.makePath(root, type));
		} catch (Exception e) {
			throw new IOException(e);
		}
	}

	@Override
	public StatusStorage setType(String type) {
		this.type = type;
		return this;
	}

	@Override
	public DateTime getLastUpdateTime(String identifier, int id) throws IOException {

		String path = PATH.join(root, type, identifier, id);

		try {
			if (curator.checkExists().forPath(path) == null) {
				return new DateTime(0);
			}

			return new DateTime(curator.checkExists().forPath(path).getMtime());

		} catch (Exception e) {
			throw new IOException(e);
		}
	}

	@Override
	public DateTime getStatusUpdateTime(String identifier, int id, Status status) throws IOException {

		String path = PATH.join(root, type, identifier, id, status);

		try {
			if (curator.checkExists().forPath(path) == null) {
				return new DateTime(0);
			}

			return new DateTime(curator.checkExists().forPath(path).getCtime());

		} catch (Exception e) {
			throw new IOException(e);
		}

	}

	@Override
	public int getCurrentIterNo(String identifier) throws IOException {

		String path = PATH.join(root, type, identifier);

		int maxIterNo = 0;
		List<String> children;

		try {
			if (curator.checkExists().forPath(path) == null) {
				return 0;
			}

			children = curator.getChildren().forPath(path);

		} catch (Exception e) {
			throw new IOException(e);
		}

		for (String child : children) {
			try {
				maxIterNo = Math.max(Integer.parseInt(child), maxIterNo);
			} catch (NumberFormatException nfe) {
				continue;
			}
		}

		return maxIterNo;
	}

	@Override
	public Status checkStatus(String identifier, int id) throws IOException {

		String path = PATH.join(root, type, identifier, id);

		List<String> children;

		try {
			if (curator.checkExists().forPath(path) == null) {
				return Status.PENDING;
			}

			children = curator.getChildren().forPath(path);

		} catch (Exception e) {
			throw new IOException(e);
		}

		if (children.contains(Status.SUCCESS.toString())) {
			return Status.SUCCESS;
		}

		if (children.contains(Status.IN_PROGRESS.toString())) {
			return Status.IN_PROGRESS;
		}

		if (children.contains(Status.ERROR.toString())) {
			return Status.ERROR;
		}

		return Status.PENDING;
	}

	@Override
	public void clearAllStatuses(String identifier, int jobId) throws IOException {
		String path = PATH.join(root, type, identifier, jobId);

		try {
			if (curator.checkExists().forPath(path) == null) {
				return;
			}

			curator.delete().forPath(path);

		} catch (Exception e) {
			throw new IOException(e);
		}
	}

	@Override
	public void removeStatus(String identifier, int jobId, Status status) throws IOException {

		String path = PATH.join(root, type, identifier, jobId, status);

		try {
			if (curator.checkExists().forPath(path) == null) {
				return;
			}

			curator.delete().forPath(path);

		} catch (Exception e) {
			throw new IOException(e);
		}
	}

	@Override
	public void commitStatus(String identifier, int jobId, Status status, DateTime time, boolean overwrite)
			throws IOException {
		String path = PATH.join(root, type, identifier, jobId, status);

		try {
			ZKPaths.mkdirs(curator.getZookeeperClient().getZooKeeper(), path);

			curator.checkExists().forPath(path).setCtime(time.getMillis());

		} catch (Exception e) {
			throw new IOException(e);
		}

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
