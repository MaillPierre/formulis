package com.irisa.formulis.view.form.suggest;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.user.client.ui.MenuBar;
import com.google.gwt.user.client.ui.MenuItem;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.VerticalPanel;

public class SuggestionPopover extends PopupPanel {
	
	private AbstractSuggestionWidget source;
	private LinkedList<Suggestion> content = new LinkedList<Suggestion>();
	private VerticalPanel panel = new VerticalPanel();
	private MenuBar suggestionBar = new MenuBar(true);
	private MenuItem moreElementsItem;
	private MenuItem newElementItem;
	private boolean canCreateNewElement = true;
	
	public SuggestionPopover(AbstractSuggestionWidget src) {
		super(true);
		
		this.setVisible(false);
		this.source = src;
		this.add(panel);
		panel.add(suggestionBar);
//		setMoreCompletionsMode(false);
		newElementItem = new MenuItem("New element", new ScheduledCommand() {
			@Override
			public void execute() {
				hide();
				source.fireElementCreationEvent(source.getValue());
			}
		});
		
		this.showRelativeTo(source);
		setCanCreateNewElement(canCreateNewElement);
		this.suggestionBar.addStyleName("weblis-suggestion-frame");
		
		refreshSuggestion();
		hide();
	}
	
	public void setCanCreateNewElement(boolean can) {
		canCreateNewElement = can;
		newElementItem.setVisible(can);
	}
	
	public boolean canCreateNewElement() {
		return canCreateNewElement;
	}
	
	public void focus() {
		this.suggestionBar.focus();
	}
	
	public void setContent(Collection<Suggestion> c) {
		content = new LinkedList<Suggestion>(c);
		refreshSuggestion();
	}
	
	public void addContent(Collection<Suggestion> c) {
		content.addAll(c);
		refreshSuggestion();
	}
	
	public void refreshSuggestion() {
		panel.clear();
		this.setVisible(true);
		suggestionBar.clearItems();
		LinkedList<MenuItem> itemList = new LinkedList<MenuItem>();
		Iterator<Suggestion> itContent = content.iterator();
		while(itContent.hasNext()) {
			final Suggestion sugg = itContent.next();
			MenuItem item = new MenuItem(sugg.getHtmlRepresentation(), true, new Scheduler.ScheduledCommand() {
				@Override
				public void execute() {
					hide();
					source.suggestionSelected(sugg);
				}
			});
			item.addStyleName("weblis-suggestion-menuitem");
			suggestionBar.addItem(item);
			itemList.add(item);
		}
		suggestionBar.addSeparator();
//		suggestionBar.addItem(moreElementsItem);
		suggestionBar.addItem(newElementItem);
		if(! itemList.isEmpty()) {
			suggestionBar.selectItem(itemList.getFirst());
		}
		panel.add(suggestionBar);
//		suggestionBar.focus();

		this.showRelativeTo(source);
	}
	
//	public void setMoreCompletionsMode(boolean mode) {
//		if(mode) {
//			moreElementsItem = new MenuItem("Less general", new ScheduledCommand() {
//				@Override
//				public void execute() {
//					hide();
//					source.fireLessCompletionsEvent();
//					source.setMoreCompletionMode(false);
//				}
//			});
//		} else {
//			moreElementsItem = new MenuItem("More general", new ScheduledCommand() {
//				@Override
//				public void execute() {
//					hide();
//					source.fireMoreCompletionsEvent();
//					source.setMoreCompletionMode(true);
//				}
//			});
//		}
//	}

}