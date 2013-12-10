package com.amiramit.bitsafe.server;

import java.util.logging.Logger;

import com.amiramit.bitsafe.client.LoginInfo;
import com.amiramit.bitsafe.client.LoginService;
import com.amiramit.bitsafe.client.UIVerifyException;
import com.amiramit.bitsafe.shared.FieldVerifier;
import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import com.google.gwt.user.server.rpc.XsrfProtectedServiceServlet;

@SuppressWarnings("serial")
public class LoginServiceImpl extends XsrfProtectedServiceServlet  implements
		LoginService {

	private static final Logger LOG = Logger.getLogger(LoginServiceImpl.class
			.getName());

	@Override
    public LoginInfo login(final String requestUri) throws UIVerifyException {
		// Validate request URI, and also limit its length to 20
		FieldVerifier.verifyUri(requestUri);
		
		final UserService userService = UserServiceFactory.getUserService();
		final User user = userService.getCurrentUser();
		final LoginInfo loginInfo = new LoginInfo();

		LOG.info("User login requested with uri: " + requestUri);
		if (user != null) {
			LOG.info("User login requested, for logged in user. User details: "
					+ user);
			loginInfo.setLoggedIn(true);
			loginInfo.setEmailAddress(user.getEmail());
			loginInfo.setNickname(user.getNickname());
			loginInfo.setLogoutUrl(userService.createLogoutURL(requestUri));
		} else {
			LOG.info("User login requested, for user not logged in. Returning login URL");
			loginInfo.setLoggedIn(false);
			loginInfo.setLoginUrl(userService.createLoginURL(requestUri));
		}
		return loginInfo;
	}

}