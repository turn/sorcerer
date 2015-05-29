/*
 * Copyright (c) 2015, Turn Inc. All Rights Reserved.
 * Use of this source code is governed by a BSD-style license that can be found
 * in the LICENSE file.
 */

package com.turn.sorcerer.task;

import com.turn.sorcerer.dependency.Dependency;

import java.util.Collection;

/**
 * Class Description Here
 *
 * @author tshiou
 */
public interface Task {
	void init(final Context context);

	void exec(final Context context) throws Exception;

	Collection<Dependency> getDependencies(int iterNo);
}
