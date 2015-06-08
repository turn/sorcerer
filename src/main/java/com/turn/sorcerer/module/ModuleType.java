/*
 * Copyright (c) 2015, Turn Inc. All Rights Reserved.
 * Use of this source code is governed by a BSD-style license that can be found
 * in the LICENSE file.
 */

package com.turn.sorcerer.module;

import com.turn.sorcerer.status.type.StatusStorageType;
import com.turn.sorcerer.util.email.EmailType;

import java.util.List;

import com.google.common.base.Objects;

/**
 * Module type object created from Sorcerer configuration
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

	public List<String> getPipelines() {
		return this.pipelines;
	}

	public String toString() {
		return Objects.toStringHelper(this)
				.add("name", this.name)
				.add("pipelines", this.pipelines)
				.add("email", this.email.isEnabled() ? this.email.toString() : "disabled")
				.add("storage", this.storage.name())
				.toString();
	}
}
