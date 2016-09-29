package com.irisa.formulis.view.form;

import java.util.Iterator;
import java.util.LinkedList;

import com.github.gwtbootstrap.client.ui.Button;
import com.github.gwtbootstrap.client.ui.CheckBox;
import com.github.gwtbootstrap.client.ui.Column;
import com.github.gwtbootstrap.client.ui.FluidRow;
import com.github.gwtbootstrap.client.ui.constants.ButtonType;
import com.github.gwtbootstrap.client.ui.constants.IconType;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.irisa.formulis.control.ControlUtils;
import com.irisa.formulis.control.Controller;
import com.irisa.formulis.control.profile.ProfileElement;
import com.irisa.formulis.control.profile.ProfileForm;
import com.irisa.formulis.model.form.Form;
import com.irisa.formulis.model.form.FormClassLine;
import com.irisa.formulis.model.form.FormLine;
import com.irisa.formulis.model.form.FormRelationLine;
import com.irisa.formulis.view.AbstractFormulisWidget;
import com.irisa.formulis.view.ViewUtils;
import com.irisa.formulis.view.create.fixed.ClassCreateWidget;
import com.irisa.formulis.view.create.fixed.RelationCreateWidget;
import com.irisa.formulis.view.event.FinishLineEvent;
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
	
	private FluidRow newElementRow = new FluidRow();
	private Button newRelationButton = new Button("New line");
	private Button newClassButton = new Button("New class");
	private Column newRelationCol = new Column(6, newRelationButton);
	private Column newClassCol = new Column(6, newClassButton);
	private RelationCreateWidget relationCreationWid = new RelationCreateWidget(this);
	private ClassCreateWidget classCreationWid = new ClassCreateWidget(this);
	
	private FluidRow contentRow = new FluidRow();
	private Column contentCol = new Column(11,contentRow, newElementRow);
	
	private Column finishCol = new Column(1);
	private Button finishButton = new Button("", IconType.PENCIL);
	
	private boolean storeSet = false;
	
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
//		newRelationButton.addStyleName("weblis-max-width");
//		newClassButton.addStyleName("weblis-max-width");
		newRelationButton.setBlock(true);
		newClassButton.setBlock(true);
		newElementRow.add(newRelationCol);
		newElementRow.add(newClassCol);
		relationCreationWid.addRelationCreationHandler(this);
		classCreationWid.addClassCreationHandler(this);
		controlCol.add(profileCheckbox);
		
		newRelationButton.addClickHandler(this);
		newRelationButton.setEnabled(false);
		newClassButton.addClickHandler(this);
		newClassButton.setEnabled(false);
		
		finishButton.addClickHandler(this);
		finishButton.setBlock(true);
		setFinishButtonState(this.getData().isFinished());
		
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
	
	public boolean isStoreIsSet() {
		return this.storeSet;
	}
	
	public void setStoreIsSet(boolean storeIsSet) {
		this.storeSet = storeIsSet;
	}
	
	/**
	 * 
	 * @return un callback qui charge un nouveau contenu de formulaire 
	 */
	public FormCallback getLoadCallback() {
		return new FormCallback(this) {
			@Override
			public void call(Controller control) {
				control.loadFormContent(this.getSource());
			}
		};
	}

	/**
	 * 
	 * @return un callback qui ajoute un nouveau contenu au formulaire 
	 */
	public FormCallback getAppendCallback() {
		return new FormCallback(this) {
			@Override
			public void call(Controller control) {
				control.appendFormContent(this.getSource());
			}
		};
	}
	
	public FormCallback getSubmittedCallback() {
		return new FormCallback(this) {
			@Override
			public void call(Controller control) {
				this.getSource().transformToSubmittedForm();
			}
		};
	}
	
	public void transformToSubmittedForm() {
		if(getData() != null && ! getData().isEmpty()) {
			clear();

			Iterator<FormLineWidget> itFo = formLinesToWidget().iterator();
			while(itFo.hasNext()) {
				FormLineWidget line = itFo.next();
				if(line.getData().isFinished()) {
					addLine(line);
				}
			}
		} 
		newRelationButton.setEnabled(false);
		newClassButton.setEnabled(false);
		
		this.finishButton.setVisible(! getData().isEmpty());
		this.setFinishButtonState(true);
		this.finishButton.setEnabled(false);
	}
	
	public void putElementCreationButtons(){
		newElementRow.clear();
		newElementRow.add(newRelationCol);
		newElementRow.add(newClassCol);
	}
	
	public void putRelationCreationWidget(){
		newElementRow.clear();
		newElementRow.add(relationCreationWid);
	}

	public void putClassCreationWidget() {
		newElementRow.clear();
		newElementRow.add(classCreationWid);
	}
	
	public void clear() {
		linesCol.clear();
		newRelationButton.setEnabled(false);
		newClassButton.setEnabled(false);
		setFinishButtonState(false);
	}
	
	public void reload() {
		if(getData() != null ) {
			if(! getData().isEmpty()) {
				clear();
	
				Iterator<FormLineWidget> itFo = formLinesToWidget().iterator();
				while(itFo.hasNext()) {
					FormLineWidget line = itFo.next();
					addLine(line);
				}
//				newRelationButton.setEnabled(true);

				this.setFinishButtonState(this.getData().isFinished());
			} 
	
			
			newClassButton.setEnabled(isStoreIsSet() && (this.getData().isEmpty() || this.getData().isAnonymous() || this.getData().isTypeList()));
			newRelationButton.setEnabled(isStoreIsSet() && (this.getData().isEmpty() || this.getData().isAnonymous() || this.getData().isTyped()));
			
			this.finishButton.setVisible(! getData().isEmpty());
		}
	}
	
	@Override
	public void onFinishLine(FinishLineEvent event) {
		super.onFinishLine(event);
		this.setFinishButtonState(this.getData().isFinished());
	}
	
	public void setFinishButtonState(boolean finished) {
		finishButton.setEnabled(finished);
		if(finished) {
			this.finishButton.setBaseIcon(IconType.CHECK);
			this.finishButton.setType(ButtonType.SUCCESS);
		} else {
			this.finishButton.setBaseIcon(IconType.PENCIL);
			this.finishButton.setType(ButtonType.DANGER);
		}
	}
	
	protected LinkedList<FormLineWidget> formLinesToWidget() {
		LinkedList<FormLineWidget> result = new LinkedList<FormLineWidget>();
		
		Iterator<FormClassLine> itClassL = getData().typeLinesIterator();
		while(itClassL.hasNext()) {
			FormClassLine line = itClassL.next();
			if(! line.isAnonymous()) {
				FormClassLineWidget nClassLine = new FormClassLineWidget(line, this);
				nClassLine.setProfileMode(this.profileMode);
				result.add(nClassLine);
//				if(this.getData().isTyped() ) {
//					if(line.isFinished()) {
//						nClassLine.setLineState(LINE_STATE.FINISHED);
//					} else {
//						nClassLine.showLabelBox();
//					}
//				}
			}
		}

		Iterator<FormRelationLine> itRelL = getData().relationLinesIterator();
		while(itRelL.hasNext()) {
			FormRelationLine line = itRelL.next();
			FormRelationLineWidget nRelLine = new FormRelationLineWidget(line, this);
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
			putRelationCreationWidget();
		} else if(event.getSource() == newClassButton) {
			putClassCreationWidget();
		} else if(event.getSource() == finishButton) {
			ControlUtils.debugMessage("FormWidget onClick finishButton");
			fireFinishFormEvent(true, this.getSubmittedCallback());
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
	
	public abstract class FormCallback implements FormEventCallback {

		private FormWidget source;
		
		public FormCallback(FormWidget src) {
			source = src;
		}
		
		public FormWidget getSource() {
			return this.source;
		}
		
	}
	
}
