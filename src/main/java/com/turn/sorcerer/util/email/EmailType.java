/*
 * Copyright (c) 2015, Turn Inc. All Rights Reserved.
 * Use of this source code is governed by a BSD-style license that can be found
 * in the LICENSE file.
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
