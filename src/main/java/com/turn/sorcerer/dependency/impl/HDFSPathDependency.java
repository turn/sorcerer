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
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Class Description Here
 *
 * @author tshiou
 */
public abstract class HDFSPathDependency implements Dependency {
	private static final Logger logger =
			LogManager.getFormatterLogger(HDFSPathDependency.class);

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
				logger.error(e);
				return false;
			}
		}


		for (String p: paths) {
			logger.debug("Checking path %s", p);

			if (p == null) {
				return false;
			}

			Path path = new Path(p);

			try {
				if (!fs.exists(path)) {
					logger.debug("Dependency Check Failed - Path does not exist: " + path.toUri());
					return false;
				}
			} catch (IOException e) {
				logger.error("Could not check existence of path %s", path);
				return false;
			}
		}

		return true;
	}

	public abstract Collection<String> paths(int iterNo);
}
