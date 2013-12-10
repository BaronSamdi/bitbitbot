package com.amiramit.bitsafe.client;

import java.io.Serializable;
import java.util.Date;

import com.amiramit.bitsafe.shared.FieldVerifier;

public class UIStopLossRule extends AbstractUITradeRule implements UIElement, Serializable {
	
	private static final long serialVersionUID = 1L;
	
	private UIBigMoney price;
	
	public UIStopLossRule(final Date createDate, final String name, final Boolean active,
			final UIBigMoney price) {
		super(createDate, name, active);
		this.price = price;
	}
	
	public UIStopLossRule() {
		super(null, null, null);
	}

	public UIBigMoney getPrice() {
		return price;
	}

	@Override
	public void verify() throws UIVerifyException {
		FieldVerifier.verifyNotNull(price);
		price.verify();
		super.verify();
	}
}
