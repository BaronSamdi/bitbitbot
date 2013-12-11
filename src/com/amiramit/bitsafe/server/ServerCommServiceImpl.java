package com.amiramit.bitsafe.server;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.logging.Logger;

import com.amiramit.bitsafe.client.NotLoggedInException;
import com.amiramit.bitsafe.client.ServerCommService;
import com.amiramit.bitsafe.client.UITicker;
import com.amiramit.bitsafe.client.UIVerifyException;
import com.amiramit.bitsafe.shared.FieldVerifier;
import com.google.appengine.api.backends.BackendServiceFactory;
import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import com.google.gwt.user.server.rpc.XsrfProtectedServiceServlet;

/**
 * The server side implementation of the RPC service.
 */
@SuppressWarnings("serial")
public class ServerCommServiceImpl extends XsrfProtectedServiceServlet implements
		ServerCommService {
	private static final Logger LOG = Logger
			.getLogger(ServerCommServiceImpl.class.getName());

	@Override
	public UITicker getTicker(String symbol) throws UIVerifyException,
			NotLoggedInException {
		checkLoggedIn();
		// Verify that the input is valid.
		FieldVerifier.verifyValidSymbol(symbol);

		// symbol is ignored for now ...
		LOG.info("getTicker with symbol: " + symbol);

		String backendURLString = BackendServiceFactory.getBackendService()
				.getBackendAddress("bitsafe-backend");
		URL url;
		UITicker lastTicker = null;
		try {
			url = new URL("http://" + backendURLString + "/get_last_ticker");
			URLConnection conn = url.openConnection();
			ObjectInputStream is = new ObjectInputStream(conn.getInputStream());
			lastTicker = (UITicker) is.readObject();
		} catch (IOException | ClassNotFoundException e) {
			LOG.severe("Error getting last ticker from backend: " + e);
			e.printStackTrace();
		}

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
