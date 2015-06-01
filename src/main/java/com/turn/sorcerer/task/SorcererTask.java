/*
 * Copyright (c) 2015, Turn Inc. All Rights Reserved.
 * Use of this source code is governed by a BSD-style license that can be found
 * in the LICENSE file.
 */

package com.turn.sorcerer.task;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Indicates an implementation of a Sorcerer Task to be registered by Sorcerer.
 *
 * <p>
 * The {@code name} field of the annotation is required. Its value is used by
 * Sorcerer to map a task configuration to its implementation.
 * </p>
 *
 * @author tshiou
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface SorcererTask {
	public String name();
}
