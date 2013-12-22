package com.amiramit.bitsafe.server.service;

import java.util.logging.Logger;

import javax.servlet.http.HttpSession;

import com.amiramit.bitsafe.client.NotLoggedInException;
import com.amiramit.bitsafe.client.service.LoginInfoService;
import com.amiramit.bitsafe.client.uitypes.UILoginInfo;
import com.amiramit.bitsafe.client.uitypes.UIVerifyException;
import com.amiramit.bitsafe.server.BLUser;
import com.google.gwt.user.server.rpc.XsrfProtectedServiceServlet;

@SuppressWarnings("serial")
public class LoginInfoServiceImpl extends XsrfProtectedServiceServlet implements
		LoginInfoService {

	private static final Logger LOG = Logger
			.getLogger(LoginInfoServiceImpl.class.getName());

	@Override
	public UILoginInfo getLoginInfo() throws UIVerifyException,
			NotLoggedInException {
		HttpSession session = getThreadLocalRequest().getSession();
		final BLUser blUser = BLUser.checkLoggedIn(session);
		final UILoginInfo loginInfo = new UILoginInfo();

		LOG.info("User login requested, for logged in user. User details: "
				+ blUser);

		final String channelToken = PushServiceImpl.getChannelToken(blUser,
				session.getId());
		blUser.save();

		loginInfo.setChannelToken(channelToken);
		loginInfo.setEmailAddress(blUser.getEmail());
		loginInfo.setNickname(blUser.getNickname());

		// TODO: set logout URL!
		loginInfo.setLogoutUrl("");

		return loginInfo;
	}

	@Override
	public String logout() throws NotLoggedInException {
		HttpSession session = getThreadLocalRequest().getSession();
		final BLUser blUser = BLUser.checkLoggedIn(session);
		blUser.onLogout(session);
		return "/";
	}

}