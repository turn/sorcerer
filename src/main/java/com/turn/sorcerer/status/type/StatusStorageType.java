/*
 * Copyright (C) 2015 Turn Inc. All Rights Reserved.
 * Proprietary and confidential.
 */

package com.turn.sorcerer.status.type;

import com.turn.sorcerer.status.StatusStorage;

/**
 * Class Description Here
 *
 * @author tshiou
 */
public interface StatusStorageType {

	Class<? extends StatusStorage> getStorageClass();

	String name();
}
