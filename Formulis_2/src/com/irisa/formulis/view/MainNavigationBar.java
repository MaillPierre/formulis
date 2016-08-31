package com.irisa.formulis.view;

import java.util.Collection;
import java.util.Iterator;

import com.github.gwtbootstrap.client.ui.Button;
import com.github.gwtbootstrap.client.ui.Column;
import com.github.gwtbootstrap.client.ui.FluidRow;
import com.github.gwtbootstrap.client.ui.Label;
import com.github.gwtbootstrap.client.ui.ListBox;
import com.github.gwtbootstrap.client.ui.NavText;
import com.github.gwtbootstrap.client.ui.ResponsiveNavbar;
import com.github.gwtbootstrap.client.ui.constants.LabelType;
import com.github.gwtbootstrap.client.ui.constants.NavbarPosition;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.irisa.formulis.control.ControlUtils;
import com.irisa.formulis.model.Store;

public class MainNavigationBar extends Composite{

	// Indicators & User menus
	public ResponsiveNavbar navBar = new ResponsiveNavbar();
	public Column controlsCol = new Column(12);
	public FluidRow statusBarPanel = new FluidRow(); // 12
	public LoginWidget loginWid = new LoginWidget();
	private Column loginCol = new Column(6, loginWid); // 8 / 12
//	public FluidRow storeListPanel = new FluidRow(); // 3 / 12
	public HorizontalPanel storeListPanel = new HorizontalPanel(); // 3 / 12
	private Column storeListCol = new Column(2, storeListPanel);
	
	private HorizontalPanel serverPanel = new HorizontalPanel();
	public NavText serverLabel = new NavText("Serveur: ");
	private Label serverStatusLabel = new Label();
	private Column serverLabelCol = new Column(2, serverPanel); // 1 / 12

	public NavText storeListLabel = new NavText("Store: ");
	private Column storeListLabelCol = new Column(4, storeListLabel); // 1 /3
	public ListBox storeListBox = new ListBox();
	private Column storeListBoxCol = new Column(8, storeListBox); // 2 / 3
	
//	public CustomSuggestionWidget debugField = new CustomSuggestionWidget(null);
//	private Column debugFieldCol = new Column(1, debugField);
	
	// Buttons
	private FluidRow buttonRow = new FluidRow();
	public AdminPanel adminPanel = new AdminPanel();
//	private FluidRow navigRow = new FluidRow();
//	private Column navigCol = new Column(6, navigRow);
//	public Paragraph navigLabel = new Paragraph("History: ");
//	private Column navigLabelCol = new Column(2, navigLabel);
//	public Button backButton = new Button("Back");
//	private Column backButtonCol = new Column(2, backButton);
//	public Button forwardButton = new Button("Forward");
//	private Column forwardButtonCol = new Column(2, forwardButton);
	public Button finishButton = new Button("Finish");
	private Column finishButtonCol = new Column(1, finishButton);

	public MainNavigationBar() {
		super();
		try {
		initWidget(navBar);
		
		
		// Barre de statut pour démo 12/04/2016
		storeListPanel.setWidth("100%");
		storeListPanel.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
		storeListBox.setWidth("100%");
		storeListBox.addStyleName("weblis-navbar-textbox");
		storeListPanel.add(storeListLabelCol);
		storeListPanel.add(storeListBoxCol);
		serverPanel.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
		serverPanel.add(serverLabel);
		serverPanel.add(serverStatusLabel);
		statusBarPanel.add(loginCol);
		statusBarPanel.add(storeListCol);
		statusBarPanel.add(serverLabelCol);
		statusBarPanel.add(finishButtonCol);
		
		buttonRow.add(adminPanel);
		
		storeListLabel.addStyleName("weblis-navbar-text");
		serverLabel.addStyleName("weblis-navbar-text");
				
		controlsCol.add(statusBarPanel);
		controlsCol.add(buttonRow);
		navBar.add(controlsCol);
		navBar.setPosition(NavbarPosition.TOP);
		

		} catch(Exception e) {
			ControlUtils.debugMessage("MainNavigationBar");
			ControlUtils.exceptionMessage(e);
		}
	}
	
	public void setStoreList(final Collection<Store> collection) {
		storeListBox.clear();
		storeListBox.addItem("", "");
		Iterator<Store> itList = collection.iterator();
		while(itList.hasNext())
		{
			Store sto = itList.next();
			String storeName = sto.getName();
			String storeLabel = sto.getLabel();
			storeListBox.addItem(storeLabel, storeName);
		}
		storeListBox.setItemSelected(0, true);
	}
	
	public void setServerStatusMessage(String message) {
		if(message == "ok") {
			serverStatusLabel.setType(LabelType.SUCCESS);
		} else {
			serverStatusLabel.setType(LabelType.INFO);
		}
		serverStatusLabel.setText(message);
	}

}
