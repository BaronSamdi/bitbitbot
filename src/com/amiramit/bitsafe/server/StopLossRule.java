package com.amiramit.bitsafe.server;

import org.joda.money.BigMoney;

import com.google.appengine.api.users.User;
import com.googlecode.objectify.annotation.Cache;
import com.googlecode.objectify.annotation.EntitySubclass;
import com.googlecode.objectify.annotation.Serialize;

@EntitySubclass(index=true)
public class StopLossRule extends TradeRule {

	// TODO: Use BigDecimal here instead of @Serialize?
	@Serialize
	private BigMoney price;

	protected StopLossRule() {
	}
			
	public StopLossRule(final User user, final String name, final Boolean active, final BigMoney price) {
		super(user, name, active);
		this.price = price;
	}

	public BigMoney getPrice() {
		return price;
	}
}
