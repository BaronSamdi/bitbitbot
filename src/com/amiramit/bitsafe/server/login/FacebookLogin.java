package com.amiramit.bitsafe.server.login;

import org.scribe.builder.ServiceBuilder;
import org.scribe.model.Token;
import org.scribe.oauth.OAuthService;

public class FacebookLogin extends SocialLogin {

	protected FacebookLogin(final String userInfoUrl,
			final String userLogoutUrl, final ServiceBuilder builder) {
		super(LoginProviderName.FACEBOOK, userInfoUrl, userLogoutUrl, builder);
	}

	@Override
	protected void doLogout(final OAuthService service, final Token accessToken) {
		// No use for logout currently, because it will either:
		// (a) logout from the user facebook account, but when he logs back, he
		// will also silently log back to us as well OR
		// (b) the next line deauthrize our application from facebook; but it is
		// of no use because to re-authrise the user does not need to enter his
		// facebook username/password again, in case he is already logged in to
		// facebook

		// OAuthRequest oAuthReq = new OAuthRequest(Verb.DELETE, userLogoutUrl);
		// service.signRequest(accessToken, oAuthReq);
		// oAuthReq.send();

	}
}