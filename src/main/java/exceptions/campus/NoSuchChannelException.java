package exceptions.campus;

import utils.exceptions.campus.Errors;

public class NoSuchChannelException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2051941328549819441L;
	
	public NoSuchChannelException(int channelId) {
		super(Errors.NOSUCHCHANNEL + channelId);
	}
}
