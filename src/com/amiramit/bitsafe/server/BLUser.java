package com.amiramit.bitsafe.server;

import static com.amiramit.bitsafe.server.OfyService.ofy;
import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Date;
import java.util.logging.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import com.amiramit.bitsafe.client.NotLoggedInException;
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
	private Date lastChannelClientIdSet;
	private String channelClientID;

	public BLUser(final String userID) {
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

	protected static BLUser getUser(final String userID) {
		checkNotNull(userID);
		return ofy().load().type(BLUser.class).id(userID).safe();
	}

	public static BLUser checkLoggedIn(final HttpServletRequest req)
			throws NotLoggedInException {
		final HttpSession session = req.getSession(false);
		final String userId = (String) session.getAttribute("userID");
		if (userId == null) {
			throw new NotLoggedInException("Not logged in.");
		}

		final BLUser ret = BLUser.getUser(userId);
		LOG.info("checkLoggedIn for user: " + ret);
		return ret;
	}

	public void save() {
		ofy().save().entity(this);
	}

	public void onLogin() {
		lastLogIn = new Date();
	}

	public String getChannelClientID() {
		return channelClientID;
	}

	public void setChannelClientID(final String channelClientID) {
		this.channelClientID = channelClientID;
		this.lastChannelClientIdSet = new Date();
	}

	public Date getLastChannelClientIdSet() {
		return lastChannelClientIdSet;
	}
}
