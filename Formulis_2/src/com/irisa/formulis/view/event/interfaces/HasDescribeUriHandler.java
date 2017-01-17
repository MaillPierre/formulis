package com.irisa.formulis.view.event.interfaces;

import com.irisa.formulis.model.basic.URI;
import com.irisa.formulis.view.basic.URIWidget;
import com.irisa.formulis.view.event.DescribeUriEvent;
import com.irisa.formulis.view.event.callback.ActionCallback;

public interface HasDescribeUriHandler {

	void fireDescribeUriEvent(DescribeUriEvent event);
	void fireDescribeUriEvent(ActionCallback cb, URI uri);
	void addDescribeUriHandler(DescribeUriHandler hand);

}
