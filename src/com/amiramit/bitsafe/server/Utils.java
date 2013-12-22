package com.amiramit.bitsafe.server;

import java.util.UUID;

public class Utils {
	
	private Utils() {};

	public static String getRandomString() {
		return UUID.randomUUID().toString();
	}
}
