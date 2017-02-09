package com.irisa.formulis.view;

import java.rmi.UnexpectedException;
import java.util.Iterator;
import java.util.LinkedList;

import com.github.gwtbootstrap.client.ui.base.InlineLabel;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlexTable;
import com.irisa.formulis.control.ControlUtils;
import com.irisa.formulis.model.Answers;
import com.irisa.formulis.model.DataUtils;
import com.irisa.formulis.model.answers.AnswersHeader;
import com.irisa.formulis.model.answers.AnswersRow;
import com.irisa.formulis.model.basic.BasicElement;
import com.irisa.formulis.model.basic.Pair;
import com.irisa.formulis.model.exception.FormElementConversionException;
import com.irisa.formulis.model.exception.UnexpectedAction;
import com.irisa.formulis.model.form.Form;
import com.irisa.formulis.view.custom.SimpleFormWidget;

public class AnswersWidget extends Composite {

	private Answers answers;
	private FlexTable table;

	public AnswersWidget(Answers a) {
		super();
		answers = a;
		table = new FlexTable();

		table.addStyleName("table-bordered");
		table.addStyleName("table-condensed");

		initWidget(table);

		loadAnswers();
	}

	public AnswersWidget() {
		this(null);
	}

	private void loadAnswers() {
		if(answers != null) {
			try {
				loadAnswers(answers);
			} catch (Exception e) {
				ControlUtils.exceptionMessage(e);
			}
		}
	}

	private void loadAnswers(Answers a) throws UnexpectedAction {
		answers = a;
		ControlUtils.debugMessage("AnswerWidget loadAnswers Answers=" + a);

		Iterator<AnswersHeader> itHeader = answers.headerColumnsIterator();
		int colHeader = 0;
		while(itHeader.hasNext()) {
			AnswersHeader header = itHeader.next();
			InlineLabel headerLabel = new InlineLabel(header.getName());
			headerLabel.addStyleName("header");
			table.setWidget(0, colHeader, headerLabel);
			colHeader++;
		}

		int numRow = 1;
		Iterator<AnswersRow> itRow = answers.contentRowsIterator();
		while(itRow.hasNext()) {
			int numCol = 0;
			AnswersRow row = itRow.next();
			ControlUtils.debugMessage("AnswerWidget loadAnswers " + row);
			Iterator<BasicElement> itElem = row.contentIterator();
			while(itElem.hasNext()) {
				try {
					BasicElement elem = itElem.next();
					LinkedList<BasicElement> rootElems = DataUtils.getFirstDisplayableElements(elem);
					if(rootElems.size() == 1 && rootElems.getFirst() instanceof Pair) {
						try {
							Form elemForm = DataUtils.pairToForm((Pair) rootElems.getFirst(), null);
							SimpleFormWidget formWid = ViewUtils.toSimpleWidget(elemForm);
							formWid.addClickHandler(new ClickHandler() {
								@Override
								public void onClick(ClickEvent event) {
									ControlUtils.debugMessage("Click sur answers Form");
								}
							});
							table.setWidget(numRow, numCol, formWid);
						} catch(ClassCastException e) {
							ControlUtils.exceptionMessage(e);
							ControlUtils.debugMessage("EXCEPTION ANSWERS ROW FORM: " + rootElems);
						}
					} else /*if(rootElems.size() == 1)*/{
						try {
							SimpleFormWidget elemWid = ViewUtils.toSimpleWidget(elem);
							elemWid.addHandler(new ClickHandler() {
								@Override
								public void onClick(ClickEvent event) {
									ControlUtils.debugMessage("Click sur answers ");
								}
							}, ClickEvent.getType());
							table.setWidget(numRow, numCol, elemWid);
						} catch(ClassCastException e) {
							ControlUtils.exceptionMessage(e);
							ControlUtils.debugMessage("EXCEPTION ANSWERS ROW: BASIC " + elem.getClass());
						}
//					} else if (rootElems.size() == a.getHeaderColumns().size()) {
//						ControlUtils.debugMessage("AnswersWidget loadAnswers " + rootElems);
//					} else {
//						throw new UnexpectedAction("Unexpected number of elements in the row");
					}
				} catch (FormElementConversionException e) {
					table.setText(numRow, numCol, "ERROR " + e.getMessage());
				}
				numCol++;
			}
			numRow++;
		}
	}

	public void setAnswers(Answers a) {
		this.setVisible(true);
		table.clear(true);
		if(a != null) {
			try {
				loadAnswers(a);
			} catch (Exception e) {
				ControlUtils.exceptionMessage(e);
			}
		}
	}

}
