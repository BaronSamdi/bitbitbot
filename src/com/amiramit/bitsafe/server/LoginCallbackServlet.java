package com.amiramit.bitsafe.server;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.scribe.builder.ServiceBuilder;
import org.scribe.builder.api.FacebookApi;
import org.scribe.builder.api.TwitterApi;
import org.scribe.model.OAuthRequest;
import org.scribe.model.Response;
import org.scribe.model.Token;
import org.scribe.model.Verb;
import org.scribe.model.Verifier;
import org.scribe.oauth.OAuthService;

import com.amiramit.bitsafe.client.NotLoggedInException;
import com.amiramit.bitsafe.client.uitypes.UIVerifyException;
import com.amiramit.bitsafe.shared.FieldVerifier;
import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;

@SuppressWarnings("serial")
public class LoginCallbackServlet extends HttpServlet {
	private static final Logger LOG = Logger
			.getLogger(LoginCallbackServlet.class.getName());

	public enum ProviderName {
		FACEBOOK, TWITTER, GOOGLE
	};

	private static final String TWITTER_APP_ID = null;
	private static final String TWITTER_APP_SECRET = null;
	private static final String TWITTER_PROTECTED_RESOURCE_URL = 
			"https://api.twitter.com/1.1/account/verify_credentials.json";

	private static final String FACEBOOK_APP_ID = "266929410125455";
	private static final String FACEBOOK_APP_SECRET = "b4c0f9a0cecd2e2986d9b9b2dbf87242";
	private static final String FACEBOOK_PROTECTED_RESOURCE_URL = "https://graph.facebook.com/me?fields=id,name,email";

	private OAuthService getOAuthService(final ProviderName authProvider,
			final String callbackUrl) throws UIVerifyException {
		assert authProvider != null && callbackUrl != null;

		switch (authProvider) {
		case TWITTER:
			return new ServiceBuilder().provider(TwitterApi.class)
					.apiKey(TWITTER_APP_ID).apiSecret(TWITTER_APP_SECRET)
					.callback(callbackUrl).build();
		case FACEBOOK:
			return new ServiceBuilder().provider(FacebookApi.class)
					.apiKey(FACEBOOK_APP_ID).apiSecret(FACEBOOK_APP_SECRET)
					.callback(callbackUrl).build();
		default:
			throw new UIVerifyException(
					"Unhandled authProvider in getOAuthService");
		}
	}

	private String getProtectedResourceUrl(final ProviderName authProvider)
			throws UIVerifyException {
		assert authProvider != null;

		switch (authProvider) {
		case TWITTER:
			return TWITTER_PROTECTED_RESOURCE_URL;
		case FACEBOOK:
			return FACEBOOK_PROTECTED_RESOURCE_URL;
		default:
			throw new UIVerifyException(
					"Unhandled authProvider in getProtectedResourceUrl");
		}
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
		} catch (final Exception e) {
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
		final String afterLoginUrl = (String) getAndRemoveAttribute(session,
				"AFTER_LOGIN_REDIRECT");
		FieldVerifier.verifyUri(afterLoginUrl);

		if (provider.equals(ProviderName.GOOGLE)) {
			final UserService userService = UserServiceFactory.getUserService();
			final User user = userService.getCurrentUser();
			if (user == null) {
				LOG.severe("Got google provider - User should not be null here!");
				return;
			} else { // already logged in ...
				LOG.info("Got login callback request from looged in google user:"
						+ user);
				doLogin(response, session, new SocialUser(user), afterLoginUrl);
			}
			return;
		}

		// Facebook (and some others) has optional state variable to protect
		// against CSFR. We'll use it
		// if (provider.equals("facebook")) {
		final String reqState = request.getParameter("state");
		final String sessionState = (String) session
				.getAttribute("LOGIN_STATE");
		if (!reqState.equals(sessionState)) {
			LOG.severe("State mismatch in session, expected: " + sessionState
					+ " Passed: " + reqState);
			response.sendError(HttpServletResponse.SC_NOT_FOUND);
			return;
		}
		// }

		// if there is any request token in session, get it; can be null
		final Token requestToken = (Token) getAndRemoveAttribute(session,
				"LOGIN_TOKEN");

		final OAuthService service = getOAuthService(provider, request
				.getRequestURL().toString());
		final String oauthVerifier = request.getParameter("code");
		final Verifier verifier = new Verifier(oauthVerifier);
		final Token accessToken = service
				.getAccessToken(requestToken, verifier);

		// Should be verified now; try to get a protected resource ...
		final OAuthRequest oAuthReq = new OAuthRequest(Verb.GET,
				getProtectedResourceUrl(provider));
		service.signRequest(accessToken, oAuthReq);
		final Response oAuthRes = oAuthReq.send();
		final String json = oAuthRes.getBody();
		final SocialUser socialUser = new SocialUser(provider, json);
		doLogin(response, session, socialUser, afterLoginUrl);
	}

	private void doLogin(final HttpServletResponse response,
			final HttpSession session, final SocialUser socialUser,
			final String redirectUrl) throws IOException {
		BLUser blUser = socialUser.toBLUser();
		doLogin(response, session, blUser, redirectUrl);
	}

	private void doLogin(final HttpServletResponse response,
			final HttpSession session, final BLUser blUser,
			final String redirectUrl) throws IOException {
		blUser.onLogin(session);
		blUser.save();
		response.sendRedirect(redirectUrl);
	}

	private Object getAndRemoveAttribute(final HttpSession session,
			final String attribute) {
		final Object res = session.getAttribute(attribute);
		session.removeAttribute(attribute);
		return res;
	}

	private void handleLoginRequest(final HttpServletRequest request,
			final HttpServletResponse response, final HttpSession session)
			throws UIVerifyException, IOException {
		final String afterLoginUrl = request.getParameter("u");
		FieldVerifier.verifyUri(afterLoginUrl);

		try {
			BLUser user = BLUser.checkLoggedIn(session);
			LOG.info("Got login request from user: " + user
					+ " with logged in session");
			doLogin(response, session, user, afterLoginUrl);
			return;
		} catch (NotLoggedInException e) {
			// Expected ...
			LOG.info("Got login request from unknown user");
		}

		final String providerStr = request.getParameter("p");
		final ProviderName provider = FieldVerifier.verifyProvider(providerStr);

		final String callbackUrl = request.getRequestURL().toString()
				+ "/callback";

		// if provider is google it's the simplest case - just use google user
		// services ...
		if (provider.equals(ProviderName.GOOGLE)) {
			final UserService userService = UserServiceFactory.getUserService();
			final User user = userService.getCurrentUser();
			if (user == null) {
				final String loginURL = userService.createLoginURL(callbackUrl);
				LOG.info("Got login request with google provider. redirecting to "
						+ loginURL);

				session.setAttribute("LOGIN_PROVIDER", provider);
				session.setAttribute("AFTER_LOGIN_REDIRECT", afterLoginUrl);
				response.sendRedirect(loginURL);
			} else { // already logged in ...
				LOG.info("Got login request from already looged in google user: "
						+ user);
				doLogin(response, session, new SocialUser(user), afterLoginUrl);
			}
			return;
		}

		final OAuthService service = getOAuthService(provider, callbackUrl);

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
		final String state = Utils.getRandomString();
		authorizationUrl += "&state=" + state;
		session.setAttribute("LOGIN_STATE", state);
		// }

		LOG.info("Got the Request Token: " + requestToken + " provide = "
				+ provider + " afterLoginUrl = " + afterLoginUrl
				+ " callbackUrl = " + callbackUrl + " authorizationUrl = "
				+ authorizationUrl);

		session.setAttribute("LOGIN_PROVIDER", provider);
		session.setAttribute("AFTER_LOGIN_REDIRECT", afterLoginUrl);
		response.sendRedirect(authorizationUrl);
	}
}
