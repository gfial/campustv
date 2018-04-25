package exceptions.campus;

import utils.exceptions.campus.Errors;

public class NotAuthorizedException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 971305580218391584L;
	
	public NotAuthorizedException() {
		super(Errors.NOTALLOWED);
	}
	
	public NotAuthorizedException(String msg) {
		super(msg);
	}
}
