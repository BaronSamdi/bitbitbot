package com.amiramit.bitsafe.client;

import java.io.Serializable;
import java.util.logging.Logger;

public class NotLoggedInException extends Exception implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private static final Logger LOG = Logger.getLogger(NotLoggedInException.class
			.getName());

	public NotLoggedInException() {
		super();
		LOG.severe("NotLoggedInException");
	}

	public NotLoggedInException(final String message) {
		super(message);
		LOG.severe("NotLoggedInException with messege: " + message);
	}

}