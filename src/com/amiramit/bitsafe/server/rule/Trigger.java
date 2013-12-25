package com.amiramit.bitsafe.server.rule;

import java.io.Serializable;

import com.amiramit.bitsafe.client.dto.PriceTriggerDTO;
import com.amiramit.bitsafe.client.dto.TriggerDTO;
import com.amiramit.bitsafe.client.dto.UIVerifyException;
import com.amiramit.bitsafe.shared.ExchangeName;

public abstract class Trigger implements Serializable {
	private static final long serialVersionUID = 1L;

	private ExchangeName atExchange;

	public Trigger(final ExchangeName atExchange) {
		super();
		this.atExchange = atExchange;
	}

	public ExchangeName getAtExchange() {
		return atExchange;
	}

	public abstract boolean check();

	public static Trigger fromDTO(final TriggerDTO uiTrigger)
			throws UIVerifyException {
		if (uiTrigger instanceof PriceTriggerDTO) {
			return new PriceTrigger(uiTrigger.getAtExchange(),
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
			return new PriceTriggerDTO(trigger.getAtExchange(),
					PriceTriggerDTO.TYPE.valueOf(((PriceTrigger) trigger)
							.getType().name()),
					((PriceTrigger) trigger).getAtPrice());
		} else {
			throw new UIVerifyException("Unknown server trigger type: "
					+ trigger.getClass().getName());
		}
	}
}
