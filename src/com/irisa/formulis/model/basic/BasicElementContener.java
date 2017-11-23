package com.irisa.formulis.model.basic;

/**
 * Abstract class for the management of indentation in conteners
 * @author pmaillot
 *
 */
public abstract class BasicElementContener implements BasicElement {
	
	private BasicElementContener parent;
	
	public BasicElementContener(BasicElementContener par) {
		parent = par;
	}
	
	public BasicElementContener getParent() {
		return parent;
	}
	
	@Override
	public boolean isFinishable() {
		return true;
	}
	
	@Override
	public String toLispql() {
		return this.toLispql(false);
	}

}
