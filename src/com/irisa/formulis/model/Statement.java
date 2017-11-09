package com.irisa.formulis.model;

import java.util.HashMap;

import com.irisa.formulis.model.basic.BasicElement;
import com.irisa.formulis.model.basic.Focus;

public class Statement {
	
	private String focusedDisplay;
	private HashMap<String, Focus> focusMap;
	private BasicElement content;
	private String statString;
	
	public Statement() {
		focusedDisplay = null;
		focusMap = new HashMap<String, Focus>();
		content = null;
		statString = "";
	}
	
	public void setFocusedDiplay(String f) {
		focusedDisplay = f;
	}
	
	public void setContent(BasicElement e) {
		content = e;
	}
	
	public void setFocusMap(HashMap<String, Focus> m) {
		focusMap = m;
	}
	
	public void addFocus(Focus f) {
		focusMap.put(f.getId(), f);
	}
	
	public String getFocusedDisplay() {
		return focusedDisplay;
	}
	
	public Focus getFocus(String id) {
		return focusMap.get(id);
	}
	
	public BasicElement getContent() {
		return content;
	}
	
	public void setString(String s) {
		this.statString = s;
	}
	
	public String getString() {
		return this.statString;
	}
	
	@Override
	public String toString() {
		return "Statement (focus: " + focusedDisplay + ")\" "+ this.statString + " \" : " + content.toString();
	}
}
