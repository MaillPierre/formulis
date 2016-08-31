package com.irisa.formulis.control;

public class Crypto {

	/**
	 * 
	 * @param passwd sel utilisé dans le cryptage
	 * @param serverAdress 
	 * @return
	 */
	public static native String getCryptedString(String passwd, String serverAdress) /*-{
		
		salt = serverAdress;
		bits = $wnd.sjcl.misc.pbkdf2(passwd, salt);
		hex = $wnd.sjcl.codec.hex.fromBits(bits);
		return hex; 
		
	}-*/;
}
