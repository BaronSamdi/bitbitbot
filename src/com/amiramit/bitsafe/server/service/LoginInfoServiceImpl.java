package com.amiramit.bitsafe.server.service;

import java.util.logging.Logger;

import javax.servlet.http.HttpSession;

import com.amiramit.bitsafe.client.NotLoggedInException;
import com.amiramit.bitsafe.client.dto.UIVerifyException;
import com.amiramit.bitsafe.client.service.LoginInfoService;
import com.amiramit.bitsafe.client.service.UILoginInfo;
import com.amiramit.bitsafe.server.BLUser;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import com.google.gwt.user.server.rpc.XsrfProtectedServiceServlet;

@SuppressWarnings("serial")
public class LoginInfoServiceImpl extends XsrfProtectedServiceServlet implements
		LoginInfoService {

	private static final Logger LOG = Logger
			.getLogger(LoginInfoServiceImpl.class.getName());

	@Override
	public UILoginInfo getLoginInfo() throws UIVerifyException,
			NotLoggedInException {
		final HttpSession session = getThreadLocalRequest().getSession();
		final BLUser blUser = BLUser.getUserFromSession(session);
		final UILoginInfo loginInfo = new UILoginInfo();

		LOG.info("User login requested, for logged in user. User details: "
				+ blUser);

		final String channelToken = PushServiceImpl.getChannelToken(blUser,
				session.getId());
		blUser.save();

		loginInfo.setChannelToken(channelToken);
		loginInfo.setEmailAddress(blUser.getEmail());
		loginInfo.setNickname(blUser.getNickname());

		return loginInfo;
	}

	@Override
	public String logout() throws NotLoggedInException {
		final HttpSession session = getThreadLocalRequest().getSession();
		final BLUser blUser = BLUser.getUserFromSession(session);
		blUser.onLogout(session);

		final String afterLogoutUrl = "/";

		// handle special case of google: user actually have to press a link to
		// log out ...
		final UserService userService = UserServiceFactory.getUserService();
		if (userService.isUserLoggedIn()) {
			LOG.info("User: "
					+ blUser
					+ " logged out and has google login. redirect to google logout");
			return userService.createLogoutURL(afterLogoutUrl);
		}

		LOG.info("User: " + blUser + " logged out. redirect to logout link");
		return afterLogoutUrl;
	}

}