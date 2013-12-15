package com.amiramit.bitsafe.client.UITypes;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

import com.amiramit.bitsafe.shared.ExchangeName;
import com.amiramit.bitsafe.shared.FieldVerifier;

public class UIStopLossRule extends UITradeRule implements UIElement,
		Serializable {

	private static final long serialVersionUID = 1L;

	private BigDecimal atPrice;

	public UIStopLossRule(final Long dbKey, final Date createDate,
			final String name, final Boolean active,
			final ExchangeName atExchange, final BigDecimal atPrice) {
		super(dbKey, createDate, name, active, atExchange);
		this.atPrice = atPrice;
	}

	public UIStopLossRule(final String name, final Boolean active,
			final ExchangeName atExchange, final BigDecimal atPrice) {
		super(null, null, name, active, atExchange);
		this.atPrice = atPrice;
	}

	protected UIStopLossRule() {
		super();
	}

	public BigDecimal getAtPrice() {
		return atPrice;
	}

	@Override
	public void verify() throws UIVerifyException {
		FieldVerifier.verifyNotNull(atPrice);

		if (atPrice.compareTo(BigDecimal.ZERO) <= 0) {
			throw new UIVerifyException("UIStopLossRule price <= 0");
		}

		super.verify();
	}
}
