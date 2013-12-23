package com.amiramit.bitsafe.server.login;

import java.io.IOException;
import java.util.logging.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.scribe.builder.ServiceBuilder;
import org.scribe.builder.api.FacebookApi;

import com.amiramit.bitsafe.client.NotLoggedInException;
import com.amiramit.bitsafe.client.uitypes.UIVerifyException;
import com.amiramit.bitsafe.server.BLUser;
import com.amiramit.bitsafe.server.SocialUser;

public abstract class LoginProvider {
	private static final Logger LOG = Logger.getLogger(LoginProvider.class
			.getName());
	
	public abstract void doLoginCallback(HttpServletRequest request,
			HttpServletResponse response, HttpSession session,
			String afterLoginUrl) throws IOException, UIVerifyException;

	protected void doLogin(final HttpServletResponse response,
			final HttpSession session, final SocialUser socialUser,
			final String redirectUrl) throws IOException {
		BLUser blUser = socialUser.toBLUser();
		doLogin(response, session, blUser, redirectUrl);
	}

	private static void doLogin(final HttpServletResponse response,
			final HttpSession session, final BLUser blUser,
			final String redirectUrl) throws IOException {
		blUser.onLogin(session);
		blUser.save();
		response.sendRedirect(redirectUrl);
	}

	public static boolean doAlreadyLoggedIn(final HttpServletResponse response,
			final HttpSession session, final String afterLoginUrl)
			throws IOException {
		try {
			BLUser user = BLUser.checkLoggedIn(session);
			LOG.info("Got login request from user: " + user
					+ " with logged in session");
			LoginProvider.doLogin(response, session, user, afterLoginUrl);
			return true;
		} catch (NotLoggedInException e) {
			// Expected ...
			LOG.info("Got login request from unknown user");
		}
		
		return false;
	}

	public abstract void doLoginFirstStage(HttpServletResponse response,
			HttpSession session, String afterLoginUrl, String callbackUrl)
			throws IOException, UIVerifyException;

	static final LoginProvider facebookProvider = new FacebookLogin(
			"https://graph.facebook.com/me",
			"https://graph.facebook.com/me/permissions", new ServiceBuilder()
					.provider(FacebookApi.class).apiKey("266929410125455")
					.apiSecret("b4c0f9a0cecd2e2986d9b9b2dbf87242"));
	static final LoginProvider googleProvider = new GoogleLogin();

	public static LoginProvider get(LoginProviderName provider)
			throws UIVerifyException {

		switch (provider) {
		case FACEBOOK:
			return facebookProvider;
		case GOOGLE:
			return googleProvider;

		default:
			throw new UIVerifyException("Invalid provider: " + provider);
		}
	}
}