package com.amiramit.bitsafe.client.UITypes;

import java.io.Serializable;
import java.util.logging.Logger;

public class UIVerifyException extends Exception implements Serializable {

	private static final Logger LOG = Logger.getLogger(UIVerifyException.class
			.getName());

	private static final long serialVersionUID = 1L;

	public UIVerifyException() {
		super();
		LOG.severe("UIVerifyException");
	}

	public UIVerifyException(final String message) {
		super(message);
		LOG.severe("UIVerifyException with messege: " + message);
	}
}