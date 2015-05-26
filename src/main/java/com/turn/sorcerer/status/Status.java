/*
 * Copyright (C) 2015 Turn Inc. All Rights Reserved.
 * Proprietary and confidential.
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
