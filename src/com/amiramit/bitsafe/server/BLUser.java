package com.amiramit.bitsafe.server;

import static com.amiramit.bitsafe.server.OfyService.ofy;
import static com.google.common.base.Preconditions.checkNotNull;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

import javax.servlet.http.HttpSession;

import com.amiramit.bitsafe.client.NotLoggedInException;
import com.googlecode.objectify.annotation.Cache;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Index;
import com.googlecode.objectify.condition.IfNotNull;

@Entity
@Cache
public class BLUser {
	private static final Logger LOG = Logger.getLogger(BLUser.class.getName());

	@Id
	private Long userId;
	@Index(IfNotNull.class)
	private List<String> socialUserIds;

	private Date creationDate;
	private Date lastLogIn;
	private Date lastChannelClientIdSet;
	private String channelClientID;

	private String email;
	private String nickname;

	public BLUser(final String email, final String nickname) {
		super();
		this.creationDate = new Date();
		this.email = email;
		this.nickname = nickname;
	}

	/**
	 * This constructor exists for frameworks (e.g. Objectify) that require it
	 * for serialization purposes. It should not be called explicitly.
	 */
	@SuppressWarnings("unused")
	private BLUser() {

	}

	public Long getUserId() {
		return userId;
	}

	public Date getLastLogIn() {
		return lastLogIn;
	}

	public Date getCreationDate() {
		return creationDate;
	}

	public String getEmail() {
		return email;
	}

	public String getNickname() {
		return nickname;
	}

	public static BLUser getUserFromId(final long userID) {
		return ofy().load().type(BLUser.class).id(userID).safe();
	}

	public static BLUser getUserFromSocialId(final String socialUserID) {
		checkNotNull(socialUserID);
		return ofy().load().type(BLUser.class)
				.filter("socialUserIds", socialUserID).first().safe();
	}

	public static BLUser getUserFromSession(final HttpSession session)
			throws NotLoggedInException {
		final Long userId = (Long) session.getAttribute("userID");
		if (userId == null) {
			throw new NotLoggedInException("Not logged in.");
		}

		final BLUser ret = BLUser.getUserFromId(userId);
		return ret;
	}

	public void save() {
		ofy().save().entity(this);
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

	public void addSocialUserIds(final String socialUserId) {
		if (socialUserIds == null) {
			socialUserIds = new ArrayList<String>(1);
		}
		socialUserIds.add(socialUserId);
	}

	public void onLogin(final HttpSession session) {
		checkNotNull(session);
		session.setAttribute("userID", getUserId());
		lastLogIn = new Date();
	}

	public void onLogout(final HttpSession session) {
		checkNotNull(session);
		session.removeAttribute("userID");
	}
}
