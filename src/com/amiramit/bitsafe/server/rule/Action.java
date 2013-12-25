package com.amiramit.bitsafe.server.rule;

import java.io.Serializable;

import com.amiramit.bitsafe.client.dto.ActionDTO;
import com.amiramit.bitsafe.client.dto.LogActionDTO;
import com.amiramit.bitsafe.client.dto.UIVerifyException;

public abstract class Action implements Serializable {

	private static final long serialVersionUID = 1L;

	public abstract void run(final Rule myRule);

	public static Action fromDTO(final ActionDTO uiAction)
			throws UIVerifyException {
		if (uiAction instanceof LogActionDTO) {
			return new LogAction();
		} else {
			throw new UIVerifyException("Unknown ui action type: "
					+ uiAction.getClass().getName());
		}
	}

	public static ActionDTO toDTO(final Action action) throws UIVerifyException {
		if (action instanceof LogAction) {
			return new LogActionDTO();
		} else {
			throw new UIVerifyException("Unknown server action type: "
					+ action.getClass().getName());
		}
	}
}
