package com.amiramit.bitsafe.server;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import com.amiramit.bitsafe.client.uitypes.UIVerifyException;
import com.amiramit.bitsafe.server.login.LoginProviderName;
import com.amiramit.bitsafe.shared.FieldVerifier;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.appengine.api.users.User;
import com.googlecode.objectify.NotFoundException;

public class SocialUser {
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
		this.nickname = (String) userData.get("name");
		this.email = (String) userData.get("email");
		this.providerName = providerName;
		verify();
	}

	public SocialUser(final User user) throws UIVerifyException {
		// TODO: get as much information on the user as possible
		this.id = user.getUserId();
		this.email = user.getEmail();
		this.providerName = LoginProviderName.GOOGLE;
		verify();
	}

	private void verify() throws UIVerifyException {
		FieldVerifier.verifyNotNull(providerName);
		FieldVerifier.verifyString(id);
		if (nickname != null) {
			FieldVerifier.verifyString(nickname);
		}
		if (email != null) {
			FieldVerifier.verifyEmail(email);
		}
	}

	public LoginProviderName getProviderName() {
		return providerName;
	}

	public String getId() {
		return id;
	}

	public BLUser toBLUser() {
		final String userSocialId = providerName.toString() + getId();
		BLUser ret;

		try {
			ret = BLUser.getUser(userSocialId);
		} catch (final NotFoundException e) {
			ret = new BLUser(email, nickname);
			ret.addSocialUserIds(userSocialId);
		}

		return ret;
	}
}
