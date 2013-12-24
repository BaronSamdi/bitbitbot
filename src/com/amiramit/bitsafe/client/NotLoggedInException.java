package com.amiramit.bitsafe.client;

import java.io.Serializable;

public class NotLoggedInException extends Exception implements Serializable {

	private static final long serialVersionUID = 1L;

	public NotLoggedInException() {
		super();
	}

	public NotLoggedInException(final String message) {
		super(message);
	}

}