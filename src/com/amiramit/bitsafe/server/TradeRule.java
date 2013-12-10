package com.amiramit.bitsafe.server;

import java.util.Date;

import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.Inheritance;
import javax.jdo.annotations.InheritanceStrategy;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

import com.google.appengine.api.users.User;

@PersistenceCapable(identityType = IdentityType.APPLICATION)
@Inheritance(strategy = InheritanceStrategy.SUBCLASS_TABLE)
public class TradeRule {

	@PrimaryKey
	@Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
	private Long id;
	@Persistent
	private User user;
	@Persistent
	private final Date createDate;
	@Persistent
	private String name;	
	@Persistent
	private Boolean active;


	private TradeRule() {
		this.createDate = new Date();
	}

	public TradeRule(final User user, final String name, final Boolean active) {
		this();
		this.user = user;
		this.name = name;
		this.active = active;
	}

	public Long getId() {
		return id;
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

	public Boolean getActive() {
		return active;
	}
}