/*
 * Copyright (C) 2015 Turn Inc. All Rights Reserved.
 * Proprietary and confidential.
 */

package com.turn.sorcerer.util.email;

/**
 * Class Description Here
 *
 * @author tshiou
 */
public class EmailType {

	private boolean enabled = false;

	private String admin = "";

	private String host = "";

	public boolean isEnabled() {
		return this.enabled;
	}

	public String getAdmins() {
		return this.admin;
	}

	public String getHost() {
		return this.host;
	}

	public String toString() {
		return "enabled:" + this.enabled + " host:" + host + " admin:" + admin;
	}
}
