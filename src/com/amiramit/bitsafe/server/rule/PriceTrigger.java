package com.amiramit.bitsafe.server.rule;

import java.math.BigDecimal;
import java.util.logging.Logger;

import com.amiramit.bitsafe.server.BLLastTicker;
import com.amiramit.bitsafe.shared.ExchangeName;

public class PriceTrigger extends Trigger {
	private static final long serialVersionUID = 1L;
	private static final Logger LOG = Logger.getLogger(PriceTrigger.class
			.getName());

	public enum TYPE {
		LOWER, HIGHER
	};

	private TYPE type;
	private BigDecimal atPrice;

	public PriceTrigger(final ExchangeName atExchange, final TYPE type,
			final BigDecimal atPrice) {
		super(atExchange);
		this.type = type;
		this.atPrice = atPrice;
	}

	@Override
	public boolean check() {
		final BLLastTicker lastTicker = BLLastTicker
				.getLastTicker(getAtExchange());

		boolean res;
		switch (type) {
		case LOWER:
			res = lastTicker.getLast().compareTo(atPrice) < 0;
			break;

		case HIGHER:
			res = lastTicker.getLast().compareTo(atPrice) > 0;
			break;

		default:
			LOG.severe("Invalid type!");
			return false;
		}

		LOG.info("PriceTrigger: " + this.toString() + " at ticker: "
				+ lastTicker + " return " + res);
		return res;
	}

	public TYPE getType() {
		return type;
	}

	public BigDecimal getAtPrice() {
		return atPrice;
	}
}
