package com.irisa.formulis.model;

import java.util.LinkedList;

import com.irisa.formulis.model.suggestions.Increment;

public class Place {
	
	private String id;
	private int relaxationRank;
	private boolean hasMore;
	private boolean hasLess;
	private Statement statement;
	private Suggestions suggest;
	private Answers answers;
	private LinkedList<Increment> currentCompletions;
	
	public Place() {
		statement = null;
		suggest = null;
		answers = null;
		currentCompletions = new LinkedList<Increment>();
		
		relaxationRank = 0;
		hasMore = false;
		hasLess = false;
	}

	public int getRelaxationRank() {
		return relaxationRank;
	}

	public void setRelaxationRank(int relaxationRank) {
		this.relaxationRank = relaxationRank;
	}

	public boolean hasMore() {
		return hasMore;
	}

	public void setHasMore(boolean hasMore) {
		this.hasMore = hasMore;
	}

	public boolean hasLess() {
		return hasLess;
	}

	public void setHasLess(boolean hasLess) {
		this.hasLess = hasLess;
	}

	public Statement getStatement() {
		return statement;
	}
	
	public void setStatement(Statement stat) {
		statement = stat;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public Suggestions getSuggestions() {
		return suggest;
	}

	public void setSuggestions(Suggestions suggest) {
		this.suggest = suggest;
	}

	public Answers getAnswers() {
		return answers;
	}

	public void setAnswers(Answers answers) {
		this.answers = answers;
	}

	public LinkedList<Increment> getCurrentCompletions() {
		return currentCompletions;
	}

	public void clearCurrentCompletions() {
		currentCompletions.clear();
	}

	public void setCurrentCompletions(LinkedList<Increment> currentCompletions) {
		this.currentCompletions = currentCompletions;
	}
}
