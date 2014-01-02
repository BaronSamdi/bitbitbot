package com.amiramit.bitsafe.server;

import static com.amiramit.bitsafe.server.OfyService.ofy;
import static com.google.common.base.Preconditions.checkNotNull;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

import com.amiramit.bitsafe.server.login.PwdUtils;
import com.googlecode.objectify.annotation.Cache;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Index;

@Entity
@Cache
public class BLUser {
	private static final Logger LOG = Logger.getLogger(BLUser.class.getName());

	@Id
	private Long userId;
	@Index
	private List<String> socialUserIds;
	private String hashedPasswd;

	private Date lastLogIn;
	private Date lastChannelClientIdSet;
	private String channelClientID;

	private Date creationDate;
	private String email;
	private String nickname;

	public BLUser(final String socialUserId, final String email,
			final String passwd, final String nickname) {
		super();
		this.socialUserIds = new ArrayList<String>(1);
		this.socialUserIds.add(socialUserId);
		this.hashedPasswd = PwdUtils.hashPassword(passwd);
		this.creationDate = new Date();
		this.email = email;
		this.nickname = nickname;
	}

	public boolean checkPassword(final String candidate) {
		return PwdUtils.checkPassword(candidate, hashedPasswd);
	}

	public void setPassword(final String passwd) {
		this.hashedPasswd = PwdUtils.hashPassword(passwd);
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

	public void save() {
		ofy().save().entity(this);
	}

	public void saveNow() {
		ofy().save().entity(this).now();
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

	public void onLogin() {
		lastLogIn = new Date();
	}

	public void onLogout() {
	}
}
