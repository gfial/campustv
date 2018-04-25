package exceptions.campus;

import utils.exceptions.campus.Errors;

public class NoSuchNewsException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2332550488706657783L;
	public NoSuchNewsException() {
		super(Errors.NOSUCHNEWS);
	}

	public NoSuchNewsException(String msg) {
		super(msg);
	}
}
