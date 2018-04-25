package exceptions.campus;

import utils.exceptions.campus.Errors;

public class InvalidSessionException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3231033715072349656L;
	
	public InvalidSessionException() {
		super(Errors.NOSESSION);
	}
	
	public InvalidSessionException(String msg) {
		super(msg);
	}

}
