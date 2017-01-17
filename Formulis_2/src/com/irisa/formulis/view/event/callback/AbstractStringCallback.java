package com.irisa.formulis.view.event.callback;

public abstract class AbstractStringCallback implements StringCallback {

	@Override
	public void call(Object object) {
		if(object instanceof String) {
			call((String)object);
		} else {
			throw new IllegalArgumentException("Expecting String argument for this callback");
		}
		
	}

	@Override
	public abstract void call(String description);

}
