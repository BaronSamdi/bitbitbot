package com.amiramit.bitsafe.server.login;

import java.util.logging.Logger;

public class PwdUtils {
	private static final Logger LOG = Logger
			.getLogger(PwdUtils.class.getName());
	
	// Define the BCrypt workload to use when generating password hashes. 10-31
	// is a valid value.
	private static int WORKLOAD = 12;

	public static String hashPassword(String password) {
		// gensalt's log_rounds parameter determines the complexity
		// the work factor is 2**log_rounds, and the default is 10
		return BCrypt.hashpw(password, BCrypt.gensalt(WORKLOAD));
	}

	public static boolean checkPassword(String candidate, String hashed) {
		LOG.severe("checkPassword candidate: " + candidate + " hashed: " + hashed + 
				"candidate hashed: " + hashPassword(candidate));
		return BCrypt.checkpw(candidate, hashed);
	}
}
