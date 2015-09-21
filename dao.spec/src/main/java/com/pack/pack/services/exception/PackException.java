package com.pack.pack.services.exception;

/**
 * 
 * @author Saurav
 *
 */
public class PackException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4280098680635340544L;
	
	private String errorCode;
	
	private String errorMsg;
	
	public PackException(String errorCode, String errorMsg, Throwable cause) {
		super(cause);
		this.errorCode = errorCode;
		this.errorMsg = errorMsg;
	}

	public String getErrorCode() {
		return errorCode;
	}

	public String getErrorMsg() {
		return errorMsg;
	}
}