package com.irisa.formulis.view.form;

import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;

import com.github.gwtbootstrap.client.ui.Button;
import com.github.gwtbootstrap.client.ui.CheckBox;
import com.github.gwtbootstrap.client.ui.Column;
import com.github.gwtbootstrap.client.ui.FluidRow;
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
import com.irisa.formulis.view.create.fixed.RelationCreateWidget;
import com.irisa.formulis.view.form.FormLineWidget.LINE_STATE;

/**
 * Widget principal pour un formulaire
 * @author pmaillot
 *
 */
public class FormWidget extends AbstractFormElementWidget {

	private FluidRow element = new FluidRow();
	private Column linesCol = new Column(12);
	private LinkedList<FormLineWidget> linesWidgets = new LinkedList<FormLineWidget>();

	private Column controlCol = new Column(1);
	private CheckBox profileCheckbox = new CheckBox();
	
	private FluidRow newRelationRow = new FluidRow();
	private Button newRelationButton = new Button("New line");
	private RelationCreateWidget relationCreationWid = new RelationCreateWidget(this);
	
	private FluidRow contentRow = new FluidRow();
	private Column contentCol = new Column(11,contentRow, newRelationRow);
	
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
		element.add(controlCol);
		contentRow.add(linesCol);
		newRelationButton.addStyleName("weblis-max-width");
		newRelationRow.add(newRelationButton);
		relationCreationWid.addRelationCreationHandler(this);
		controlCol.add(profileCheckbox);
		
		newRelationButton.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				ControlUtils.debugMessage("Creation button click");
				putRelationCreationWidget();
			}
		});
		
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
	
	public Callback getCallback() {
		return new Callback(this);
	}
	
	public Callback getCallback(FORM_CALLBACK_MODE mode) {
		return new Callback(this, mode);
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
		if(getData() != null/* && ! getData().isFinished()*/) {
			Form formData = getData();
			clear();

			if(formData.getTypeLine() != null && ! formData.getTypeLine().isAnonymous() ) {
				FormClassLineWidget typeLineWid = new FormClassLineWidget( formData.getTypeLine(), this);
				typeLineWid.setLineState(LINE_STATE.FINISHED);
				typeLineWid.showLabelBox();
				addLine(typeLineWid);
			}
						
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
		
		LinkedList<FormLine> lines = new LinkedList<FormLine>(this.getData().getLines());
		Collections.sort(lines, new FormLineComparator());
		
		Iterator<FormLine> itFo = lines.iterator();
		while(itFo.hasNext()) {
			FormLine line = itFo.next();
			if( line instanceof FormRelationLine) {
				FormRelationLineWidget nRelLine = new FormRelationLineWidget((FormRelationLine) line, this);
				nRelLine.setProfileMode(this.profileMode);
				result.add(nRelLine);
			} else if(line instanceof FormClassLine) {
				FormClassLineWidget nClassLine = new FormClassLineWidget((FormClassLine) line, this);
				nClassLine.setProfileMode(this.profileMode);
				result.add(nClassLine);
			}
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
		line.addCompletionAskedHandler(this);
		line.addElementCreationHandler(this);
		line.addLineSelectionHandler(this);
		line.addMoreCompletionsHandler(this);
//		line.addNestedFormHandler(this);
		line.addRelationCreationHandler(this);
		line.addRemoveLineHandler(this);
		line.addStatementChangeHandler(this);
		line.addTypeLineSetHandler(this);
	}

	public void setData(Form form) {
		data = form;
		reload();
	}

	@Override
	public void onClick(ClickEvent event) {
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
			
			if(this.getData().getTypeLine() != null) {
				result.setTypeLine(this.getData().getTypeLine().toProfileClassLine());
			}
			
			Iterator<FormLineWidget> itLine = this.linesWidgets.iterator();
			while(itLine.hasNext()) {
				FormLineWidget line = itLine.next();
				if(line.toProfileElement() != null) {
					result.addLine(line.toProfileLine());
				}
			}
			
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
	
	public class Callback implements FormEventCallback {

		private FormWidget source;
		private FORM_CALLBACK_MODE callbackMode = FORM_CALLBACK_MODE.LOAD ;
		
		public Callback(FormWidget src) {
			source = src;
		}
		
		public Callback(FormWidget src, FORM_CALLBACK_MODE mode) {
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
