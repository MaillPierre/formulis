package com.irisa.formulis.model.form;

import java.util.Comparator;

/**
 * Sort from lines by type, fixed element and variables element
 * @author pmaillot
 *
 */
public class FormLineComparator implements Comparator<FormLine> {

	@Override
	public int compare(FormLine l0, FormLine l1) {
		if(l0.getClass() == l1.getClass()) {
			if(l0.getWeight() == l1.getWeight()) {
				if(l0.getFixedElement() == l1.getFixedElement()) {
					if(l0.getVariableElement() == l1.getVariableElement()) {
						return 1;
					} else if(l0.getVariableElement() == null){
						return -1;
					} else if(l1.getVariableElement() == null) {
						return 1;
					} else {
						return l0.getVariableElement().toString().compareTo(l1.getVariableElement().toString());
					}
				} else {
					return l0.getFixedElement().toString().compareTo(l1.getFixedElement().toString());
				}
			} else {
				return Integer.compare(l1.getWeight(), l0.getWeight());
			}
		} else if(l0 instanceof FormClassLine) {
			return -1;
		} else {
			return 1;
		}
	}

}
