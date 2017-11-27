package com.irisa.formulis.model.basic;

import java.util.Iterator;
import java.util.LinkedList;

import com.irisa.formulis.model.DataUtils;


/**
 * Abstract class, basically a decorated list of elements
 * @author pmaillot
 *
 */
public abstract class BasicElementList extends BasicElementContener {
	
	private LinkedList<BasicElement> content;

	public BasicElementList(BasicElementContener par) {
		super(par);
		content = new LinkedList<BasicElement>();
	}
	
	public void addAllContent(LinkedList<BasicElement> list) {
		Iterator<BasicElement> itList = list.iterator();
		while(itList.hasNext()) {
			BasicElement listElem = itList.next();
			addContent(listElem);
		}
	}
	
	public void addContent(BasicElement e ) {
		if(e != null) {
			content.addAll(DataUtils.getFirstDisplayableElements(e));
		}
	}
	
	public LinkedList<BasicElement> getContent() {
		return content;
	}
	
	public Iterator<BasicElement> getContentIterator() {
		return content.iterator();
	}
	
	@Override
	public String toString() {
		String result = "[ ";
		Iterator<BasicElement> itCont = content.iterator();
		while(itCont.hasNext()) {
			BasicElement elemDisList = itCont.next();
			if(elemDisList != null) {
				if(elemDisList.toString() != "") {
					result += elemDisList.toString();
					if(itCont.hasNext()) {
						result += " , ";
					}
				}
			}
		}
		result += " ]";
		return result;
	}
	
	@Override
	public int hashCode() {
		return this.toString().hashCode();
	}

}
