package com.amiramit.bitsafe.server;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import com.amiramit.bitsafe.client.uitypes.UIVerifyException;
import com.amiramit.bitsafe.server.LoginCallbackServlet.ProviderName;
import com.amiramit.bitsafe.shared.FieldVerifier;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.appengine.api.users.User;
import com.googlecode.objectify.NotFoundException;

public class SocialUser {
	private ProviderName providerName;
	private String id;

	public SocialUser(ProviderName providerName, String json)
			throws JsonParseException, JsonMappingException, IOException,
			UIVerifyException {
		// TODO: get as much information on the user as possible
		ObjectMapper mapper = new ObjectMapper();
		Map<String, Object> userData = mapper.readValue(json,
				new TypeReference<HashMap<String, Object>>() {
				});

		this.id = userData.get("id").toString();
		FieldVerifier.verifyString(id);
		this.providerName = providerName;
	}

	public SocialUser(User user) throws UIVerifyException {
		// TODO: get as much information on the user as possible
		this.id = user.getUserId();
		FieldVerifier.verifyString(id);
		this.providerName = ProviderName.GOOGLE;
	}

	public ProviderName getProviderName() {
		return providerName;
	}

	public String getId() {
		return id;
	}

	public BLUser toBLUser() {
		String userUniqueId = providerName == null ? "GOOGLE" : providerName
				.toString() + getId();
		BLUser ret = null;

		try {
			ret = BLUser.getUser(userUniqueId);
		} catch (NotFoundException e) {
			ret = new BLUser(userUniqueId);
		}

		return ret;
	}
}
