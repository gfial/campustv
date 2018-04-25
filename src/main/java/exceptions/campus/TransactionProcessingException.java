package exceptions.campus;

import utils.exceptions.campus.Errors;

public class TransactionProcessingException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1790936628639775779L;

	public TransactionProcessingException() {
		super(Errors.TRANSACTIONFAILED);
	}
	
	public TransactionProcessingException(String msg) {
		super(msg);
	}
}
