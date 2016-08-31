package com.irisa.formulis.model.form;

public abstract class FormLine extends FormComponent{
	
	protected FormElement fixedElement;
	protected FormElement variableElement;
	protected int weight = -1;
	protected String info = "";

	protected boolean isNewLine = false;

	protected FormLine(FormComponent par, FormElement fixed, FormElement variable) {
		super(par);
		this.fixedElement = fixed;
		this.variableElement = variable;
	}
		
	protected FormLine(FormLine l){
		super(l.getParent());
	}

	public FormElement getFixedElement() {
		return fixedElement;
	}

	public void setFixedElement(FormElement fix) {
		this.fixedElement = fix;
	}

	public FormElement getVariableElement() {
		return variableElement;
	}

	public void setVariableElement(FormElement var) {
		this.variableElement = var;
	}
	
	/**
	 * Un index de -1 indique un placement auto
	 * @return la place voulue de la ligne dans liste des lignes
	 */
	public int getWeight() {
		return weight;
	}

	public void setWeight(int index) {
		this.weight = index;
	}
	
	public void incrementWeight() {
		this.weight++;
	}
	
	public void decrementWeight() {
		this.weight--;
	}
	
	public String getInfo() {
		return info;
	}

	public void setInfo(String info) {
		this.info = info;
	}
	
	/**
	 * Indique si la ligne est completement nouvelle (n'apparait pas dans la base)
	 * @return
	 */
	public boolean isNew() {
		return isNewLine;
	}
	
	public void setAsNew(boolean n) {
		this.isNewLine = n;
	}
	
	public abstract FormLine repeatLine();
	
	@Override
	public Form getParent() {
		if(super.getParent() != null && super.getParent() instanceof Form) {
			return (Form) super.getParent();
		}
		return null;
	}
	
	@Override
	public String toLispql() {
		return toLispql(false, false);
	}

	public abstract String toLispql(boolean selected, boolean isFinalRequest);
	
	@Override
	public boolean equals(Object o) {
		if(o!= null) {
			if(o instanceof FormLine) {
				FormLine oLine = ((FormLine)o);
//				if(oLine.getVariableElement() != null) {
//					return oLine.getFixedElement().equals(this.fixedElement) && oLine.getVariableElement().equals(this.variableElement);
//				} else {
					return oLine.getFixedElement().equals(this.fixedElement);
//				}
			}
		}
		return super.equals(o);
	}
	
	@Override
	public boolean isLine() {
		return true;
	}

	@Override
	public boolean isForm() {
		return false;
	}
	
}
