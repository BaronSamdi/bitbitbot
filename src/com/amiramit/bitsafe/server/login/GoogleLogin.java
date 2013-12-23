package com.amiramit.bitsafe.server.login;

import java.io.IOException;
import java.util.logging.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.amiramit.bitsafe.client.uitypes.UIVerifyException;
import com.amiramit.bitsafe.server.SocialUser;
import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;

public class GoogleLogin extends LoginProvider {
	private static final String AFTER_LOGIN_REDIRECT = "AFTER_LOGIN_REDIRECT";
	private static final String LOGIN_PROVIDER = "LOGIN_PROVIDER";
	private static final Logger LOG = Logger.getLogger(GoogleLogin.class
			.getName());

	@Override
	public void doLoginCallback(HttpServletRequest request,
			HttpServletResponse response, HttpSession session,
			String afterLoginUrl) throws IOException, UIVerifyException {
		final UserService userService = UserServiceFactory.getUserService();
		final User user = userService.getCurrentUser();
		if (user == null) {
			LOG.severe("Got google provider - User should not be null here!");
			response.sendError(HttpServletResponse.SC_NOT_FOUND);
		} else { // already logged in ...
			LOG.info("Got login callback request from looged in google user:"
					+ user);
			doLogin(response, session, new SocialUser(user), afterLoginUrl);
		}
	}

	public void doLoginFirstStage(HttpServletResponse response,
			HttpSession session, String afterLoginUrl, String callbackUrl)
			throws IOException, UIVerifyException {
		// if provider is google it's the simplest case - just use google
		// user services ...
		final UserService userService = UserServiceFactory.getUserService();
		final User user = userService.getCurrentUser();
		if (user == null) {
			final String loginURL = userService.createLoginURL(callbackUrl);
			LOG.info("Got login request with google provider. redirecting to "
					+ loginURL);

			session.setAttribute(LOGIN_PROVIDER, LoginProviderName.GOOGLE);
			session.setAttribute(AFTER_LOGIN_REDIRECT, afterLoginUrl);
			response.sendRedirect(loginURL);
		} else { // already logged in ...
			LOG.info("Got login request from already looged in google user: "
					+ user);
			doLogin(response, session, new SocialUser(user), afterLoginUrl);
		}
	}
}