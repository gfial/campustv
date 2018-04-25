package exceptions.campus;

import utils.exceptions.campus.Errors;

public class NoTagsException extends Exception {

	private static final long serialVersionUID = 2049510583832194940L;

	
	public NoTagsException() {
		super(Errors.NOTAGS);
	}
	
	public NoTagsException(String error) {
		super(error);
	}
}
