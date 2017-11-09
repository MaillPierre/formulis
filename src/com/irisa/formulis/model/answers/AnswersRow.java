package com.irisa.formulis.model.answers;

import java.util.Iterator;
import java.util.LinkedList;

import com.irisa.formulis.model.basic.BasicElement;

public class AnswersRow {

	private LinkedList<BasicElement> content;
	
	public AnswersRow() {
		content = new LinkedList<BasicElement>();
	}
	
	public Iterator<BasicElement> contentIterator() {
		return content.iterator();
	}
	
	public void addContent(BasicElement e) {
		content.add(e);
	}

	public void addAllContent(LinkedList<BasicElement> list) {
		content.addAll(list);
	}
	
	@Override
	public boolean equals(Object o) {
		if(o instanceof AnswersRow) {
//			return content.equals(((AnswersRow) o).content);
			return content.toString().equals(((AnswersRow) o).content.toString());
		}
		return super.equals(o);
	}
	
	@Override
	public String toString() {
		String result = "";
		
		Iterator<BasicElement> itContent = this.contentIterator();
		while(itContent.hasNext()) {
			result += itContent.next() + " ";
		}
		
		return result;
	}
}
