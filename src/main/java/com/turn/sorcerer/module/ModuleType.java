/*
 * Copyright (C) 2015 Turn Inc. All Rights Reserved.
 * Proprietary and confidential.
 */

package com.turn.sorcerer.module;

import com.turn.sorcerer.status.type.StatusStorageType;
import com.turn.sorcerer.util.email.EmailType;

import java.util.List;

import com.google.common.base.MoreObjects;

/**
 * Class Description Here
 *
 * @author tshiou
 */
public class ModuleType {
	private String name;

	private List<String> pipelines;

	private EmailType email = new EmailType();

	private Integer retention = 0;

	private List<String> packages;

	private StatusStorageType storage;

	public String getName() {
		return this.name;
	}

	public List<String> getPackages() {
		return this.packages;
	}

	public StatusStorageType getStorage() {
		return this.storage;
	}

	public EmailType getAdminEmail() {
		return this.email;
	}

	public String toString() {
		return MoreObjects.toStringHelper(this)
				.add("name", this.name)
				.add("pipelines", this.pipelines)
				.add("email", this.email.isEnabled() ? this.email.toString() : "disabled")
				.add("storage", this.storage.name())
				.toString();
	}
}
