package com.amiramit.bitsafe.server.login;

import java.util.logging.Logger;

import org.scribe.builder.ServiceBuilder;
import org.scribe.model.OAuthRequest;
import org.scribe.model.Token;
import org.scribe.model.Verb;
import org.scribe.oauth.OAuthService;

public class FacebookLogin extends SocialLogin {
	private static final Logger LOG = Logger.getLogger(FacebookLogin.class
			.getName());

	protected FacebookLogin(String userInfoUrl, String userLogoutUrl,
			ServiceBuilder builder) {
		super(LoginProviderName.FACEBOOK, userInfoUrl, userLogoutUrl, builder);
	}

	protected void doLogout(final OAuthService service, final Token accessToken) {
		OAuthRequest oAuthReq = new OAuthRequest(Verb.DELETE, userLogoutUrl);
		service.signRequest(accessToken, oAuthReq);
		oAuthReq.send();
	}
}