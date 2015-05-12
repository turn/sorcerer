/*
 * Copyright (C) 2015 Turn Inc. All Rights Reserved.
 * Proprietary and confidential.
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
