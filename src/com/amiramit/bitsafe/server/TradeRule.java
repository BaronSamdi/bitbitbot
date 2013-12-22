package com.amiramit.bitsafe.server;

import static com.amiramit.bitsafe.server.OfyService.ofy;

import java.util.Date;

import com.amiramit.bitsafe.shared.ExchangeName;
import com.googlecode.objectify.annotation.Cache;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Index;
import com.googlecode.objectify.condition.IfTrue;

@Entity
@Cache
public abstract class TradeRule {

	@Id
	private Long key;

	@Index
	private long userId;

	private Date createDate;

	private String name;

	@Index(IfTrue.class)
	private boolean active;

	@Index
	private ExchangeName atExchange;

	protected TradeRule() {
	}

	public TradeRule(final long userId, final String name,
			final boolean active, final ExchangeName atExchange) {
		assert name != null;
		assert atExchange != null;
		this.createDate = new Date();
		this.userId = userId;
		this.name = name;
		this.active = active;
		this.atExchange = atExchange;
	}

	public Long getKey() {
		return key;
	}

	public String getName() {
		return name;
	}

	public long getUserId() {
		return this.userId;
	}

	public Date getCreateDate() {
		return this.createDate;
	}

	public void setUser(final long userId) {
		this.userId = userId;
	}

	public ExchangeName getAtExchange() {
		return this.atExchange;
	}

	public boolean getActive() {
		return active;
	}

	protected void setActive(final boolean active) {
		this.active = active;
	}

	public boolean checkTrigger() {
		return active;
	}

	@Override
	public String toString() {
		return "TradeRule [key=" + key + ", userId=" + userId + ", createDate="
				+ createDate + ", name=" + name + ", active=" + active
				+ ", atExchange=" + atExchange + "]";
	}

	public abstract boolean trigger();

	public void save() {
		ofy().save().entity(this);
		assert (this.getKey() != null);
	}
}