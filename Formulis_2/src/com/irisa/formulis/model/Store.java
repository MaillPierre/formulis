package com.irisa.formulis.model;

import java.util.HashMap;
import java.util.Iterator;

public class Store {
	
	private String name;
	private String role;
	private String label;
	private HashMap<String, String> prefixNsMap = new HashMap<String, String>();
	private HashMap<String, String> nsPrefixMap = new HashMap<String, String>();
	
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
	
	public void addNamespace(String prefix, String ns) {
		prefixNsMap.put(prefix, ns);
		nsPrefixMap.put(ns, prefix);
	}
	
	public void removeNamespace(String ns) {
		String prefix = this.nsPrefixMap.get(ns);
		if(prefix != null && this.prefixNsMap.get(prefix) != null) {
			this.nsPrefixMap.remove(ns);
			this.prefixNsMap.remove(prefix);
		}
	}
	
	public void removePrefix(String prefix) {
		String ns = this.prefixNsMap.get(prefix);
		if(ns != null && this.nsPrefixMap.get(ns) != null) {
			this.nsPrefixMap.remove(ns);
			this.prefixNsMap.remove(prefix);
		}
	}
	
	public Iterator<String> getNamespaceIterator() {
		return this.nsPrefixMap.keySet().iterator();
	}
	
	public Iterator<String> getPrefixIterator() {
		return this.prefixNsMap.keySet().iterator();
	}
	
	public String getNamespacePrefix(String ns) {
		return this.nsPrefixMap.get(ns);
	}
	
	public String getPrefixNamespace(String prefix) {
		return this.prefixNsMap.get(prefix);
	}
	
	@Override
	public String toString() {
		return name + ", " + label + ", " + role; 
	}
	
}
