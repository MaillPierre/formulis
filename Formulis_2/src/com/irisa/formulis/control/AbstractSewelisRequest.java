package com.irisa.formulis.control;

import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.http.client.Response;
import com.google.gwt.http.client.URL;

public abstract class AbstractSewelisRequest {

	public AbstractSewelisRequest() {
	}
	public void send(final String query) {
		send(query, "");
	}

	public void send(final String query, final String errorMessage) {
		RequestBuilder builder = new RequestBuilder(RequestBuilder.GET, URL.encode(query));
		builder.setTimeoutMillis(ControlUtils.queryTimeout);

		try {
			builder.sendRequest(null, new RequestCallback() {

				@Override
				public void onResponseReceived(Request request, Response response) {
					onServerResponseReceived(request, response);
				}

				@Override
				public void onError(Request request, Throwable exception) {
					if(errorMessage != "") {
						ControlUtils.debugMessage(errorMessage);
					}
					ControlUtils.exceptionMessage(exception);
				}
				
			});
		} catch (RequestException e) {
			ControlUtils.exceptionMessage(e);
		}
			
	}
	
	public abstract void onServerResponseReceived(Request request, Response response);
	
}
