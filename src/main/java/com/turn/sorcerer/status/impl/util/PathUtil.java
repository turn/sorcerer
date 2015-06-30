/*
 * Copyright (C) 2015 Turn Inc. All Rights Reserved.
 * Proprietary and confidential.
 */

package com.turn.sorcerer.status.impl.util;

/**
 * Static methods for path manipulation
 *
 * @author tshiou
 */
public class PathUtil {

	public static String stripPrePostSlashes(String s) {
		String ret = s;
		if (s.charAt(0) == '/') {
			ret = s.substring(1);
		}
		return stripPostSlash(ret);
	}

	public static String stripPostSlash(String s) {
		String ret = s;
		if (s.charAt(s.length() - 1) == '/') {
			ret = ret.substring(0, ret.length() - 1);
		}
		return ret;
	}
}
