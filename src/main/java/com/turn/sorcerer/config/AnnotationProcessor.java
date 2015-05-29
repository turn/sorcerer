/*
 * Copyright (c) 2015, Turn Inc. All Rights Reserved.
 * Use of this source code is governed by a BSD-style license that can be found
 * in the LICENSE file.
 */

package com.turn.sorcerer.config;

import java.util.Collection;

/**
 * Class Description Here
 *
 * @author tshiou
 */
public interface AnnotationProcessor {

	void process(Collection<String> ... pkgs);
}
