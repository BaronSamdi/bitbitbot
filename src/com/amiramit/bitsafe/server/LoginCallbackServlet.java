package com.amiramit.bitsafe.server;

import static com.google.common.base.Preconditions.checkNotNull;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.scribe.builder.ServiceBuilder;
import org.scribe.builder.api.FacebookApi;
import org.scribe.builder.api.GoogleApi;
import org.scribe.builder.api.TwitterApi;
import org.scribe.model.OAuthRequest;
import org.scribe.model.Response;
import org.scribe.model.Token;
import org.scribe.model.Verb;
import org.scribe.model.Verifier;
import org.scribe.oauth.OAuthService;

import com.amiramit.bitsafe.client.uitypes.UIVerifyException;
import com.amiramit.bitsafe.shared.FieldVerifier;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import com.googlecode.objectify.NotFoundException;

@SuppressWarnings("serial")
public class LoginCallbackServlet extends HttpServlet {
	private static final Logger LOG = Logger
			.getLogger(LoginCallbackServlet.class.getName());

	public enum ProviderName {
		FACEBOOK, TWITTER, GOOGLE
	};

	private static final String TWITTER_APP_ID = null;
	private static final String TWITTER_APP_SECRET = null;
	private static final String TWITTER_PROTECTED_RESOURCE_URL = "https://api.twitter.com/1.1/account/verify_credentials.json";

	private static final String FACEBOOK_APP_ID = null;
	private static final String FACEBOOK_APP_SECRET = null;
	private static final String FACEBOOK_PROTECTED_RESOURCE_URL = "https://graph.facebook.com/me";

	private OAuthService getOAuthService(ProviderName authProvider,
			String callbackUrl) {
		OAuthService service = null;

		assert authProvider != null && callbackUrl != null;

		switch (authProvider) {
		case TWITTER:
			service = new ServiceBuilder().provider(TwitterApi.class)
					.apiKey(TWITTER_APP_ID).apiSecret(TWITTER_APP_SECRET)
					.callback(callbackUrl).build();
			break;
		case FACEBOOK:
			service = new ServiceBuilder().provider(FacebookApi.class)
					.apiKey(FACEBOOK_APP_ID).apiSecret(FACEBOOK_APP_SECRET)
					.callback(callbackUrl).build();
			break;
		}

		checkNotNull(service);

		return service;
	}

	private String getProtectedResourceUrl(ProviderName authProvider)
			throws UIVerifyException {
		assert authProvider != null;

		switch (authProvider) {
		case TWITTER:
			return TWITTER_PROTECTED_RESOURCE_URL;
		case FACEBOOK:
			return FACEBOOK_PROTECTED_RESOURCE_URL;
		}

		throw new UIVerifyException("Invalid authProvider!");
	}

	@Override
	public void doGet(final HttpServletRequest request,
			final HttpServletResponse response) throws IOException {
		try {
			if (request.getRequestURI().equals("/login")) {
				final HttpSession session = request.getSession(true);
				handleLoginRequest(request, response, session);
				return;
			} else if (request.getRequestURI().equals("/login/callback")) {
				handleLoginCallbackRequest(request, response);
				return;
			}
		} catch (Exception e) {
			LOG.log(Level.SEVERE, "Exception handling doGet", e);
		}

		response.sendError(HttpServletResponse.SC_NOT_FOUND);
	}

	private void handleLoginCallbackRequest(final HttpServletRequest request,
			final HttpServletResponse response) throws UIVerifyException,
			IOException {
		final HttpSession session = request.getSession(false);
		final ProviderName provider = (ProviderName) getAndRemoveAttribute(
				session, "LOGIN_PROVIDER");
		String afterLoginUrl = (String) getAndRemoveAttribute(session,
				"AFTER_LOGIN_REDIRECT");
		FieldVerifier.verifyUri(afterLoginUrl);

		if (provider.equals(ProviderName.GOOGLE)) {
			final UserService userService = UserServiceFactory.getUserService();
			final User user = userService.getCurrentUser();
			if (user == null) {
				LOG.severe("Got google provider - User should not be null here!");
				return;
			} else {// already logged in ...
				LOG.info("Got login callback request from looged in google user.");
				doLogin(response, session, new SocialUser(user), afterLoginUrl);
			}
			return;
		}

		// Facebook (and some others) has optional state variable to protect
		// against CSFR. We'll use it
		// if (provider.equals("facebook")) {
		String reqState = request.getParameter("state");
		String sessionState = (String) session.getAttribute("LOGIN_STATE");
		if (!reqState.equals(session)) {
			LOG.severe("State mismatch in session, expected: " + sessionState
					+ " Passed: " + reqState);
			response.sendError(HttpServletResponse.SC_NOT_FOUND);
			return;
		}
		// }

		// if there is any request token in session, get it; can be null
		Token requestToken = (Token) getAndRemoveAttribute(session,
				"LOGIN_TOKEN");

		OAuthService service = getOAuthService(provider, request
				.getRequestURL().toString());
		String oauth_verifier = request.getParameter("oauth_verifier");
		FieldVerifier.verifyString(oauth_verifier);
		Verifier verifier = new Verifier(oauth_verifier);
		Token accessToken = service.getAccessToken(requestToken, verifier);

		// Should be verified now; try to get a protected resource ...
		OAuthRequest oAuthReq = new OAuthRequest(Verb.GET,
				getProtectedResourceUrl(provider));
		service.signRequest(accessToken, oAuthReq);
		Response oAuthRes = oAuthReq.send();
		String json = oAuthRes.getBody();
		SocialUser socialUser = new SocialUser(provider, json);
		doLogin(response, session, socialUser, afterLoginUrl);
	}

	private void doLogin(HttpServletResponse response, HttpSession session,
			SocialUser socialUser, String redirectUrl) throws IOException {
		BLUser blUser = socialUser.toBLUser();
		blUser.onLogin();
		blUser.save();
		session.setAttribute("userID", checkNotNull(blUser.getUserID()));
		response.sendRedirect(redirectUrl);
	}

	private Object getAndRemoveAttribute(final HttpSession session,
			String attribute) {
		Object res = session.getAttribute(attribute);
		session.removeAttribute(attribute);
		return res;
	}

	private void handleLoginRequest(final HttpServletRequest request,
			final HttpServletResponse response, final HttpSession session)
			throws UIVerifyException, IOException {
		String afterLoginUrl = request.getParameter("u");
		FieldVerifier.verifyUri(afterLoginUrl);

		String providerStr = request.getParameter("p");
		ProviderName provider = FieldVerifier.verifyProvider(providerStr);

		String callbackUrl = request.getRequestURL().toString() + "/callback";

		// if provider is google it's the simplest case - just use google user
		// services ...
		if (provider.equals(ProviderName.GOOGLE)) {
			final UserService userService = UserServiceFactory.getUserService();
			final User user = userService.getCurrentUser();
			if (user == null) {
				String loginURL = userService.createLoginURL(callbackUrl);
				LOG.info("Got login request with google provider. redirecting to "
						+ loginURL);

				session.setAttribute("LOGIN_PROVIDER", provider);
				session.setAttribute("AFTER_LOGIN_REDIRECT", afterLoginUrl);
				response.sendRedirect(loginURL);
			} else {// already logged in ...
				LOG.info("Got login request from already looged in google user.");
				doLogin(response, session, new SocialUser(user), afterLoginUrl);
			}
			return;
		}

		OAuthService service = getOAuthService(provider, callbackUrl);

		Token requestToken = null;
		// Twitter (and some others) requires request token first. obtain it ...
		if (provider.equals(ProviderName.TWITTER)) {
			requestToken = service.getRequestToken();
			session.setAttribute("LOGIN_TOKEN", requestToken);
		}

		String authorizationUrl = service.getAuthorizationUrl(requestToken);

		// Facebook (and some others) has optional state variable to protect
		// against CSFR. We'll use it
		// if (provider.equals("facebook")) {
		String state = Utils.getRandomString();
		authorizationUrl += "&state=" + state;
		session.setAttribute("LOGIN_STATE", state);
		// }

		LOG.info("Got the Request Token: " + requestToken.getToken()
				+ " provide = " + provider + " afterLoginUrl = "
				+ afterLoginUrl + " callbackUrl = " + callbackUrl
				+ " authorizationUrl = " + authorizationUrl);

		session.setAttribute("LOGIN_PROVIDER", provider);
		session.setAttribute("AFTER_LOGIN_REDIRECT", afterLoginUrl);
		response.sendRedirect(authorizationUrl);
	}
}
