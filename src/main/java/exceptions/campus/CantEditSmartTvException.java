package exceptions.campus;

import utils.exceptions.campus.Errors;

public class CantEditSmartTvException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7176234982954652401L;

	public CantEditSmartTvException() {
		super(Errors.CANTEDITSMARTTV);
	}
	
	public CantEditSmartTvException(String msg) {
		super(msg);
	}
}
