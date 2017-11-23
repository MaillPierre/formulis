package com.irisa.formulis.view.create;

import com.irisa.formulis.control.ControlUtils.DATATYPE_URIS;
import com.irisa.formulis.view.AbstractDataWidget;
import com.irisa.formulis.view.create.variable.DateCreateWidget;
import com.irisa.formulis.view.create.variable.DateTimeCreateWidget;
import com.irisa.formulis.view.create.variable.NumericCreateWidget;
import com.irisa.formulis.view.create.variable.TextCreateWidget;

public class CreationUtils {

	/**
	 * Return the Creation widget associated with a particular datatype
	 * @param uri
	 * @return
	 */
	static public AbstractDataWidget getCorrespondingCreateWidget(DATATYPE_URIS uri) {
		AbstractDataWidget result = null;
		switch(uri) {
		case xsdString:
			result = new TextCreateWidget(null);
			break;
		case xsdInteger:
			result = new NumericCreateWidget(null);
			break;
		case xsdBoolean:
			break;
		case xsdDate:
		case xsdYearMonth:
		case xsdYear:
		case xsdMonth:
		case xsdDay:
		case xsdMonthDay:
			result = new DateCreateWidget(null);
			break;
		case xsdDatetime:
			result = new DateTimeCreateWidget(null);
			break;
		case xsdTime:
			break;
		default:
			break;
		}
		return result;
	}

}
