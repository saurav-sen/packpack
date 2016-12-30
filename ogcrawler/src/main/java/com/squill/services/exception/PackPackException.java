package com.squill.services.exception;

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
	
	public static final String ERR_CODE = "errorCode"; //$NON-NLS-1$
	public static final String ERR_MSG = "errorMsg"; //$NON-NLS-1$

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
	
	public boolean isUserError() {
		if(errorCode == null || errorCode.trim().isEmpty())
			return false;
		String[] split = errorCode.split("_");
		if(split.length == 3) {
			try {
				int codeVal = Integer.parseInt(split[2].trim());
				if(codeVal >= 70)
					return true;
			} catch (NumberFormatException e) {
			}
		}
		return false;
	}
}