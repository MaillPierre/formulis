package com.irisa.formulis.view.form.suggest;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;

public class SuggestionOracle {

	private LinkedList<Suggestion> suggestions = new LinkedList<Suggestion>();
	
	public SuggestionOracle() {
	}

	/**
     * 
     * @param query The current text being entered into the suggest box
     * @param limit The maximum number of results to return 
     * @return A collection of increment suggestions that match.
     */
	public Collection<Suggestion> matchingIncrement(String query, int limit) {
		LinkedList<Suggestion> matchingIncrement = new LinkedList<Suggestion>();

		if(! suggestions.isEmpty()) {
			// rendu des valeurs uniques
			LinkedList<Suggestion> tmpSuggestions = new LinkedList<Suggestion>(new HashSet<Suggestion>(suggestions) );
			// tri des suggestions par ratio left
			Collections.sort(tmpSuggestions);
             
			// Nombre d'éléments suggérés
			int count = 0;
			// only begin to search after the user has type two characters
			if ( query.length() >= 1 ) {
				String prefixToMatch = query.toLowerCase();
	 
				int i = 0;

				// Now we are at the start of the block of matching names. Add matching names till we
				// run out of names, stop finding matches, or have enough matches.
				while (count < limit && i < tmpSuggestions.size()) {
					if(tmpSuggestions.get(i).getElement() != null) {
						String normalizedTmpSuggString = tmpSuggestions.get(i).getElement().toLispql().toLowerCase();
						if(normalizedTmpSuggString.contains(prefixToMatch)) {
							matchingIncrement.add( tmpSuggestions.get(i) );
							count++;
						}
					}
					i++;
				}
				Collections.sort(matchingIncrement);
			}
			// On remplit les places restantes dans la liste de suggestion avec celles qui apparaissent dans l'ordre
			int index = 0;
			while(count < limit && index < tmpSuggestions.size()) {
				Suggestion sugg = tmpSuggestions.get(index);
				if(! matchingIncrement.contains(sugg)) {
					matchingIncrement.add(sugg);
					count++;
				}
				index++;
			}
		} 
		return matchingIncrement;
	}
      
	public void setSuggestions(Collection<Suggestion> c) {
		clear();
		suggestions.addAll(c);
	}

  	public void add(Suggestion customSuggestion) {
  		suggestions.add(customSuggestion);
  	}

    public void clear() {
    	suggestions.clear();
    }

    public boolean remove(Suggestion o) {
    	if ( suggestions.isEmpty()) {
        	return suggestions.remove(o);
        }
        return false;
    }
    
    public boolean contains(Suggestion s) {
    	return this.suggestions.contains(s);
    }

}
