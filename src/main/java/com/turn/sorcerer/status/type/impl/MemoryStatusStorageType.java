/*
 * Copyright (C) 2015 Turn Inc. All Rights Reserved.
 * Proprietary and confidential.
 */

package com.turn.sorcerer.status.type.impl;

import com.turn.sorcerer.status.StatusStorage;
import com.turn.sorcerer.status.type.StatusStorageType;
import com.turn.sorcerer.status.impl.MemoryStatusStorage;

/**
 * Class Description Here
 *
 * @author tshiou
 */
public class MemoryStatusStorageType implements StatusStorageType {

	private String dummy;

	@Override
	public Class<? extends StatusStorage> getStorageClass() {
		return MemoryStatusStorage.class;
	}

	@Override
	public String name() {
		return "In-memory status storage";
	}
}
