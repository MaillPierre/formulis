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
	
	public static native String obfuscate(String ch) /*-{
		return btoa(ch);
	}-*/;
	
	public static native String deobfuscate(String ch) /*-{
		return atob(ch);
	}-*/;
}
