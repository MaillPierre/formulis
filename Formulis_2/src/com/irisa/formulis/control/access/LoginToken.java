package com.irisa.formulis.control.access;

/**
 * Container object for login/password data
 * @author pmaillot
 *
 */
public class LoginToken {
	
	final private String login;
	final private String password;
	
	public LoginToken(String l, String p) {
		login = l;
		password = p;
	}

	public String getLogin() {
		return login;
	}

	public String getPassword() {
		return password;
	}

}
