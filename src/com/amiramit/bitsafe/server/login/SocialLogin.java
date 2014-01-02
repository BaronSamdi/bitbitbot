package com.amiramit.bitsafe.server.login;

import java.io.IOException;
import java.util.logging.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.scribe.builder.ServiceBuilder;
import org.scribe.model.OAuthRequest;
import org.scribe.model.Response;
import org.scribe.model.Token;
import org.scribe.model.Verb;
import org.scribe.model.Verifier;
import org.scribe.oauth.OAuthService;

import com.amiramit.bitsafe.client.dto.UIVerifyException;
import com.amiramit.bitsafe.server.Utils;

abstract class SocialLogin extends LoginProvider {
	private static final Logger LOG = Logger.getLogger(LoginProvider.class
			.getName());

	protected static final String LOGIN_STATE = "LOGIN_STATE";

	private final LoginProviderName providerName;
	private final String userInfoUrl;
	private final ServiceBuilder builder;

	protected SocialLogin(final LoginProviderName providerName,
			final String userInfoUrl, final String userLogoutUrl,
			final ServiceBuilder builder) {
		this.providerName = providerName;
		this.userInfoUrl = userInfoUrl;
		this.builder = builder;
	}

	private OAuthService getOAuthService(final String callbackUrl)
			throws UIVerifyException {
		assert callbackUrl != null;
		return builder.callback(callbackUrl).build();
	}

	private OAuthService getOAuthService() {
		return builder.build();
	}

	@Override
	public void doLoginFirstStage(final HttpServletRequest request,
			final HttpServletResponse response, final HttpSession session,
			final String afterLoginUrl, final String callbackUrl)
			throws UIVerifyException, IOException {
		final OAuthService service = getOAuthService(callbackUrl);

		final Token requestToken = null;
		// Twitter (and some others) requires request token first. obtain it ...
		// if (providerName.equals(LoginProviderName.TWITTER)) {
		// requestToken = service.getRequestToken();
		// session.setAttribute("LOGIN_TOKEN", requestToken);
		// }

		String authorizationUrl = service.getAuthorizationUrl(requestToken);

		// Facebook (and some others) has optional state variable to protect
		// against CSFR. We'll use it
		final String state = Utils.getRandomString();
		authorizationUrl += "&state=" + state;
		session.setAttribute(LOGIN_STATE, state);

		LOG.info("Got the Request Token: " + requestToken + " provide = "
				+ providerName + " afterLoginUrl = " + afterLoginUrl
				+ " callbackUrl = " + callbackUrl + " authorizationUrl = "
				+ authorizationUrl);

		session.setAttribute(LOGIN_PROVIDER, providerName);
		session.setAttribute(AFTER_LOGIN_REDIRECT, afterLoginUrl);
		response.sendRedirect(authorizationUrl);
	}

	@Override
	public void doLoginCallback(final HttpServletRequest request,
			final HttpServletResponse response, final HttpSession session,
			final String afterLoginUrl) throws IOException, UIVerifyException {

		// Facebook (and some others) has optional state variable to protect
		// against CSFR. We'll use it
		final String reqState = request.getParameter("state");
		final String sessionState = (String) session.getAttribute(LOGIN_STATE);
		if (!reqState.equals(sessionState)) {
			LOG.severe("State mismatch in session, expected: " + sessionState
					+ " Passed: " + reqState);
			response.sendError(HttpServletResponse.SC_NOT_FOUND);
			return;
		}

		// if there is any request token in session, get it; can be null
		final Token requestToken = (Token) Utils.getAndRemoveAttribute(session,
				"LOGIN_TOKEN");

		final OAuthService service = getOAuthService();
		final String oauthVerifier = request.getParameter("code");
		final Verifier verifier = new Verifier(oauthVerifier);
		final Token accessToken = service
				.getAccessToken(requestToken, verifier);

		// Should be verified now; try to get a protected resource ...
		final OAuthRequest oAuthReq = new OAuthRequest(Verb.GET, userInfoUrl);
		service.signRequest(accessToken, oAuthReq);
		final Response oAuthRes = oAuthReq.send();
		final String json = oAuthRes.getBody();
		final SocialUser socialUser = new SocialUser(providerName, json);

		doLogout(service, accessToken);

		LOG.info("Login success for user: " + socialUser + " token: "
				+ accessToken);

		doLogin(response, session, socialUser, afterLoginUrl);
	}

	protected abstract void doLogout(final OAuthService service,
			final Token accessToken);
};
