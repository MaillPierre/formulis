package com.irisa.formulis.control;

import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.Response;
import com.google.gwt.http.client.URL;

/**
 * Utility class to encapsulate all queries sent to a HTTP endpoint.
 * Centralize exception management during asynchronous management and server interaction (timeout...) settings
 * @author pmaillot
 *
 */
public abstract class AbstractSewelisRequest {

	private String sentQuery;
	
	public AbstractSewelisRequest(String query) {
		sentQuery = query;
	}
	public void send() {
		send("");
	}

	public void send(final String errorMessage) {
		RequestBuilder builder = new RequestBuilder(RequestBuilder.GET, URL.encode(sentQuery));
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
		} catch (Exception e) {
			ControlUtils.exceptionMessage(e);
		}
			
	}
	
	public abstract void onServerResponseReceived(Request request, Response response);
	
}
