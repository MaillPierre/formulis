package com.irisa.formulis.model.answers;

public class AnswersHeader {
	
	public enum ORDER {
		DEFAULT,
		ASCENDING,
		DESCENDING
	}
	
	public enum AGGREGATION {
		DEFAULT,
		COUNT,
		SUM,
		AVERAGE
	}

	private String name;
	private ORDER order;
	private String pattern;
	private AGGREGATION aggreg;
	private boolean hidden;
	
	public AnswersHeader(String n) {
		name = n;
		order = ORDER.DEFAULT;
		aggreg = AGGREGATION.DEFAULT;
		hidden = false;
		pattern = "";
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public AnswersHeader.ORDER getOrder() {
		return order;
	}
	
	public void setOrder(AnswersHeader.ORDER order) {
		this.order = order;
	}
	
	public String getPattern() {
		return pattern;
	}
	
	public void setPattern(String pattern) {
		this.pattern = pattern;
	}
	
	public AGGREGATION getAggreg() {
		return aggreg;
	}
	
	public void setAggreg(AGGREGATION aggreg) {
		this.aggreg = aggreg;
	}
	
	public boolean isHidden() {
		return hidden;
	}
	
	public void setHidden(boolean hidden) {
		this.hidden = hidden;
	}
	
	@Override
	public String toString() {
		return name;
	}
	
	public static ORDER orderFromString(String s) {
		ORDER result = ORDER.DEFAULT;
		
		switch(s) {
			case "ascending":
				result = ORDER.ASCENDING;
			break;
			case "descending":
				result = ORDER.DESCENDING;
			break;
		}
		
		return result;
	}
	
	public static AGGREGATION aggregationFromString(String s) {
		AGGREGATION result = AGGREGATION.DEFAULT;
		
		switch(s) {
			case "count":
				result = AGGREGATION.COUNT;
			break;
			case "sum":
				result = AGGREGATION.SUM;
			break;
			case "avg":
				result = AGGREGATION.AVERAGE;
			break;
		}
		
		return result;
	}
	
}
