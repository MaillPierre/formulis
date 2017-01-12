package com.irisa.formulis.view.event;

import com.irisa.formulis.view.form.suggest.Suggestion;

public class SuggestionSelectionEvent {

	private Suggestion suggestion;
	
	public SuggestionSelectionEvent(Suggestion suggest) {
		this.suggestion = suggest;
	}

	public Suggestion getSuggestion() {
		return suggestion;
	}

	public void setSuggestion(Suggestion suggest) {
		this.suggestion = suggest;
	}
	
}
