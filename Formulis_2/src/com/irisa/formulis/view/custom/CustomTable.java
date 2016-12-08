package com.irisa.formulis.view.custom;

import com.google.gwt.dom.client.Element;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Widget;

/**
 * Table avec un header.
 * Adapté de http://programmatica.blogspot.fr/2008/02/gwt-flextable-with-frozen-header.html
 * @author pmaillot
 *
 */
public class CustomTable extends FlexTable {
	private Element head;  
	private Element headerTr;  

	public CustomTable() {  
		super();  
		head = DOM.createTHead();  
		headerTr = DOM.createTR();  
		DOM.insertChild(this.getElement(), head, 0);  
		DOM.insertChild(head, headerTr, 0);

		this.addStyleName("table");

	} 

	public void setHeader(int column, String text){  
		prepareHeader(column);  
		if (text != null) {  
			DOM.getChild(headerTr, column).setInnerText(text);  
		}  
	}  

	private void prepareHeader(int column) {  
		if (column < 0) {  
			throw new IndexOutOfBoundsException(  
					"Cannot create a column with a negative index: " + column);  
		}  
		int cellCount = DOM.getChildCount(headerTr);  
		int required = column + 1 - cellCount;  
		if (required > 0) {  
			addCells(head, 0, required);  
		}  
	}  


	public void setHeaderWidget(int column, Widget widget) {  
		prepareHeader(column);  
		if (widget != null) {  
			widget.removeFromParent();  
			// Physical attach.  
			DOM.appendChild(DOM.getChild(headerTr, column), widget.getElement());  

			adopt(widget);  
		}  
	}
	
	public int getHeaderCellCount() {
		return DOM.getChildCount(headerTr);
	}
	
	@Override
	public void clear(boolean clearInnerHtml) {
		super.clear(clearInnerHtml);
		for(int col = 0; col < getHeaderCellCount(); col++) {
			setHeaderWidget(col, new HTML(""));
		}
	}

	private native void addCells(Element table, int row, int num)/*-{ 
	     var rowElem = table.rows[row]; 
	     for(var i = 0; i < num; i++){ 
	       var cell = $doc.createElement("td"); 
	       rowElem.appendChild(cell);   
	     } 
	  }-*/;  
}
