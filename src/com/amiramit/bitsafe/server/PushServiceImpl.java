package com.amiramit.bitsafe.server;

import java.util.Date;
import java.util.logging.Logger;

import com.amiramit.bitsafe.client.NotLoggedInException;
import com.amiramit.bitsafe.client.service.PushService;
import com.google.appengine.api.channel.ChannelServiceFactory;
import com.google.gwt.user.server.rpc.XsrfProtectedServiceServlet;

@SuppressWarnings("serial")
public class PushServiceImpl extends XsrfProtectedServiceServlet implements
		PushService {

	private static final Logger LOG = Logger.getLogger(PushServiceImpl.class
			.getName());
	private static final int DEFAULT_TIMEOUT_IN_MINUTES = 120;
	// If the channel is going to expire anyway in a minute, create a new one
	private static final long DEFAULT_TIMEOUT_PLUS_IN_MILLIS = (60 * 1000 * DEFAULT_TIMEOUT_IN_MINUTES)
			+ (60 * 1000);

	@Override
	public String getChannelKey() throws NotLoggedInException {
		final BLUser user = BLUser.checkLoggedIn(getThreadLocalRequest());

		return newChannelToken(user, getThreadLocalRequest().getSession()
				.getId());
	}

	public static String getChannelToken(final BLUser user,
			final String jSessionId) {
		String channelClientID = user.getChannelClientID();
		if (channelClientID != null
				&& user.getLastChannelClientIdSet() != null
				&& ((user.getLastChannelClientIdSet().getTime() + DEFAULT_TIMEOUT_PLUS_IN_MILLIS) > new Date()
						.getTime())) {
			LOG.info("establishChannel return existing " + channelClientID
					+ " for user: " + user);
			return channelClientID;
		}

		channelClientID = newChannelToken(user, jSessionId);
		return channelClientID;
	}

	private static String newChannelToken(final BLUser user,
			final String jSessionId) {
		String channelClientID;
		// TODO: Add some salt here?
		final String token = jSessionId;
		channelClientID = ChannelServiceFactory.getChannelService()
				.createChannel(token, DEFAULT_TIMEOUT_IN_MINUTES);
		user.setChannelClientID(channelClientID);
		user.save();
		LOG.info("establishChannel new " + channelClientID + " for user: "
				+ user);
		return channelClientID;
	}
}