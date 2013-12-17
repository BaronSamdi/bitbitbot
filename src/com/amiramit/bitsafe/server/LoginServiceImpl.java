package com.amiramit.bitsafe.server;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Random;
import java.util.logging.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import com.amiramit.bitsafe.client.UITypes.UILoginInfo;
import com.amiramit.bitsafe.client.UITypes.UIVerifyException;
import com.amiramit.bitsafe.client.service.LoginService;
import com.amiramit.bitsafe.shared.FieldVerifier;
import com.google.appengine.api.channel.ChannelServiceFactory;
import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.googlecode.objectify.NotFoundException;

@SuppressWarnings("serial")
public class LoginServiceImpl extends RemoteServiceServlet implements
		LoginService {

	private static final Random RANDOM = new Random();
	private static final Logger LOG = Logger.getLogger(LoginServiceImpl.class
			.getName());

	@Override
	public UILoginInfo login(final String requestUri) throws UIVerifyException {
		// Validate request URI
		FieldVerifier.verifyUri(requestUri);

		final UserService userService = UserServiceFactory.getUserService();
		final User user = userService.getCurrentUser();
		final UILoginInfo loginInfo = new UILoginInfo();

		LOG.info("User login requested with uri: " + requestUri);
		if (user == null) {
			LOG.info("User login requested, for user not logged in. Returning login URL");
			loginInfo.setLoggedIn(false);
			loginInfo.setLoginUrl(userService.createLoginURL(requestUri));
			return loginInfo;
		}

		LOG.info("User login requested, for logged in user. User details: "
				+ user);

		// Store logged in state in the session
		HttpServletRequest req = getThreadLocalRequest();
		HttpSession session = req.getSession(true);

		String userId = user.getUserId();
		session.setAttribute("userID", checkNotNull(userId));

		// If user does not exists, create it
		BLUser blUser;
		try {
			blUser = BLUser.getUser(userId);
		} catch (NotFoundException e) {
			blUser = new BLUser(userId);
		}
		blUser.onLogin();
		String channelToken = blUser.establishChannel(session.getId());		
		blUser.save();

		loginInfo.setChannelToken(channelToken);
		loginInfo.setLoggedIn(true);
		loginInfo.setEmailAddress(user.getEmail());
		loginInfo.setNickname(user.getNickname());
		loginInfo.setLogoutUrl(userService.createLogoutURL(requestUri));

		return loginInfo;
	}

}