package com.amiramit.bitsafe.server;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.amiramit.bitsafe.client.uitypes.UIVerifyException;
import com.amiramit.bitsafe.server.login.LoginProvider;
import com.amiramit.bitsafe.server.login.LoginProviderName;
import com.amiramit.bitsafe.shared.FieldVerifier;

@SuppressWarnings("serial")
public class LoginCallbackServlet extends HttpServlet {
	private static final Logger LOG = Logger
			.getLogger(LoginCallbackServlet.class.getName());

	@Override
	public void doGet(final HttpServletRequest request,
			final HttpServletResponse response) throws IOException {
		try {
			if (request.getRequestURI().equals("/login")) {
				final HttpSession session = request.getSession(true);
				handleLoginRequest(request, response, session);
				return;
			} else if (request.getRequestURI().equals("/login/callback")) {
				final HttpSession session = request.getSession(false);
				final LoginProviderName providerName = (LoginProviderName) Utils
						.getAndRemoveAttribute(session, "LOGIN_PROVIDER");
				final String afterLoginUrl = (String) Utils
						.getAndRemoveAttribute(session, "AFTER_LOGIN_REDIRECT");
				FieldVerifier.verifyUri(afterLoginUrl);

				LoginProvider.get(providerName).doLoginCallback(request,
						response, session, afterLoginUrl);
				return;
			}
		} catch (final Exception e) {
			LOG.log(Level.SEVERE, "Exception handling doGet", e);
		}

		response.sendError(HttpServletResponse.SC_NOT_FOUND);
	}

	private void handleLoginRequest(final HttpServletRequest request,
			final HttpServletResponse response, final HttpSession session)
			throws UIVerifyException, IOException {
		final String afterLoginUrl = request.getParameter("u");
		FieldVerifier.verifyUri(afterLoginUrl);

		if (LoginProvider.doAlreadyLoggedIn(response, session, afterLoginUrl)) {
			return;
		}

		final String providerStr = request.getParameter("p");
		FieldVerifier.verifyNotNull(providerStr);
		final LoginProviderName providerName = LoginProviderName
				.valueOf(providerStr);

		final String callbackUrl = request.getRequestURL().toString()
				+ "/callback";

		LoginProvider.get(providerName).doLoginFirstStage(response, session,
				afterLoginUrl, callbackUrl);
	}
}
