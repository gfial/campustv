package security.campus.api;

import java.security.SecureRandom;

public class Utils {
	
	public static String DEFAULT_SEED = "0xabcdef";

	public static SecureRandom sr = null;
	
	static {
		sr = new SecureRandom();
		sr.setSeed(System.nanoTime());
	}
	
	public static String generateRandomString(String input) {
		input += DEFAULT_SEED;
		String base = "";
		sr.setSeed(new String(SecureRandom.getSeed(64) + input).getBytes());
		for(int i = 0; i < 1<<3; i++) {
			base += sr.nextLong();
		}
		return base;
	}
	
	public static byte[] getRandomSalt() {
		return generateRandomString(DEFAULT_SEED).getBytes();
	}
	
}
