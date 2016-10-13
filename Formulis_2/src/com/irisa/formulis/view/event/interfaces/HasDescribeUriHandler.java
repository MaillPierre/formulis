package com.irisa.formulis.view.event.interfaces;

import com.irisa.formulis.view.event.DescribeUriEvent;
import com.irisa.formulis.view.form.FormEventCallback;

public interface HasDescribeUriHandler {

	void fireDescribeUriEvent(DescribeUriEvent event);

	void fireDescribeUriEvent(FormEventCallback cb);

	void addDescribeUriHandler(DescribeUriHandler hand);

}
