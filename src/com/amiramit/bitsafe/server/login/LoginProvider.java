package com.amiramit.bitsafe.server.login;

import java.io.IOException;
import java.util.logging.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.scribe.builder.ServiceBuilder;
import org.scribe.builder.api.FacebookApi;

import com.amiramit.bitsafe.client.NotLoggedInException;
import com.amiramit.bitsafe.client.dto.UIVerifyException;
import com.amiramit.bitsafe.server.BLUser;
import com.googlecode.objectify.NotFoundException;

public abstract class LoginProvider {
	private static final Logger LOG = Logger.getLogger(LoginProvider.class
			.getName());

	protected static final String AFTER_LOGIN_REDIRECT = "AFTER_LOGIN_REDIRECT";
	protected static final String LOGIN_PROVIDER = "LOGIN_PROVIDER";
	public static final String USER_ID = "USER_ID";

	public abstract void doLoginCallback(HttpServletRequest request,
			HttpServletResponse response, HttpSession session,
			String afterLoginUrl) throws IOException, UIVerifyException;

	protected void doLogin(final HttpServletResponse response,
			final HttpSession session, final SocialUser socialUser,
			final String redirectUrl) throws IOException {
		final BLUser blUser = socialUser.toBLUser();
		doLogin(response, session, blUser, redirectUrl);
	}

	protected static void doLogin(final HttpServletResponse response,
			final HttpSession session, final BLUser blUser,
			final String redirectUrl) throws IOException {
		blUser.onLogin();
		if (blUser.getUserId() == null) {
			// We need to save now to generate id in case of new user
			blUser.saveNow();
		} else {
			// We can do the save async
			blUser.save();
		}
		session.setAttribute(USER_ID, blUser.getUserId());
		response.sendRedirect(redirectUrl);
	}

	public static boolean doAlreadyLoggedIn(final HttpServletResponse response,
			final HttpSession session, final String afterLoginUrl)
			throws IOException {
		try {
			BLUser user = isLoggedIn(session);
			LOG.info("Got login request from user: " + user
					+ " with logged in session");
			doLogin(response, session, user, afterLoginUrl);
			return true;
		} catch (NotLoggedInException e) {
			LOG.info("Got login request from unknown user");
			return false;
		}
	}

	public static BLUser isLoggedIn(final HttpSession session)
			throws NotLoggedInException {
		final Long userId = (Long) session.getAttribute("userID");
		if (userId != null) {
			try {
				return BLUser.getUserFromId(userId);
			} catch (NotFoundException e) {
			}
		}
		throw new NotLoggedInException();
	}

	public abstract void doLoginFirstStage(HttpServletRequest request,
			HttpServletResponse response, HttpSession session,
			String afterLoginUrl, String callbackUrl) throws IOException,
			UIVerifyException;

	static final LoginProvider FACEBOOK_PROVIDER = new FacebookLogin(
			"https://graph.facebook.com/me",
			"https://graph.facebook.com/me/permissions", new ServiceBuilder()
					.provider(FacebookApi.class).apiKey("266929410125455")
					.apiSecret("b4c0f9a0cecd2e2986d9b9b2dbf87242"));
	static final LoginProvider GOOGLE_PROVIDER = new GoogleLogin();

	static final LoginProvider INT_PROVIDER = new IntLogin();

	public static LoginProvider get(final LoginProviderName provider)
			throws UIVerifyException {

		switch (provider) {
		case FACEBOOK:
			return FACEBOOK_PROVIDER;
		case GOOGLE:
			return GOOGLE_PROVIDER;
		case INT:
			return INT_PROVIDER;

		default:
			throw new UIVerifyException("Invalid provider: " + provider);
		}
	}
}