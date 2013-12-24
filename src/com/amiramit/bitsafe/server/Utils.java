package com.amiramit.bitsafe.server;

import java.util.UUID;

import javax.servlet.http.HttpSession;

public final class Utils {
	private Utils() {
	};

	public static String getRandomString() {
		return UUID.randomUUID().toString();
	}

	public static Object getAndRemoveAttribute(final HttpSession session,
			final String attribute) {
		final Object res = session.getAttribute(attribute);
		session.removeAttribute(attribute);
		return res;
	}
}
