package com.irisa.formulis.view.custom;

import com.github.gwtbootstrap.client.ui.FluidContainer;

/**
 * Experimental Custom class to replace gwt.HorisontalPanel by a bootstrap fluid container while retaining horizontal alignment.
 * 
 *  Style added:
 *  "fluidHorizontalContainer {
 *  	display:table;
 *  	width:100%;
 *  }"
 * @author pmaillot
 *
 */
public class FluidHorizontalContainer extends FluidContainer {

	public FluidHorizontalContainer() {
		super();
		this.addStyleName("fluidHorizontalContainer");
	}

}
