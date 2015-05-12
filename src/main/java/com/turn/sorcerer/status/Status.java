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

	DEFAULT("_PENDING"),
	IN_PROGRESS("_RUNNING"),
	SUCCESS("_SUCCESS"),
	ERROR("_ERROR"),
	;

	String string;

	Status(String string) {
		this.string = string;
	}

	public String getString() {
		return string;
	}
}
