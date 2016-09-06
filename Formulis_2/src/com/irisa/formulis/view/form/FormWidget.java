package com.irisa.formulis.view.form;

import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;

import com.github.gwtbootstrap.client.ui.Button;
import com.github.gwtbootstrap.client.ui.CheckBox;
import com.github.gwtbootstrap.client.ui.Column;
import com.github.gwtbootstrap.client.ui.FluidRow;
import com.github.gwtbootstrap.client.ui.constants.IconType;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.irisa.formulis.control.ControlUtils;
import com.irisa.formulis.control.Controller;
import com.irisa.formulis.control.profile.ProfileElement;
import com.irisa.formulis.control.profile.ProfileForm;
import com.irisa.formulis.model.form.Form;
import com.irisa.formulis.model.form.FormClassLine;
import com.irisa.formulis.model.form.FormLine;
import com.irisa.formulis.model.form.FormLineComparator;
import com.irisa.formulis.model.form.FormRelationLine;
import com.irisa.formulis.view.AbstractFormulisWidget;
import com.irisa.formulis.view.ViewUtils;
import com.irisa.formulis.view.create.fixed.RelationCreateWidget;
import com.irisa.formulis.view.form.FormLineWidget.LINE_STATE;

/**
 * Widget principal pour un formulaire
 * @author pmaillot
 *
 */
public class FormWidget extends AbstractFormElementWidget {

	private FluidRow element = new FluidRow();
	private Column linesCol = new Column(11);
	private LinkedList<FormLineWidget> linesWidgets = new LinkedList<FormLineWidget>();

	private Column controlCol = new Column(1);
	private CheckBox profileCheckbox = new CheckBox();
	
	private FluidRow newRelationRow = new FluidRow();
	private Button newRelationButton = new Button("New line");
	private RelationCreateWidget relationCreationWid = new RelationCreateWidget(this);
	
	private FluidRow contentRow = new FluidRow();
	private Column contentCol = new Column(11,contentRow, newRelationRow);
	
	private Column finishCol = new Column(1);
	private Button finishButton = new Button("", IconType.OK);
	
	public enum FORM_CALLBACK_MODE {
		/**
		 * Charger le contenu d'un formulaire vide
		 */
		LOAD,
		/**
		 * Ajouter du contenu à un formulaire prérempli
		 */
		APPEND,
		/**
		 * Ajouter une ligne nouvellement créée à un formulaire
		 */
		ADD
	}
	
	public FormWidget(Form da, AbstractFormulisWidget fParent) {
		super(da, fParent);
		initWidget(element);
		
		element.add(contentCol);
		element.add(finishCol);
		element.add(controlCol);
		element.addStyleName("weblis-form-frame");
		contentRow.add(linesCol);
		contentRow.add(finishCol);
		finishCol.add(finishButton);
		newRelationButton.addStyleName("weblis-max-width");
		newRelationRow.add(newRelationButton);
		relationCreationWid.addRelationCreationHandler(this);
		controlCol.add(profileCheckbox);
		
		newRelationButton.addClickHandler(this);
		
		finishButton.addClickHandler(this);
		
		profileCheckbox.addValueChangeHandler(new ValueChangeHandler<Boolean>() {
			@Override
			public void onValueChange(ValueChangeEvent<Boolean> event) {
				setSelectedForProfile(event.getValue());
			}
		});
		profileCheckbox.setText("Add to profile");
		setProfileMode(false);
		
		reload();
	}
	
	public FormCallback getCallback() {
		return new FormCallback(this);
	}
	
	public FormCallback getCallback(FORM_CALLBACK_MODE mode) {
		return new FormCallback(this, mode);
	}
	
	public void putRelationCreationButton(){
		newRelationRow.clear();
		newRelationRow.add(newRelationButton);
	}
	
	public void putRelationCreationWidget(){
		newRelationRow.clear();
		newRelationRow.add(relationCreationWid);
	}
	
	public void clear() {
		linesCol.clear();
		newRelationButton.setVisible(false);
	}
	
	public void reload() {
		if(getData() != null && ! getData().isEmpty()/* && ! getData().isFinished()*/) {
			clear();

//			if(! formData.isAnonymous() ) {
//				FormClassLineWidget typeLineWid = new FormClassLineWidget( formData.getTypeLines().peekFirst(), this);
//				typeLineWid.setLineState(LINE_STATE.FINISHED);
//				typeLineWid.showLabelBox();
//				addLine(typeLineWid);
//			}
						
			Iterator<FormLineWidget> itFo = formLinesToWidget().iterator();
			while(itFo.hasNext()) {
				FormLineWidget line = itFo.next();
				addLine(line);
			}
			newRelationButton.setVisible(true);
		} 

		// Si le formulaire est le root
		if(this.getData().isEmpty() && this.getData().isRoot()) {
			newRelationButton.setVisible(false);
		}
	}
	
	protected LinkedList<FormLineWidget> formLinesToWidget() {
		LinkedList<FormLineWidget> result = new LinkedList<FormLineWidget>();
		
		Iterator<FormClassLine> itClassL = getData().typeLinesIterator();
		while(itClassL.hasNext()) {
			FormLine line = itClassL.next();
			FormClassLineWidget nClassLine = new FormClassLineWidget((FormClassLine) line, this);
			nClassLine.setProfileMode(this.profileMode);
			result.add(nClassLine);
			if(this.getData().isTyped()) {
				nClassLine.setLineState(LINE_STATE.FINISHED);
				nClassLine.showLabelBox();
			}
		}
		
		Iterator<FormRelationLine> itRelL = getData().relationLinesIterator();
		while(itRelL.hasNext()) {
			FormLine line = itRelL.next();
			FormRelationLineWidget nRelLine = new FormRelationLineWidget((FormRelationLine) line, this);
			nRelLine.setProfileMode(this.profileMode);
			result.add(nRelLine);
		}
		
		return result;
	}
	
	@Override
	public Form getData() {
		return (Form) super.getData();
	}
	
	@Override
	public FormLineWidget getParentWidget() {
		if(super.getParentWidget() != null) {
			return (FormLineWidget) super.getParentWidget();
		}
		return null;
	}
	
	public void addLine(FormLineWidget line) {
		linesCol.add(line);
		linesWidgets.addLast(line);
		ViewUtils.connectFormEventChain(line, this);
	}

	public void setData(Form form) {
		data = form;
		reload();
	}

	@Override
	public void onClick(ClickEvent event) {
		if(event.getSource() == newRelationButton) {
			ControlUtils.debugMessage("Creation button click");
			putRelationCreationWidget();
		} else if(event.getSource() == finishButton) {
			fireFinishFormEvent();
		}
//		fireClickWidgetEvent(new ClickWidgetEvent(this));s
	}
	
	@Override
	public String toString() {
		return "Form widget " + this.getData().toString();		
	}

	@Override
	public ProfileElement toProfileElement() {
		return toProfileForm();
	}
	
	public ProfileForm toProfileForm() {
		if(this.isSelectedForProfile()) {
			ProfileForm result = new ProfileForm();
			
//			if(this.getData().getTypeLine() != null) {
//				result.setTypeLine(this.getData().getTypeLine().toProfileClassLine());
//			}
//			
//			Iterator<FormLineWidget> itLine = this.linesWidgets.iterator();
//			while(itLine.hasNext()) {
//				FormLineWidget line = itLine.next();
//				if(line.toProfileElement() != null) {
//					result.addLine(line.toProfileLine());
//				}
//			}
			
			return result;
		}
		return null;
	}

	@Override
	public boolean isSelectedForProfile() {
		return this.profileCheckbox.getValue();
	}

	@Override
	public void setSelectedForProfile(boolean val) {
		this.profileCheckbox.setValue(val);
		
		if(! val) {
			Iterator<FormLineWidget> itLine = this.linesWidgets.iterator();
			while(itLine.hasNext()) {
				FormLineWidget line = itLine.next();
				line.setSelectedForProfile(val);
			}
		}
	}
	
	@Override
	public void setProfileMode(boolean value) {
		super.setProfileMode(value);
		
		this.profileCheckbox.setVisible(value);
		Iterator<FormLineWidget> itLine = this.linesWidgets.iterator();
		while(itLine.hasNext()) {
			FormLineWidget line = itLine.next();
			line.setProfileMode(value);
		}
		
		if(value) {
			element.add(controlCol);
			contentCol.setSize(11);
		} else {
			element.remove(controlCol);
			contentCol.setSize(12);
		}
	}
	
	public class FormCallback implements FormEventCallback {

		private FormWidget source;
		private FORM_CALLBACK_MODE callbackMode = FORM_CALLBACK_MODE.LOAD ;
		
		public FormCallback(FormWidget src) {
			source = src;
		}
		
		public FormCallback(FormWidget src, FORM_CALLBACK_MODE mode) {
			source = src;
			callbackMode = mode;
		}
		
		@Override
		public void call(Controller control) {
			if(callbackMode == FORM_CALLBACK_MODE.LOAD) {
				control.loadFormContent(source);
			} else if (callbackMode == FORM_CALLBACK_MODE.APPEND) {
				control.appendFormContent(source);
			}
		}
		
	}
	
}
