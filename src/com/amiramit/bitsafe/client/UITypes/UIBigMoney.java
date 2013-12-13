package com.amiramit.bitsafe.client.UITypes;

import java.io.Serializable;
import java.math.BigDecimal;

import com.amiramit.bitsafe.shared.FieldVerifier;

public class UIBigMoney implements UIElement, Serializable {
	private static final long serialVersionUID = 1L;
	
	private UICurrencyUnit unit;
	private BigDecimal amount;

	public UIBigMoney(final UICurrencyUnit unit, final BigDecimal amount) {
		super();
		this.unit = unit;
		this.amount = amount;
	}
	
	public UIBigMoney() {
		this(null, null);
	}

	public BigDecimal getAmount() {
		return amount;
	}

	public UICurrencyUnit getUnit() {
		return unit;
	}

	@Override
	public void verify() throws UIVerifyException {
		FieldVerifier.verifyNotNull(unit);
		FieldVerifier.verifyNotNull(amount);
	}
}
