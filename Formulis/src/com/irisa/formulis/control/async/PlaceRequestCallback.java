package com.irisa.formulis.control.async;

import com.google.gwt.http.client.RequestCallback;
import com.irisa.formulis.model.Place;

public interface PlaceRequestCallback extends RequestCallback {
	
	public Place getPlace();
	
}
