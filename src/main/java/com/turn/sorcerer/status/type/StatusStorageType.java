/*
 * Copyright (c) 2015, Turn Inc. All Rights Reserved.
 * Use of this source code is governed by a BSD-style license that can be found
 * in the LICENSE file.
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
