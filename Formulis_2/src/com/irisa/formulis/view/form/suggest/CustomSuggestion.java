package com.irisa.formulis.view.form.suggest;

import com.irisa.formulis.model.DataUtils;
import com.irisa.formulis.model.form.FormElement;
import com.irisa.formulis.model.suggestions.Increment;
import com.irisa.formulis.view.ViewUtils;

public class CustomSuggestion implements Comparable<CustomSuggestion>{

	private FormElement elem;
	private String htmlRepresentation;
	private Integer support = 0;

	public CustomSuggestion(Increment inc) {
		elem = DataUtils.extractDisplayElementFromIncrement(inc);
		support = inc.getRatioLeft();
		htmlRepresentation = "<table> <tr> <td> " +ViewUtils.toSimpleHtml( elem).asString() + "</td> <td> (" + support + ") </td> </tr> </table>";
	}
	
	public FormElement getElement() {
		return this.elem;
	}
	
	public String getHtmlRepresentation() {
		return htmlRepresentation;
	}

	public void setHtmlRepresentation(String htmlRepresentation) {
		this.htmlRepresentation = htmlRepresentation;
	}
	
	@Override
	public String toString() {
		return "suggestion " + elem.toLispql() + " (" + support + ")";
	}
	
	@Override
	public boolean equals(Object o) {
		if(o instanceof CustomSuggestion) {
			return ((CustomSuggestion)o).hashCode() == this.hashCode();
		}
		return false;
	}

	@Override
	public int compareTo(CustomSuggestion o) {
		if(this.support == o.support) {
			return this.htmlRepresentation.compareTo(o.getHtmlRepresentation());
		}
		return o.support.compareTo(this.support);
	}
	
	@Override
	public int hashCode() {
		return this.htmlRepresentation.hashCode();
	}

}
