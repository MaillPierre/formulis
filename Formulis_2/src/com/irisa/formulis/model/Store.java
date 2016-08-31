package com.irisa.formulis.model;

public class Store {
	
	private String name;
	private String role;
	private String label;
	
	public Store(String _name, String _label, String _role){
		name = _name;
		label = _label;
		role = _role;
	}
	
	public String getName() {
		return name;
	}
	
	public String getLabel() {
		return label;
	}
	
	public String getRole() {
		return role;
	}
	
	@Override
	public String toString() {
		return name + ", " + label + ", " + role; 
	}
	
}
