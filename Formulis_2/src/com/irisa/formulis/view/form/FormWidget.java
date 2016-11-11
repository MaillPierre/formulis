package com.irisa.formulis.view.form;

import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedList;

import com.github.gwtbootstrap.client.ui.Button;
import com.github.gwtbootstrap.client.ui.CheckBox;
import com.github.gwtbootstrap.client.ui.Column;
import com.github.gwtbootstrap.client.ui.FluidRow;
import com.github.gwtbootstrap.client.ui.constants.ButtonType;
import com.github.gwtbootstrap.client.ui.constants.IconType;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.DragEndEvent;
import com.google.gwt.event.dom.client.DragEndHandler;
import com.google.gwt.event.dom.client.DragEnterEvent;
import com.google.gwt.event.dom.client.DragEnterHandler;
import com.google.gwt.event.dom.client.DragEvent;
import com.google.gwt.event.dom.client.DragHandler;
import com.google.gwt.event.dom.client.DragLeaveEvent;
import com.google.gwt.event.dom.client.DragLeaveHandler;
import com.google.gwt.event.dom.client.DragOverEvent;
import com.google.gwt.event.dom.client.DragOverHandler;
import com.google.gwt.event.dom.client.DragStartEvent;
import com.google.gwt.event.dom.client.DragStartHandler;
import com.google.gwt.event.dom.client.DropEvent;
import com.google.gwt.event.dom.client.DropHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.irisa.formulis.control.ControlUtils;
import com.irisa.formulis.control.Controller;
import com.irisa.formulis.control.profile.ProfileElement;
import com.irisa.formulis.control.profile.ProfileForm;
import com.irisa.formulis.model.form.Form;
import com.irisa.formulis.model.form.FormClassLine;
import com.irisa.formulis.model.form.FormLineComparator;
import com.irisa.formulis.model.form.FormRelationLine;
import com.irisa.formulis.view.AbstractDataWidget;
import com.irisa.formulis.view.AbstractFormulisWidget;
import com.irisa.formulis.view.ViewUtils;
import com.irisa.formulis.view.create.fixed.ClassCreateWidget;
import com.irisa.formulis.view.create.fixed.RelationCreateWidget;
import com.irisa.formulis.view.event.FinishableLineEvent;
import com.irisa.formulis.view.form.FormLineWidget.LINE_STATE;

/**
 * Widget principal pour un formulaire
 * @author pmaillot
 *
 */
public class FormWidget 
	extends AbstractFormElementWidget 
	implements DragEndHandler, DragStartHandler {

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
	private Button moreButton = new Button("", IconType.PLUS_SIGN);
	private Button reloadButton = new Button("", IconType.REFRESH);
	
	private boolean storeSet = false;
	
	public enum FINISH_BUTTON_STATE {
		FINISHABLE, // Peut être fini
		FINISHED, // Fini et envoyé au serveur
		EDIT; // Manque d'éléments pour être fini
	}
	
	/**
	 * Responsable de la gestion des evenement lorsqu'une ligne est en cours de déplacement (le début et la fin du déplacement son gérés par le form lui-même
	 */
	private LineDragHandler dragLineHand = null; // TODO Test pour gestion drag&drop, sale ?
	
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
		finishCol.add(moreButton);
		finishCol.add(reloadButton);
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
		setFinishButtonsState(computeFinishButtonState());
		moreButton.setBlock(true);
		moreButton.addClickHandler(this);
		moreButton.setTitle("Generalize");
		reloadButton.setBlock(true);
		reloadButton.addClickHandler(this);
		reloadButton.setTitle("Reload");
		
		profileCheckbox.addValueChangeHandler(new ValueChangeHandler<Boolean>() {
			@Override
			public void onValueChange(ValueChangeEvent<Boolean> event) {
				setSelectedForProfile(event.getValue());
			}
		});
		profileCheckbox.setText("Add to profile");
		setProfileMode(false);
		
		dragLineHand = new LineDragHandler(this);
		
		reload();
	}
	
	public boolean isStoreIsSet() {
		return this.storeSet;
	}
	
	public void setStoreIsSet(boolean storeIsSet) {
		this.storeSet = storeIsSet;
	}
	
	public FINISH_BUTTON_STATE computeFinishButtonState() {
		if(getData() != null) {
			if(getData().isFinishable()) {
				if(getData().isFinished()) {
					return FINISH_BUTTON_STATE.FINISHED;
				}
				return FINISH_BUTTON_STATE.FINISHABLE;
			}
		}
		return FINISH_BUTTON_STATE.EDIT;
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
				this.getSource().getData().setFinished(true);
				this.getSource().reload();
			}
		};
	}
	
	public void transformToSubmittedForm() {
//		ControlUtils.debugMessage("FormWidget transformToSubmittedForm");
		
		if(getData() != null && ! getData().isEmpty()) {
			clear();

			Iterator<FormLineWidget> itFo = formLinesToWidgetLines().iterator();
			while(itFo.hasNext()) {
				FormLineWidget line = itFo.next();
				if(line.getData().isFinishable()) {
					line.setLineState(LINE_STATE.FINISHED);
					addLine(line);
				}
			}
		} 
		newRelationButton.setEnabled(false);
		newClassButton.setEnabled(false);
		
//		this.finishButton.setVisible(! getData().isEmpty());
//		this.finishButton.setEnabled(false);
		this.setFinishButtonsState(FINISH_BUTTON_STATE.FINISHED);
		this.moreButton.setVisible(false);
		this.reloadButton.setVisible(false);
		
//		ControlUtils.debugMessage("FormWidget transformToSubmittedForm END");
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
		setFinishButtonsState(FINISH_BUTTON_STATE.EDIT);
	}
	
	public void reload() {
//		ControlUtils.debugMessage("FormWidget reload");
		if(getData() != null ) {
			if(! getData().isEmpty() ) {
				if(getData().isFinished()) {
					transformToSubmittedForm();
				} else {
					clear();
		
					Iterator<FormLineWidget> itFo = formLinesToWidgetLines().iterator();
					while(itFo.hasNext()) {
						FormLineWidget line = itFo.next();
						ControlUtils.debugMessage("\t" + line.getData());
						addLine(line);
					}
				} 
				
				newClassButton.setEnabled(isStoreIsSet() 
						&& (this.getData().isEmpty() 
								|| this.getData().isAnonymous() 
								|| this.getData().isTypeList())
						&& ! this.getData().isFinished());
				newRelationButton.setEnabled(isStoreIsSet() 
						&& (this.getData().isEmpty() 
								|| this.getData().isAnonymous() 
								|| this.getData().isTyped())
						&& ! this.getData().isFinished());
			}
		}
		this.finishButton.setVisible(getData() != null && ! getData().isEmpty());
//		this.finishButton.setEnabled(getData() != null && ! getData().isFinished());
		this.setFinishButtonsState(computeFinishButtonState());
		this.moreButton.setVisible(getData() != null && ! getData().isEmpty() && getData().hasMore() && ! getData().isFinished()); // TODO uncomment to reactivate More Form
//			this.moreButton.setVisible(false); // TODO comment to desactivate More Form
		this.reloadButton.setVisible(getData() != null && ! getData().isEmpty() && ! getData().isFinished());	
//		ControlUtils.debugMessage("FormWidget reload END");
	}
	
	@Override
	public void onFinishableLine(FinishableLineEvent event) {
		ControlUtils.debugMessage("FormWidget onFinishableLine " + event.getSource().getClass().getSimpleName() + " " + event.getState());
		super.onFinishableLine(event);
		this.setFinishButtonsState(computeFinishButtonState());
		// Si c'est la ligne de type qui est ré-éditée
		if(event.getSource() instanceof FormClassLineWidget && this.getData().isTyped() && ! ((FormLineWidget) event.getSource()).getData().isFinished() && this.getData().isFinished()) {
			this.getData().setFinished(false);
			reload();
		}
		ControlUtils.debugMessage("FormWidget onFinishableLine " + event.getSource().getClass().getSimpleName() + " " + event.getState() + " END");
	}
	
	public void setFinishButtonsState(FINISH_BUTTON_STATE state) {
//		ControlUtils.debugMessage("setFinishButtonsState " + state);
		if(state.equals(FINISH_BUTTON_STATE.FINISHED) || state.equals(FINISH_BUTTON_STATE.FINISHABLE)) {
			finishButton.setEnabled(state.equals(FINISH_BUTTON_STATE.FINISHABLE));
			this.finishButton.setBaseIcon(IconType.CHECK);
			this.finishButton.setType(ButtonType.SUCCESS);
		} else {
			finishButton.setEnabled(false);
			this.finishButton.setBaseIcon(IconType.PENCIL);
			this.finishButton.setType(ButtonType.DANGER);
		}
//		ControlUtils.debugMessage("setFinishButtonsState " + state + " END");
	}
	
	protected LinkedList<FormLineWidget> formLinesToWidgetLines() {
		LinkedList<FormLineWidget> result = new LinkedList<FormLineWidget>();
		
		Iterator<FormClassLine> itClassL = getData().typeLinesIterator();
		while(itClassL.hasNext()) {
			FormClassLine line = itClassL.next();
			if(! line.isAnonymous()) {
				FormClassLineWidget nClassLine = new FormClassLineWidget(line, this);
				nClassLine.setProfileMode(this.profileMode);
				result.add(nClassLine);
			}
		}

		Iterator<FormRelationLine> itRelL = getData().relationLinesIterator();
		while(itRelL.hasNext()) {
			FormRelationLine line = itRelL.next();
			FormRelationLineWidget nRelLine = new FormRelationLineWidget(line, this);
			if( ! getData().isFinished() || (getData().isFinished() && nRelLine.getData().isFinished())) {
				nRelLine.setProfileMode(this.profileMode);
				result.add(nRelLine);
			}
		}

		final FormLineComparator comp = new FormLineComparator();
		Collections.sort(result, new Comparator<FormLineWidget>(){
			@Override
			public int compare(FormLineWidget o1, FormLineWidget o2) {
				if(o1 != null && o2 != null) {
					return comp.compare(o1.getData(), o2.getData());
				} else {
					return 0;
				}
			}
		});
		
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
		if(line instanceof FormRelationLineWidget) {
			((FormRelationLineWidget) line).addDragEndHandler(this);
			((FormRelationLineWidget) line).addDragEnterHandler(dragLineHand);
			((FormRelationLineWidget) line).addDragHandler(dragLineHand);
			((FormRelationLineWidget) line).addDragLeaveHandler(dragLineHand);
			((FormRelationLineWidget) line).addDragOverHandler(dragLineHand);
			((FormRelationLineWidget) line).addDragStartHandler(this);
			((FormRelationLineWidget) line).addDropHandler(dragLineHand);
		}
	}
	
	public void insertRelationLine(FormRelationLineWidget line, FormRelationLineWidget after) {
		ControlUtils.debugMessage("FormWidget insertRelationLine " + line + " AFTER " + after);
		getData().insertLineAfter(line.getFormLine(), after.getFormLine());
		reload();
	}

	public void setData(Form form) {
		data = form;
		reload();
	}

	@Override
	public void finish() {
		if(getData().isFinishable() && ! getData().isFinished()) {
			fireFinishFormEvent(true, this.getSubmittedCallback());
		}
	}

	@Override
	public void onClick(ClickEvent event) {
		if(event.getSource() == newRelationButton) {
			putRelationCreationWidget();
		} else if(event.getSource() == newClassButton) {
			putClassCreationWidget();
		} else if(event.getSource() == finishButton) {
			finish();
		} else if(event.getSource() == this.moreButton) {
			fireMoreFormLinesEvent(getAppendCallback());
		} else if(event.getSource() == this.reloadButton) {
			fireReloadEvent(getAppendCallback());
		}
	}

	@Override
	public void onDragStart(DragStartEvent event) {
		ControlUtils.debugMessage("FormWidget onDragStart");
		if(event.getSource() instanceof FormRelationLineWidget) {
			FormRelationLineWidget src = (FormRelationLineWidget) event.getSource();
			event.setData("text", src.getData().toString());
			event.getDataTransfer().setDragImage(src.getElement(), 10, 10);
			this.dragLineHand.setCurrentDraggedLine(src);
			src.setDraggedLineFlag(true);
		}
	}

	@Override
	public void onDragEnd(DragEndEvent event) {
		ControlUtils.debugMessage("FormRelationLineWidget onDragEnd " + event.getSource());
		if(event.getSource() instanceof FormRelationLineWidget) {
			FormRelationLineWidget src = (FormRelationLineWidget) event.getSource();
			src.setDraggedLineFlag(false);
			this.dragLineHand.setCurrentDraggedLine(null);
		}
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
	
	public class LineDragHandler implements DragEnterHandler, DragLeaveHandler, DragHandler, DragOverHandler, DropHandler {

		private FormWidget formWid = null;
		private FormRelationLineWidget currentDraggedLine = null;
		
		public LineDragHandler(FormWidget formWidget) {
			formWid = formWidget;
		}
		
		public void setCurrentDraggedLine(FormRelationLineWidget line) {
			currentDraggedLine = line;
		}
		
		public FormRelationLineWidget getCurrentDraggedLine() {
			return currentDraggedLine;
		}

		@Override
		public void onDragOver(DragOverEvent event) {
			ControlUtils.debugMessage("FormRelationLineWidget onDragOver " + event.getSource());
			
			if(event.getSource() instanceof FormRelationLineWidget) {
				FormRelationLineWidget src = (FormRelationLineWidget) event.getSource();
				if(this.currentDraggedLine != null 
						&& ! src.equals(this.currentDraggedLine) 
						&& src.getParentWidget().equals(this.currentDraggedLine.getParentWidget())) {
					ControlUtils.debugMessage("FormRelationLineWidget onDragOver " + this.currentDraggedLine + " OVER " + src);
					src.setLinePlaceHolderFlag(true);
				}
			} else if(event.getSource() instanceof FormRelationLineWidget.FormRelationLinePlaceHolderWidget) {
				
			}
			event.stopPropagation();
			event.getNativeEvent().preventDefault();
		}

		@Override
		public void onDrag(DragEvent event) {
			ControlUtils.debugMessage("FormRelationLineWidget onDrag " + event.getSource());
			if(event.getSource() instanceof FormRelationLineWidget 
					&& currentDraggedLine.equals(event.getSource())) {
			}
			event.stopPropagation();
			event.getNativeEvent().preventDefault();
		}

		@Override
		public void onDragEnter(DragEnterEvent event) {
			ControlUtils.debugMessage("FormRelationLineWidget onDragEnter " + event.getSource());
			if(event.getSource() instanceof FormRelationLineWidget) {
				FormRelationLineWidget src = (FormRelationLineWidget) event.getSource();
				if(src.equals(currentDraggedLine)) {
					src.setLinePlaceHolderFlag(false);
				}
			}
			event.stopPropagation();
			event.getNativeEvent().preventDefault();
		}

		@Override
		public void onDragLeave(DragLeaveEvent event) {
			ControlUtils.debugMessage("FormRelationLineWidget onDragLeave " + event.getSource());
			if(event.getSource() instanceof FormRelationLineWidget) {
				FormRelationLineWidget src = (FormRelationLineWidget) event.getSource();
				if(! src.equals(currentDraggedLine)) {
					src.setLinePlaceHolderFlag(false);
				}
			}
			event.stopPropagation();
			event.getNativeEvent().preventDefault();
		}

		@Override
		public void onDrop(DropEvent event) {
			ControlUtils.debugMessage("FormRelationLineWidget onDrop " + event.getSource());
			if(event.getSource() instanceof FormRelationLineWidget) {
				FormRelationLineWidget src = (FormRelationLineWidget) event.getSource();
				if(! src.equals(currentDraggedLine)) {
					src.setLinePlaceHolderFlag(false);
					formWid.insertRelationLine(currentDraggedLine, src);
				}
			}
			event.stopPropagation();
			event.getNativeEvent().preventDefault();
		}
		
	}
	
}
