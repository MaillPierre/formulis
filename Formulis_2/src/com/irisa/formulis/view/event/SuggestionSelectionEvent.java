package com.irisa.formulis.view.event;

import com.irisa.formulis.view.form.suggest.CustomSuggestion;

public class SuggestionSelectionEvent {

	private CustomSuggestion suggestion;
	
	public SuggestionSelectionEvent(CustomSuggestion suggest) {
		this.suggestion = suggest;
	}

	public CustomSuggestion getSuggestion() {
		return suggestion;
	}

	public void setSuggestion(CustomSuggestion suggest) {
		this.suggestion = suggest;
	}
	
}
