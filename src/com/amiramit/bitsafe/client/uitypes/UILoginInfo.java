package com.amiramit.bitsafe.client.uitypes;

import java.io.Serializable;

public class UILoginInfo implements Serializable {

	private static final long serialVersionUID = 1L;

	private String emailAddress;
	private String nickname;
	private String channelToken;

	public UILoginInfo() {

	}

	public void setEmailAddress(final String emailAddress) {
		this.emailAddress = emailAddress;
	}

	public void setNickname(final String nickname) {
		this.nickname = nickname;
	}

	public void setChannelToken(final String channelToken) {
		this.channelToken = channelToken;
	}

	public String getEmailAddress() {
		return emailAddress;
	}

	public String getNickname() {
		return nickname;
	}

	public String getChannelToken() {
		return channelToken;
	}
}