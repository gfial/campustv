package exceptions.campus;

import utils.exceptions.campus.Errors;

public class NoSuchAuthenticatedTagException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 506471149685414434L;

	public NoSuchAuthenticatedTagException() {
		super(Errors.NOSUCHAUTHTAG);
	}
	
	public NoSuchAuthenticatedTagException(String msg) {
		super(msg);
	}
}
