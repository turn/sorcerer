/*
 * Copyright (c) 2015, Turn Inc. All Rights Reserved.
 * Use of this source code is governed by a BSD-style license that can be found
 * in the LICENSE file.
 */

package com.turn.sorcerer.dependency.impl;

import com.turn.sorcerer.dependency.Dependency;

import java.io.IOException;
import java.util.Collection;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Task Dependency implementation based on the existence of a path in HDFS
 *
 * @author tshiou
 */
public abstract class HDFSPathDependency implements Dependency {
	private static final Logger logger =
			LoggerFactory.getLogger(HDFSPathDependency.class);

	private FileSystem fs = null;

	@Override
	public boolean check(int iterNo){

		Collection<String> paths = paths(iterNo);

		// zero paths is considered invalid
		if (paths == null || paths.size() == 0) {
			return false;
		}

		if (fs == null) {
			try {
				fs = FileSystem.get(new Configuration());
			} catch (IOException e) {
				logger.error("Filesystem unreachable!", e);
				return false;
			}
		}


		for (String p: paths) {
			logger.debug("Checking path {}", p);

			if (p == null) {
				return false;
			}

			Path path = new Path(p);

			try {
				if (!fs.exists(path)) {
					logger.debug("Dependency Check Failed - Path does not exist: {}", path.toUri());
					return false;
				}
			} catch (IOException e) {
				logger.error("Could not check existence of path {}", path);
				return false;
			}
		}

		return true;
	}

	public abstract Collection<String> paths(int iterNo);
}
