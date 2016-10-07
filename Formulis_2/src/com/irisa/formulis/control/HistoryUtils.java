package com.irisa.formulis.control;

import java.util.HashMap;

import com.google.gwt.storage.client.Storage;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.Window;
import com.irisa.formulis.model.exception.InvalidHistoryState;

public class HistoryUtils {
	
	private static Storage bank = Storage.getLocalStorageIfSupported();
	private static int stateId = 0;
	private static HashMap<Integer, String> stateMap = new HashMap<Integer, String>();
	
	public static void addHistoryToken(String token) {
		if(Storage.isLocalStorageSupported()) {
			int state = newStateId();
			stateMap.put(state, token);
			bank.setItem(Integer.toString(state), token);
			History.newItem(Integer.toString(state), false);
		} else {
			History.newItem(Crypto.obfuscate(token), false);
		}
	}
	
	public static String getProfileFromHistoryToken(String token) throws InvalidHistoryState {
		// token is a state number
		if(Storage.isLocalStorageSupported()) {
			int state = Integer.parseInt(token);
			if(stateMap.containsKey(state)){
//				return stateMap.get(state);
				return bank.getItem(Integer.toString(state));
			} else {
				throw new InvalidHistoryState("State " + state + " unknown in history bank");
			}

		// token is a profile file
		} else {
			return Crypto.deobfuscate(token);
		}
	}
	
	public static String getPermalinkFromToken(String token) throws InvalidHistoryState {
		return Controller.getServerAdress()+ "?state=" + Crypto.obfuscate(getProfileFromHistoryToken(token));
	}
	
	public static String getPermalink(String profile) throws InvalidHistoryState {
		ControlUtils.debugMessage("HistoryUtils path "+ Window.Location.getPath());
		ControlUtils.debugMessage("HistoryUtils host "+ Window.Location.getHost());
		ControlUtils.debugMessage("HistoryUtils href "+ Window.Location.getHref());
		ControlUtils.debugMessage("HistoryUtils parameter(state) "+ Window.Location.getParameter("state"));
		return "http://"+ Window.Location.getHost()+ "?state=" + Crypto.obfuscate(profile);
	}
	
	public static int newStateId() {
		stateId++;
		return stateId;
	}

}
