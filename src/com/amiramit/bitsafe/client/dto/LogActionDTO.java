package com.amiramit.bitsafe.client.dto;

public class LogActionDTO extends ActionDTO {
	private static final long serialVersionUID = 1L;

	@Override
	public void verify() throws UIVerifyException {
		// no data - no need to verify
	}
}
