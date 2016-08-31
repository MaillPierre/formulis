package com.irisa.formulis.model.basic;

/**
 * Class abstraite pour la gestion de l'indentation dans les conteners
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
	public boolean isFinished() {
		return true;
	}
	
	@Override
	public String toLispql() {
		return this.toLispql(false);
	}

}
