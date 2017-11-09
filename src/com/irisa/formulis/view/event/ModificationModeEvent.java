package com.irisa.formulis.view.event;

import com.google.gwt.user.client.ui.Widget;

public class ModificationModeEvent extends AbstractFormEvent {

	private boolean _modification = false;
	
	public ModificationModeEvent(Widget src, boolean modification) {
		super(src);
		_modification = modification;
	}
	
	public boolean getModificationFlag() {
		return _modification;
	}

}
