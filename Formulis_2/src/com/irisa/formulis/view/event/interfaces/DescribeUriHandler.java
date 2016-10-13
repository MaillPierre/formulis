package com.irisa.formulis.view.event.interfaces;

import com.irisa.formulis.view.event.ClassCreationEvent;
import com.irisa.formulis.view.event.DescribeUriEvent;
import com.irisa.formulis.view.form.FormEventCallback;

public interface DescribeUriHandler {

	void onDescribeUri(DescribeUriEvent event);

}
