package com.amiramit.bitsafe.server;

import java.util.Date;

import com.google.appengine.api.users.User;
import com.googlecode.objectify.annotation.Cache;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Index;
import com.googlecode.objectify.condition.IfTrue;

@Entity
public abstract class TradeRule {

	@Id
	private Long key;

	@Index
	private User user;

	private Date createDate;

	private String name;

	@Index(IfTrue.class)
	private boolean active;

	protected TradeRule() {
	}

	public TradeRule(final User user, final String name, final boolean active) {
		this.createDate = new Date();
		this.user = user;
		this.name = name;
		this.active = active;
	}

	public Long getKey() {
		return key;
	}

	public String getName() {
		return name;
	}

	public User getUser() {
		return this.user;
	}

	public Date getCreateDate() {
		return this.createDate;
	}

	public void setUser(final User user) {
		this.user = user;
	}

	public boolean getActive() {
		return active;
	}

	public boolean checkTrigger() {
		XXX
	}
}