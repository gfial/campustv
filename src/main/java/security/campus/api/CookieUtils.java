package security.campus.api;

import java.util.UUID;

import org.jasypt.encryption.pbe.StandardPBEStringEncryptor;

public class CookieUtils {
	
	public static String getSession() {
		String token = Utils.generateRandomString("");
		String key = UUID.randomUUID().toString().toUpperCase() +
		        "|" + token;
		StandardPBEStringEncryptor jasypt = new StandardPBEStringEncryptor();
		
		// this is the authentication token user will send in order to use the web service
		jasypt.setPassword(UUID.randomUUID().toString());
		//TODO isnt this unsecure?
		String authenticationToken = jasypt.encrypt(key);
		return authenticationToken;
	}
}
