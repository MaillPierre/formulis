package com.irisa.formulis.model;

import java.util.Iterator;
import java.util.LinkedList;

import com.irisa.formulis.model.answers.AnswersHeader;
import com.irisa.formulis.model.answers.AnswersRow;
import com.irisa.formulis.model.basic.BasicElement;
import com.irisa.formulis.view.ViewUtils;

/**
 * Data representation of the Answers part of SEWELIS XML responses
 * @author pmaillot
 *
 */
public class Answers {

	private int count;
	private int start;
	private int end;
	private int size;
	
//	private BasicElement pivotElementSelected = null;

	private LinkedList<AnswersRow> contentRows;
	private LinkedList<AnswersHeader> headerColumns;
	
	public Answers() {
		count = 0;
		start = 0;
		end = 0;
		size = 0;
		
		contentRows = new LinkedList<AnswersRow>();
		headerColumns = new LinkedList<AnswersHeader>();
	}

//	public BasicElement getPivotElement() {
//		return pivotElementSelected;
//	}
//
//	public void setPivotElement(BasicElement pivotElement) {
//		this.pivotElementSelected = pivotElement;
//	}
	
	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}

	public int getStart() {
		return start;
	}

	public void setStart(int start) {
		this.start = start;
	}

	public int getEnd() {
		return end;
	}

	public void setEnd(int end) {
		this.end = end;
	}

	public int size() {
		return size;
	}

	public void setSize(int size) {
		this.size = size;
	}

	public Iterator<AnswersRow> contentRowsIterator() {
		return contentRows.iterator();
	}
	
	public Iterator<AnswersHeader> headerColumnsIterator() {
		return headerColumns.iterator();
	}
	
	public void addContentRow(AnswersRow l) {
		if(! this.contentRows.contains(l)) {
			contentRows.add(l);
		}
	}
	
	public void addHeaderColumn(AnswersHeader e) {
		headerColumns.add(e);
	}
	
	public LinkedList<AnswersRow> getContentRows() {
		return contentRows;
	}
	
	public LinkedList<AnswersHeader> getHeaderColumns() {
		return headerColumns;
	}
	
	@Override
	public String toString() {
		String result = "";
		
		Iterator<AnswersHeader> itHeader = this.headerColumnsIterator();
		while(itHeader.hasNext()) {
			String hString = itHeader.next().toString();
			result += hString + " ";
		}
		result +=  "\n";
		
		Iterator<AnswersRow> itRows = this.contentRowsIterator();
		while(itRows.hasNext()) {
			AnswersRow row = itRows.next();
			Iterator<BasicElement> itRow = row.contentIterator();
			while(itRow.hasNext()) {
				BasicElement elem = itRow.next();
				result += elem.toString() + " " + ViewUtils.getHTMLSpaceString();
			}
			result +=  "\n";
		}
		
		return result;
	}
}
