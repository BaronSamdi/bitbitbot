package com.amiramit.bitsafe.server.login;

public class PwdUtils {
	// Define the BCrypt workload to use when generating password hashes. 10-31
	// is a valid value.
	private static int WORKLOAD = 12;

	public static String hashPassword(String password) {
		// gensalt's log_rounds parameter determines the complexity
		// the work factor is 2**log_rounds, and the default is 10
		return BCrypt.hashpw(password, BCrypt.gensalt(WORKLOAD));
	}

	public static boolean checkPassword(String candidate, String hashed) {
		return BCrypt.checkpw(candidate, hashed);
	}
}
