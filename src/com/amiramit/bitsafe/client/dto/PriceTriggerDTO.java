package com.amiramit.bitsafe.client.dto;

import java.math.BigDecimal;

import com.amiramit.bitsafe.shared.ExchangeName;
import com.amiramit.bitsafe.shared.FieldVerifier;

public class PriceTriggerDTO extends TriggerDTO {
	private static final long serialVersionUID = 1L;

	public enum TYPE {
		LOWER, HIGHER
	};

	private TYPE type;
	private BigDecimal atPrice;

	// For GWT serialization
	@SuppressWarnings("unused")
	private PriceTriggerDTO() {
		super();
	}

	public PriceTriggerDTO(final ExchangeName atExchange, final TYPE type,
			final BigDecimal atPrice) {
		super(atExchange);
		this.type = type;
		this.atPrice = atPrice;
	}

	public TYPE getType() {
		return type;
	}

	public BigDecimal getAtPrice() {
		return atPrice;
	}

	@Override
	public void verify() throws UIVerifyException {
		FieldVerifier.verifyNotNull(type);

		if (atPrice.compareTo(BigDecimal.ZERO) <= 0) {
			throw new UIVerifyException("PriceTriggerDTO price <= 0");
		}
	}
}
