/*
 * Copyright (c) 2015, Turn Inc. All Rights Reserved.
 * Use of this source code is governed by a BSD-style license that can be found
 * in the LICENSE file.
 */

package com.turn.sorcerer.status;

/**
 * Audience reporting service status enumerations
 *
 * @author tshiou
 */
public enum Status {

	PENDING("PENDING"),
	IN_PROGRESS("RUNNING"),
	SUCCESS("SUCCESS"),
	ERROR("ERROR"),
	;

	String string;

	private Status(String string) {
		this.string = string;
	}

	public String getString() {
		return string;
	}
}
