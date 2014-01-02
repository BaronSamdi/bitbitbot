package com.amiramit.bitsafe.server.rule;

import java.io.Serializable;

import com.amiramit.bitsafe.client.dto.PriceTriggerDTO;
import com.amiramit.bitsafe.client.dto.TriggerDTO;
import com.amiramit.bitsafe.client.dto.UIVerifyException;
import com.amiramit.bitsafe.shared.CurrencyPair;
import com.amiramit.bitsafe.shared.Exchange;

public abstract class Trigger implements Serializable {
	private static final long serialVersionUID = 1L;

	private Exchange exchange;
	private CurrencyPair currencyPair;

	public Trigger(final Exchange exchange, final CurrencyPair currencyPair) {
		super();
		this.exchange = exchange;
		this.currencyPair = currencyPair;
	}

	public Exchange getExchange() {
		return exchange;
	}

	public CurrencyPair getCurrencyPair() {
		return currencyPair;
	}

	public abstract boolean check();

	public static Trigger fromDTO(final TriggerDTO uiTrigger)
			throws UIVerifyException {
		if (uiTrigger instanceof PriceTriggerDTO) {
			return new PriceTrigger(uiTrigger.getExchange(),
					uiTrigger.getCurrencyPair(),
					PriceTrigger.TYPE.valueOf(((PriceTriggerDTO) uiTrigger)
							.getType().name()),
					((PriceTriggerDTO) uiTrigger).getAtPrice());
		} else {
			throw new UIVerifyException("Unknown ui trigger type: "
					+ uiTrigger.getClass().getName());
		}
	}

	public static TriggerDTO toDTO(final Trigger trigger)
			throws UIVerifyException {
		if (trigger instanceof PriceTrigger) {
			return new PriceTriggerDTO(trigger.getExchange(),
					trigger.getCurrencyPair(),
					PriceTriggerDTO.TYPE.valueOf(((PriceTrigger) trigger)
							.getType().name()),
					((PriceTrigger) trigger).getAtPrice());
		} else {
			throw new UIVerifyException("Unknown server trigger type: "
					+ trigger.getClass().getName());
		}
	}
}
