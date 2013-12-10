package com.amiramit.bitsafe.server;

import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;

import org.joda.money.BigMoney;

import com.google.appengine.api.users.User;

@PersistenceCapable(identityType = IdentityType.APPLICATION)
public class StopLossRule extends TradeRule {

	@Persistent
	private final BigMoney price;

	public StopLossRule(final User user, final String name, final Boolean active, final BigMoney price) {
		super(user, name, active);
		this.price = price;
	}

	public BigMoney getPrice() {
		return price;
	}
}
