/*
 * Copyright (C) 2015 Turn Inc. All Rights Reserved.
 * Proprietary and confidential.
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
