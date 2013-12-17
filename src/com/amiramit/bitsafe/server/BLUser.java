package com.amiramit.bitsafe.server;

import static com.amiramit.bitsafe.server.OfyService.ofy;
import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Date;
import java.util.logging.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import com.amiramit.bitsafe.client.NotLoggedInException;
import com.google.appengine.api.channel.ChannelServiceFactory;
import com.googlecode.objectify.annotation.Cache;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;

@Entity
@Cache
public class BLUser {
	private static final Logger LOG = Logger.getLogger(BLUser.class.getName());

	@Id
	private String userID;

	private Date creationDate;
	private Date lastLogIn;
	private String channelClientID;

	public BLUser(String userID) {
		super();
		this.userID = userID;
		this.creationDate = new Date();
		this.lastLogIn = new Date();
	}

	/**
	 * This constructor exists for frameworks (e.g. Google Web Toolkit) that
	 * require it for serialization purposes. It should not be called
	 * explicitly.
	 */
	@SuppressWarnings("unused")
	private BLUser() {
	}

	public String getUserID() {
		return userID;
	}

	public Date getLastLogIn() {
		return lastLogIn;
	}

	public Date getCreationDate() {
		return creationDate;
	}

	static protected BLUser getUser(String userID) {
		checkNotNull(userID);
		return ofy().load().type(BLUser.class).id(userID).safe();
	}

	public static BLUser checkLoggedIn(HttpServletRequest req)
			throws NotLoggedInException {
		HttpSession session = req.getSession(false);
		String userId = (String) session.getAttribute("userID");
		if (userId == null) {
			throw new NotLoggedInException("Not logged in.");
		}

		BLUser ret = BLUser.getUser(userId);
		LOG.info("checkLoggedIn for user: " + ret);
		return ret;
	}

	public void save() {
		ofy().save().entity(this);
	}

	public void onLogin() {
		channelClientID = null;
		lastLogIn = new Date();
	}

	public String establishChannel(String JSESSIONID) {
		if (channelClientID != null) {
			LOG.info("establishChannel with session ID: " + JSESSIONID
					+ " returning existing channel id: " + channelClientID);
			return channelClientID;
		}

		// TODO: Add some salt here?
		String token = JSESSIONID;
		channelClientID = ChannelServiceFactory.getChannelService()
				.createChannel(JSESSIONID);
		LOG.info("establishChannel token new token " + token + " for user: " + this);
		return token;
	}

	public void onChannelDisconnect() {
		channelClientID = null;
	}

	public String getChannelID() {
		return channelClientID;
	}
}
