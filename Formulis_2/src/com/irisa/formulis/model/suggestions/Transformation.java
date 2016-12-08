package com.irisa.formulis.model.suggestions;

public class Transformation {

//	public enum TRANSFORMATIONS {
//		ToggleNo,
//		ToggleOnly,
//		ToggleEvery,
//		InsertForEach,
//		InsertIsThere,
//		InsertAnd,
//		InsertOr,
//		InsertAndNot,
//		ToggleNot,
//		ToggleMaybe,
//		Select,
//		Delete
//	}
	
	private String name;
	
	public Transformation(String trans) {
		name=trans;
	}
	
	public String getName() {
		return name;
	}
	
	public String toDisplayString() {
		switch(name) {
		case "ToggleNo":
			return "no _";
		case "ToggleOnly":
			return "only _";
		case "ToggleEvery":
			return "every _";
		case "InsertForEach":
			return "for each _";
		case "InsertIsThere":
			return "_ is there";
		case "InsertAnd":
			return "_ and _";
		case "InsertOr":
			return "_ or _";
		case "InsertAndNot":
			return "_ and not _";
		case "ToggleNot":
			return "not _";
		case "ToggleMaybe":
			return "maybe _";
		case "ToggleOpt":
			return "optionnally _";
		case "ToggleTrans":
			return "transitively _";
		case "ToggleSym":
			return "symmetrically _";
		case "Select":
			return "SELECT ";
		case "Delete":
			return "DELETE ";
		case "Description":
			return "Describe _";
		default:
			return "ERROR: unknown transformation "+ name;
		}
	}
}
