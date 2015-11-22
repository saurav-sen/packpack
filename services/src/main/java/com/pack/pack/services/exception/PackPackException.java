package com.pack.pack.services.exception;

/**
 * 
 * @author Saurav
 *
 */
public class PackPackException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8128382796037653377L;

	private String errorCode;
	
	public PackPackException(String errorCode, Throwable cause) {
		this(errorCode, cause.getMessage(), cause);
	}
	
	public PackPackException(String errorCode, String errorMsg) {
		this(errorCode, errorMsg, null);
	}
	
	public PackPackException(String errorCode, String errorMsg, Throwable cause) {
		super(errorMsg, cause);
		this.errorCode = errorCode;
	}

	public String getErrorCode() {
		return errorCode;
	}

	public void setErrorCode(String errorCode) {
		this.errorCode = errorCode;
	}
}