/*
 * Copyright (c) 2015, Turn Inc. All Rights Reserved.
 * Use of this source code is governed by a BSD-style license that can be found
 * in the LICENSE file.
 */

package com.turn.sorcerer.status.type.impl;

import com.turn.sorcerer.status.StatusStorage;
import com.turn.sorcerer.status.type.StatusStorageType;
import com.turn.sorcerer.status.impl.HDFSStatusStorage;

/**
 * Class Description Here
 *
 * @author tshiou
 */
public class HDFSStatusStorageType implements StatusStorageType {

	private String root;

	public String getRoot() {
		return this.root;
	}

	@Override
	public Class<? extends StatusStorage> getStorageClass() {
		return HDFSStatusStorage.class;
	}

	@Override
	public String name() {
		return "HDFS @ " + root;
	}
}
