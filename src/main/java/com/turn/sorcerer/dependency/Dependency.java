/**
 * Copyright (C) 2015 Turn Inc. All Rights Reserved.
 * Proprietary and confidential.
 */

package com.turn.sorcerer.dependency;

public interface Dependency {

	boolean check(int iterNo);

	String toString();
}
