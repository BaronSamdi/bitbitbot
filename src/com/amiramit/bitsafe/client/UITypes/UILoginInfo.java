package com.amiramit.bitsafe.client.UITypes;

import java.io.Serializable;

public class UILoginInfo implements Serializable {

	private static final long serialVersionUID = 1L;

	private boolean loggedIn = false;
	private String loginUrl;
	private String logoutUrl;
	private String emailAddress;
	private String nickname;
	private String channelToken;

	public void setLoginUrl(String loginUrl) {
		this.loginUrl = loginUrl;
	}

	public void setLogoutUrl(String logoutUrl) {
		this.logoutUrl = logoutUrl;
	}

	public void setEmailAddress(String emailAddress) {
		this.emailAddress = emailAddress;
	}

	public void setNickname(String nickname) {
		this.nickname = nickname;
	}

	public void setChannelToken(String channelToken) {
		this.channelToken = channelToken;
	}

	public boolean isLoggedIn() {
		return loggedIn;
	}

	public void setLoggedIn(final boolean loggedIn) {
		this.loggedIn = loggedIn;
	}

	public String getLoginUrl() {
		return loginUrl;
	}

	public String getLogoutUrl() {
		return logoutUrl;
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