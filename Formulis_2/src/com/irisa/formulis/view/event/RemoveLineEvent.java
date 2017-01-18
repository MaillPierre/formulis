package com.irisa.formulis.view.event;

import com.irisa.formulis.view.form.AbstractFormLineWidget;

public class RemoveLineEvent extends AbstractFormEvent {
	
	public RemoveLineEvent(AbstractFormLineWidget src) {
		super(src);
	}
	
	@Override
	public AbstractFormLineWidget getSource() {
		return (AbstractFormLineWidget) super.getSource();
	}

}
