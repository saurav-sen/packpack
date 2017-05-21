package com.pack.pack.model.web.dto;

/**
 * 
 * @author Saurav
 *
 */
public class PasswordResetDTO {

	private String userName;
	
	private String verifier;
	
	private String newPassword;
	
	public PasswordResetDTO() {
	}
	
	public PasswordResetDTO(String userName, String verifier, String newPassword) {
		this.userName = userName;
		this.verifier = verifier;
		this.newPassword = newPassword;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getVerifier() {
		return verifier;
	}

	public void setVerifier(String verifier) {
		this.verifier = verifier;
	}

	public String getNewPassword() {
		return newPassword;
	}

	public void setNewPassword(String newPassword) {
		this.newPassword = newPassword;
	}
}
