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
//		Description
//	}
	
	private String name;
	
	public Transformation(String trans) {
		name=trans;
	}
	
	public String getName() {
		return name;
	}
}
