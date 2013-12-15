package com.amiramit.bitsafe.server;

import java.math.BigDecimal;
import java.util.logging.Logger;

import com.amiramit.bitsafe.shared.ExchangeName;
import com.google.appengine.api.users.User;
import com.googlecode.objectify.annotation.EntitySubclass;

@EntitySubclass(index = true)
public class StopLossRule extends TradeRule {
	private static final Logger LOG = Logger
			.getLogger(StopLossRule.class.getName());

	private BigDecimal atPrice;

	protected StopLossRule() {
	}

	public StopLossRule(final User user, final String name,
			final Boolean active, final ExchangeName atExchange, final BigDecimal atPrice
			) {
		super(user, name, active, atExchange);
		this.atPrice = atPrice;
	}

	public BigDecimal getAtPrice() {
		return atPrice;
	}

	@Override
	public boolean checkTrigger() {
		if (!super.checkTrigger())
			return false;
		
		BLLastTicker lastTicker = BLLastTicker.getLastTicker(getAtExchange());
		LOG.info("StopLossRule: " + this.toString() + " is at checkTrigger with lastTicker = " + lastTicker);
		if (lastTicker.getLast().compareTo(atPrice) < 0) {
			LOG.info("StopLossRule: " + this.toString() + " return true");
			return true;
		}

		LOG.info("StopLossRule: " + this.toString() + " return false");
		return false;
	}

	@Override
	public boolean trigger() {
		LOG.info("StopLossRule: " + this.toString() + " is at trigger()");
		// Make sure checkTrigger() condition is still valid as there might be some time passed 
		// between rule checkTrigger() call and trigger() call
		if (checkTrigger()) {
			LOG.severe("StopLossRule: " + this.toString() + " triggered!");
			// Currently just print to log ...
			// TODO: do the actual sell in relevant exchange ...
		}
		return false;
	}	
	

	@Override
	public String toString() {
		return "StopLossRule [" + super.toString() + ", atPrice=" + atPrice +  "]";
	}
}
