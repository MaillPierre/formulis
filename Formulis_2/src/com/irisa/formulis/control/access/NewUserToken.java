package com.irisa.formulis.control.access;

public class NewUserToken {

	final private String userName;
	final private String userPassword;
	final private String userEmail;
	
	public NewUserToken(String name, String pssword, String email) {
		userName = name;
		userPassword = pssword;
		userEmail = email;
	}

	public String getUserName() {
		return userName;
	}

	public String getPassword() {
		return userPassword;
	}

	public String getEmail() {
		return userEmail;
	}
	
}
