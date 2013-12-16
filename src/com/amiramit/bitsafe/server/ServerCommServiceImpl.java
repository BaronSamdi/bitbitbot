package com.amiramit.bitsafe.server;

import java.util.logging.Logger;

import com.amiramit.bitsafe.client.NotLoggedInException;
import com.amiramit.bitsafe.client.UITypes.UITicker;
import com.amiramit.bitsafe.client.UITypes.UIVerifyException;
import com.amiramit.bitsafe.client.service.ServerCommService;
import com.amiramit.bitsafe.shared.ExchangeName;
import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import com.google.gwt.user.server.rpc.XsrfProtectedServiceServlet;

/**
 * The server side implementation of the RPC service.
 */
@SuppressWarnings("serial")
public class ServerCommServiceImpl extends XsrfProtectedServiceServlet
		implements ServerCommService {
	private static final Logger LOG = Logger
			.getLogger(ServerCommServiceImpl.class.getName());

	@Override
	public UITicker getTicker(ExchangeName atExchange)
			throws UIVerifyException, NotLoggedInException {
		checkLoggedIn();
		// No need to verify atExchange because it is enum

		LOG.info("getTicker with symbol: " + atExchange);

		BLLastTicker blLastTicker = BLLastTicker.getLastTicker(atExchange);
		UITicker lastTicker = blLastTicker == null ? null : blLastTicker.toUITicker();

		return lastTicker;
	}

	public static User checkLoggedIn() throws NotLoggedInException {
		User ret = getUser();
		if (ret == null) {
			throw new NotLoggedInException("Not logged in.");
		}
		LOG.info("checkLoggedIn for user: " + ret);

		return ret;
	}

	private static User getUser() {
		UserService userService = UserServiceFactory.getUserService();
		return userService.getCurrentUser();
	}
}
