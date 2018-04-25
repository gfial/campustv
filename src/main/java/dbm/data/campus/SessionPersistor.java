package dbm.data.campus;

import security.campus.api.CookieUtils;
import security.campus.api.PasswordUtils;
import utils.campus.Logger;
import controller.db.campus.SessionController;
import data.campus.Member;
import exceptions.campus.InvalidSessionException;
import exceptions.campus.NoSuchMemberException;
import exceptions.campus.TransactionProcessingException;

public class SessionPersistor {

	public static String generateSessionId(Member member) throws TransactionProcessingException {
		String sessionId = CookieUtils.getSession();
		SessionController.saveSessionId(sessionId, member.getId());
		return sessionId;
	}

	public static boolean checkUser(int id, String password) {
		String storePass;
		try {
			storePass = SessionController.getStoredPass(id);
		} catch (NoSuchMemberException e1) {
			Logger.verboseLog(e1.getLocalizedMessage());
			return false;
		}
		try {
			return PasswordUtils.check(password, storePass);
		} catch (Exception e) {
			Logger.log(e.getLocalizedMessage());
		}
		return false;
	}

	public static int getMemberId(String sessionId) throws InvalidSessionException, NoSuchMemberException {
		return SessionController.getMemberId(sessionId);
	}

	public static void deleteSession(String sessionId) throws TransactionProcessingException {
		SessionController.deleteSession(sessionId);
	}

	public static void createUser(int memberId, String password) throws TransactionProcessingException {
		String passHash;
		try {
			passHash = PasswordUtils.getSaltedHash(password);
		} catch (Exception e) {
			Logger.log(e.getLocalizedMessage());
			throw new TransactionProcessingException();
		}
		SessionController.createUser(memberId, passHash);
	}

}
