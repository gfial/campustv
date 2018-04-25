package exceptions.campus;

import utils.exceptions.campus.Errors;

public class NoSuchMemberException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = -647384150793735797L;
	
	public NoSuchMemberException() {
		super(Errors.NOSUCHMEMBER);
	}

	public NoSuchMemberException(String msg) {
		super(msg);
	}
}
