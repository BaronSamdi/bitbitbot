package com.amiramit.bitsafe.client.dto;

import java.io.Serializable;

import com.amiramit.bitsafe.shared.ExchangeName;

public abstract class TriggerDTO implements BasicDTO, Serializable {
	private static final long serialVersionUID = 1L;

	private ExchangeName atExchange;

	// For GWT serialization
	protected TriggerDTO() {
	}

	public TriggerDTO(final ExchangeName atExchange) {
		super();
		this.atExchange = atExchange;
	}

	public ExchangeName getAtExchange() {
		return atExchange;
	}
}
