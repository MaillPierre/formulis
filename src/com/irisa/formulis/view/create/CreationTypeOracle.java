package com.irisa.formulis.view.create;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;

import com.irisa.formulis.control.ControlUtils;
import com.irisa.formulis.model.Couple;
import com.irisa.formulis.model.basic.BasicLeafElement;
import com.irisa.formulis.model.basic.Plain;
import com.irisa.formulis.model.basic.Typed;

/**
 * View utility class which try to determine the kind of creation widget needed according to the class suggested
 * @author pmaillot
 *
 */
public class CreationTypeOracle {
	
	private LinkedList<BasicLeafElement> types;
	private HashMap<String, String> uriTypeMap = new HashMap<String, String>();
	public HashMap<String, String> typeItemMap = new HashMap<String, String>();
	
	private String oracle = null;
	protected String startValue = "";

	public CreationTypeOracle(LinkedList<BasicLeafElement> linkedList, String startVal) {
		types = linkedList;
		this.startValue = startVal;

		uriTypeMap.put("http://www.w3.org/2001/XMLSchema#string", "text");
		uriTypeMap.put("http://www.w3.org/2001/XMLSchema#integer", "number");
		uriTypeMap.put("http://www.w3.org/2001/XMLSchema#datetime", "datetime");
		uriTypeMap.put("http://www.w3.org/2001/XMLSchema#time", "time");
		uriTypeMap.put("http://www.w3.org/2001/XMLSchema#date", "date");
		uriTypeMap.put("http://www.w3.org/2001/XMLSchema#gYear", "date-year");
		uriTypeMap.put("http://www.w3.org/2001/XMLSchema#gMonth", "date-month");
		uriTypeMap.put("http://www.w3.org/2001/XMLSchema#gDay", "date-day");
		uriTypeMap.put("http://www.w3.org/2001/XMLSchema#gMonthDay", "date-monthday");
		uriTypeMap.put("http://www.w3.org/2001/XMLSchema#gYearMonth", "date-yearmonth");
		uriTypeMap.put("", "text");
		
		typeItemMap.put("date", "Date");
		typeItemMap.put("time", "Time");
		typeItemMap.put("datetime", "Date and time");
		typeItemMap.put("number", "Numeric value");
		typeItemMap.put("text", "Text");
		typeItemMap.put("entity", "Entity");
		
		determineMostLikelyType();
	}

	private void determineMostLikelyType() {
		ControlUtils.debugMessage("CreationTypeOracle determineMostLikelyType");
		HashMap<String, Integer> typeScoreMap = new HashMap<String, Integer>();
		// init de la map des scores
		Iterator<String> itKeys = uriTypeMap.keySet().iterator();
		while(itKeys.hasNext()) {
			String key = itKeys.next();
			
			typeScoreMap.put(key, 0);
		}
		
		// Ajout des scores des types
		Iterator<BasicLeafElement> itLines = this.types.iterator();
		while(itLines.hasNext()) {
			BasicLeafElement line = itLines.next();
			if(line instanceof Typed) {
				Typed typedLine = (Typed) line;
				typeScoreMap.put(typedLine.getUri(), typeScoreMap.get(typedLine.getUri()) + 1);
			} else if(line instanceof Plain) {
				typeScoreMap.put("", typeScoreMap.get("") + 1);
			}
		}
		
		LinkedList<Couple<String, Integer>> typeScoreSet = new LinkedList<Couple<String, Integer>>();
		Iterator<String> itType = typeScoreMap.keySet().iterator();
		while(itType.hasNext()) {
			String type = itType.next();
			typeScoreSet.add(Couple.of(type, typeScoreMap.get(type)));
		}
		Collections.sort(typeScoreSet, new Comparator<Couple<String, Integer>>() {
			@Override
			public int compare(Couple<String, Integer> arg0, Couple<String, Integer> arg1) {
				return arg0.getSecond().compareTo(arg1.getSecond());
			}
		});
		
		oracle = uriTypeMap.get(typeScoreSet.getLast().getFirst());
		ControlUtils.debugMessage("CreationTypeOracle determineMostLikelyType "+ oracle +" END");
	}

	public String getMostLikelyLiteralType() {
		return oracle;
	}

	public String getSimpleMostLikelyLiteralType() {
		if(oracle.equals("date-year") 
				|| oracle.equals("date-month") 
				|| oracle.equals("date-day") 
				|| oracle.equals("date-monthday") 
				|| oracle.equals("date-yearmonth")){
			return "date";
		}
			
		return oracle;
	}
	
	public String getStartValue() {
		return startValue;
	}
	
}
