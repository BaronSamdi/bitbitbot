package com.amiramit.bitsafe.shared;

import com.amiramit.bitsafe.client.dto.UIVerifyException;
import com.google.gwt.safehtml.shared.UriUtils;

/**
 * <p>
 * FieldVerifier validates that the name the user enters is valid.
 * </p>
 * <p>
 * This class is in the <code>shared</code> package because we use it in both
 * the client code and on the server. On the client, we verify that the name is
 * valid before sending an RPC request so the user doesn't have to wait for a
 * network round trip to get feedback. On the server, we verify that the name is
 * correct to ensure that the input is correct regardless of where the RPC
 * originates.
 * </p>
 * <p>
 * When creating a class that is used on both the client and the server, be sure
 * that all code is translatable and does not use native JavaScript. Code that
 * is not translatable (such as code that interacts with a database or the file
 * system) cannot be compiled into client side JavaScript. Code that uses native
 * JavaScript (such as Widgets) cannot be run on the server.
 * </p>
 */
public final class FieldVerifier {

	private FieldVerifier() {
	};

	public static void verifyNotNull(final Object obj) throws UIVerifyException {
		if (obj == null) {
			throw new UIVerifyException("Got null");
		}
	}

	public static void verifyIsNull(final Object obj) throws UIVerifyException {
		if (obj != null) {
			throw new UIVerifyException("Got non-null");
		}
	}

	public static void verifyValidSymbol(final String symbol)
			throws UIVerifyException {
		verifyNotNull(symbol);

		if (!symbol.equals("BTCUSD")) {
			throw new UIVerifyException("Invalid symbol: "
					+ truncateStr(symbol, 10));
		}
	}

	public static void verifyAlphanumericPlus(final String str)
			throws UIVerifyException {
		verifyNotNull(str);
		final int sz = str.length();
		for (int i = 0; i < sz; i++) {
			final char charAt = str.charAt(i);
			if (charAt != ' ' && !Character.isLetterOrDigit(charAt) && charAt != '@' && charAt != '.') {
				throw new UIVerifyException("String: '" + truncateStr(str, 30)
						+ "' is not alpha numeric; char: " + charAt);
			}
		}
	}

	public static void verifyString(final String str) throws UIVerifyException {
		verifyAlphanumericPlus(str);
		verifyNotEmpty(str);
	}

	private static void verifyNotEmpty(final String str)
			throws UIVerifyException {
		if (str.isEmpty()) {
			throw new UIVerifyException("Empty String");
		}
	}

	private static String truncateStr(final String str, final int len) {
		return str.substring(0, Math.min(str.length(), len));
	}

	public static void verifyUri(final String requestUri)
			throws UIVerifyException {
		verifyNotNull(requestUri);
		if (requestUri.length() > 50 || requestUri.isEmpty()) {
			throw new UIVerifyException("Unsafe URI (length > 50 || isEmpty): "
					+ truncateStr(requestUri, 50));
		}

		if (!UriUtils.isSafeUri(requestUri)) {
			// This message is logged in UIVerifyException
			throw new UIVerifyException("Unsafe URI: "
					+ truncateStr(requestUri, 50));
		}
	}

	public static void verifyEmail(final String email) throws UIVerifyException {
		verifyNotNull(email);
		verifyNotEmpty(email);

		final String emailPattern = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.(?:[a-zA-Z]{2,6})$";

		if (!email.matches(emailPattern)) {
			throw new UIVerifyException("Invalid email: "
					+ truncateStr(email, 50));
		}
	}
}
