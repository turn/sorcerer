/*
 * Copyright (c) 2015, Turn Inc. All Rights Reserved.
 * Use of this source code is governed by a BSD-style license that can be found
 * in the LICENSE file.
 */

package com.turn.sorcerer.exception;

/**
 * Class Description Here
 *
 * @author tshiou
 */
public class SorcererException extends Exception {

	public SorcererException() {
		super();
	}

	public SorcererException(String message) {
		super(message);
	}

	public SorcererException(Throwable t) {
		super(t);
	}

	public SorcererException(String message, Throwable t) {
		super(message, t);
	}
}
