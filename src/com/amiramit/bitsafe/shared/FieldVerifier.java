package com.amiramit.bitsafe.shared;

import com.amiramit.bitsafe.client.UITypes.UIVerifyException;
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
public class FieldVerifier {

	private FieldVerifier() {
	};

	public static void verifyNotNull(Object obj) throws UIVerifyException {
		if (obj == null) {
			throw new UIVerifyException("Got null");
		}
	}

	public static void verifyIsNull(Object obj) throws UIVerifyException {
		if (obj != null) {
			throw new UIVerifyException("Got non-null");
		}
	}

	public static void verifyValidSymbol(String symbol)
			throws UIVerifyException {
		verifyNotNull(symbol);

		if (!symbol.equals("BTCUSD")) {
			throw new UIVerifyException("Invalid symbol: "
					+ truncateStr(symbol, 10));
		}
	}

	private static boolean isAlphanumeric(String str) {
		if (str == null) {
			return false;
		}
		int sz = str.length();
		for (int i = 0; i < sz; i++) {
			char charAt = str.charAt(i);
			if (charAt != ' ' && Character.isLetterOrDigit(charAt) == false) {
				return false;
			}
		}
		return true;
	}

	public static void verifyString(String str) throws UIVerifyException {
		verifyNotNull(str);
		if (!isAlphanumeric(str)) {
			throw new UIVerifyException("String: '" + truncateStr(str, 30)
					+ "' is not alpha numeric");
		}
	}

	private static String truncateStr(String str, int len) {
		return str.substring(0, Math.min(str.length(), len));
	}

	public static void verifyUri(String requestUri) throws UIVerifyException {
		verifyNotNull(requestUri);
		if (requestUri.length() > 50) {
			throw new UIVerifyException(
					"Unsafe URI requested at login (length > 50!): "
							+ truncateStr(requestUri, 50));
		}

		if (!UriUtils.isSafeUri(requestUri)) {
			// This message is logged in UIVerifyException
			throw new UIVerifyException("Unsafe URI requested at login: "
					+ truncateStr(requestUri, 50));
		}
	}

}
