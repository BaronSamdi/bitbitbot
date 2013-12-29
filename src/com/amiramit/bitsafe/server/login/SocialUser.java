package com.amiramit.bitsafe.server.login;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import com.amiramit.bitsafe.client.dto.UIVerifyException;
import com.amiramit.bitsafe.server.BLUser;
import com.amiramit.bitsafe.shared.FieldVerifier;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.appengine.api.users.User;
import com.googlecode.objectify.NotFoundException;

public class SocialUser {
	private static final Logger LOG = Logger
			.getLogger(SocialUser.class.getName());
	
	private LoginProviderName providerName;
	private String id;
	private String email;
	private String nickname;

	public SocialUser(final LoginProviderName providerName, final String json)
			throws IOException, UIVerifyException {
		// TODO: get as much information on the user as possible
		final ObjectMapper mapper = new ObjectMapper();
		final Map<String, Object> userData = mapper.readValue(json,
				new TypeReference<HashMap<String, Object>>() {
				});

		this.id = (String) userData.get("id");
		this.email = (String) userData.get("email");
		this.nickname = (String) userData.get("name");		
		this.providerName = providerName;
		verify();
	}

	public SocialUser(final User user) throws UIVerifyException {
		// TODO: get as much information on the user as possible
		this.id = user.getUserId();
		this.email = user.getEmail();
		this.nickname = user.getNickname();
		this.providerName = LoginProviderName.GOOGLE;
		verify();
	}
	
	public SocialUser(final String userId, final String email) throws UIVerifyException {
		// TODO: get as much information on the user as possible
		this.id = userId;
		this.email = email;
		this.providerName = LoginProviderName.INT;
		verify();
	}

	private void verify() throws UIVerifyException {
		FieldVerifier.verifyNotNull(providerName);
		FieldVerifier.verifyString(id);
		if (nickname != null) {
			FieldVerifier.verifyString(nickname);
		}
		if (email != null) {
			LOG.info("email = " + email);
			FieldVerifier.verifyEmail(email);
		}
	}

	private String getId() {
		return providerName.toString() + id;
	}
	
	public BLUser getExistingBLUser() {
		final String userSocialId = getId();
		try {
			return BLUser.getUserFromSocialId(userSocialId);
		} catch (final NotFoundException e) {
			return null;
		}
	}
	
	public BLUser newBLUser(String passwd) {
		return new BLUser(getId(), email, passwd, nickname);
	}

	public BLUser toBLUser() {
		try {
			return BLUser.getUserFromSocialId(getId());
		} catch (final NotFoundException e) {
			return newBLUser(null);
		}
	}
}
