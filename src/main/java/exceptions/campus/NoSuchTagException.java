package exceptions.campus;

import utils.exceptions.campus.Errors;

public class NoSuchTagException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6699184128433599407L;
	public NoSuchTagException() {
		super(Errors.NOSUCHNEWS);
	}

	public NoSuchTagException(String msg) {
		super(msg);
	}

}
