package com.amiramit.bitsafe.client;

import java.io.Serializable;
import java.util.Date;

import com.amiramit.bitsafe.shared.FieldVerifier;

public class UIStopLossRule extends AbstractUITradeRule implements UIElement, Serializable {
	
	private static final long serialVersionUID = 1L;
	
	private UIBigMoney price;
	
	public UIStopLossRule(final Long dbKey, final Date createDate, final String name, final Boolean active,
			final UIBigMoney price) {
		super(dbKey, createDate, name, active);
		this.price = price;
	}
	
	public UIStopLossRule(final String name, final Boolean active,
			final UIBigMoney price) {
		super(INVALID_DB_ID, null, name, active);
		this.price = price;
	}
	
	public UIStopLossRule() {
		super(INVALID_DB_ID, null, null, null);
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
