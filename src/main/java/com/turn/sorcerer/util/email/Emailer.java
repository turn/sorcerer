/*
 * Copyright (c) 2015, Turn Inc. All Rights Reserved.
 * Use of this source code is governed by a BSD-style license that can be found
 * in the LICENSE file.
 */

package com.turn.sorcerer.util.email;

import com.turn.sorcerer.injector.SorcererInjector;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.List;

import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.apache.commons.mail.Email;
import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.HtmlEmail;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Class Description Here
 *
 * @author tshiou
 */
public class Emailer {

	private static final Logger LOGGER =
			LogManager.getFormatterLogger(Emailer.class);

	private String title;
	private String body;

	private static final EmailType ADMIN_EMAIL = SorcererInjector.get().getAdminEmail();

	public Emailer(String title, Exception ex) {
		this(title, ExceptionUtils.getStackTrace(ex));
	}

	public Emailer(String title, String body) {
		this.title = title;
		this.body = body;
	}

	public void send() {
		if (ADMIN_EMAIL.isEnabled() == false) {
			LOGGER.info("Service email disabled. Not sending email");
			return;
		}

		try {
			sendEmail();
		} catch (EmailException e) {
			LOGGER.error("Could not send email", e);
		} catch (UnknownHostException e) {
			LOGGER.error("Could not get local hostname for email");
		}
	}

	private void sendEmail() throws EmailException, UnknownHostException {

		List<String> addresses =
				Lists.newArrayList(Splitter.on(',')
						.omitEmptyStrings()
						.trimResults()
						.split(ADMIN_EMAIL.getAdmins()));
		LOGGER.info("Sending email to %s", addresses.toString());


		Email email = new HtmlEmail();
		email.setHostName(ADMIN_EMAIL.getHost());
		email.setSocketTimeout(30000); // 30 seconds
		email.setSocketConnectionTimeout(30000); // 30 seconds
		for (String address : addresses) {
			email.addTo(address);
		}
		email.setFrom(SorcererInjector.get().getModule().getName() + "@" +
				InetAddress.getLocalHost().getHostName());
		email.setSubject(title);
		email.setMsg(body);
		email.send();

	}
}
