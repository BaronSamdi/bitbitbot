package com.amiramit.bitsafe.server;

import java.math.BigDecimal;

import com.google.appengine.api.users.User;
import com.googlecode.objectify.annotation.EntitySubclass;

@EntitySubclass(index = true)
public class StopLossRule extends TradeRule {

	private BigDecimal atPrice;

	protected StopLossRule() {
	}

	public StopLossRule(final User user, final String name,
			final Boolean active, final BigDecimal atPrice) {
		super(user, name, active);
		this.atPrice = atPrice;
	}

	public BigDecimal getAtPrice() {
		return atPrice;
	}
}
