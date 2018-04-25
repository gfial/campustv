package exceptions.campus;

import utils.exceptions.campus.Errors;

public class MemberAlreadyExistsException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = -29302985790482809L;
	
	public MemberAlreadyExistsException() {
		super(Errors.MEMBERALREADYEXISTS);
	}
	
	public MemberAlreadyExistsException(String msg) {
		super(msg);
	}
}
