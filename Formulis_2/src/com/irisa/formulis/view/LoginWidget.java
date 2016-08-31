package com.irisa.formulis.view;

import com.github.gwtbootstrap.client.ui.Column;
import com.github.gwtbootstrap.client.ui.FluidRow;
import com.github.gwtbootstrap.client.ui.NavText;
import com.github.gwtbootstrap.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.github.gwtbootstrap.client.ui.PasswordTextBox;
import com.github.gwtbootstrap.client.ui.TextBox;

public class LoginWidget extends Composite {

	private FluidRow element;
	private Column elementCol = new Column(12);
	private LOGIN_STATE logState;
	
	public enum LOGIN_STATE {
		LOGGED,
		NEW_USER,
		NOT_LOGGED
	}
	
	// Not logged
	private FluidRow notLoggedRow = new FluidRow();
		private FluidRow notLoggedLoginRow = new FluidRow();
		private Column notLoggedLoginCol = new Column(4, notLoggedLoginRow);
			private NavText notLoggedLoginLabel = new NavText("Nom:");
			private Column notLoggedLoginLabelCol = new Column(4, notLoggedLoginLabel);
			public TextBox notLoggedLoginTextbox = new TextBox(); 
			private Column notLoggedLoginTextboxCol = new Column(8, notLoggedLoginTextbox);
		private FluidRow notLoggedPasswdRow = new FluidRow();
		private Column notLoggedPasswdCol = new Column(5, notLoggedPasswdRow);
			private NavText notLoggedPasswdLabel = new NavText("Mot de passe:");
			private Column notLoggedPasswdLabelCol = new Column(5, notLoggedPasswdLabel);
			public PasswordTextBox notLoggedPasswdTextBox = new PasswordTextBox();
			private Column notLoggedPasswdTextBoxCol = new Column(7, notLoggedPasswdTextBox);
		private HorizontalPanel notLoggedButtonPanel = new HorizontalPanel();
		private Column notLoggedButtonCol = new Column(3 , notLoggedButtonPanel);
		public Button notLoggedLoginButton = new Button("Connexion");
//		private Column notLoggedLoginButtonCol = new Column(1, notLoggedLoginButton);
		public Button notLoggedNewuserButton = new Button("Nouveau");
//		private Column notLoggedNewuserButtonCol = new Column(2, notLoggedNewuserButton);
	
	// Logged
	private HorizontalPanel loggedRow = new HorizontalPanel();
	public NavText loggedUsernameLabel = new NavText();
	public Button logoutButton = new Button("Déco.");
	
	// New user
	private FluidRow newUserLoginPsswdRow = new FluidRow();
	private NavText newUserloginLabel = new NavText("Nom:");
	private Column newUserloginLabelCol = new Column(1, newUserloginLabel);
	public TextBox newUserTextbox = new TextBox(); 
	private Column newUserTextboxCol = new Column(4, newUserTextbox);
	private NavText newUserpasswdLabel = new NavText("Mot de passe:");
	private Column newUserpasswdLabelCol = new Column(2, newUserpasswdLabel);
	public PasswordTextBox newUserpasswdTextBox = new PasswordTextBox();
	private Column newUserpasswdTextBoxCol = new Column(4, newUserpasswdTextBox);
	
	private FluidRow newUserRepeatEmailRow = new FluidRow();
	private NavText newUserEmailLabel = new NavText("e-mail:");
	private Column newUserEmailLabelCol = new Column(1, newUserEmailLabel);
	public TextBox newUserEmailTextbox = new TextBox();
	private Column newUserEmailTextboxCol = new Column(4, newUserEmailTextbox);
	private NavText newUserRepeatPasswdLabel = new NavText("Répéter:");
	private Column newUserRepeatPasswdLabelCol = new Column(2, newUserRepeatPasswdLabel);
	public PasswordTextBox newUserRepeatPasswdTextBox = new PasswordTextBox();
	private Column newUserRepeatPasswdTextBoxCol = new Column(4, newUserRepeatPasswdTextBox);
	public Button newUserButton = new Button("Céer");
	private Column newUserButtonCol = new Column(1, newUserButton);
	private Column newUserCol = new Column(12, newUserLoginPsswdRow, newUserRepeatEmailRow);
	
	
	public LoginWidget() {
		logState = LOGIN_STATE.NOT_LOGGED;
		element = new FluidRow();
		
		initWidget(element);
		element.add(elementCol);
		
		newUserloginLabel.setStyleName("weblis-navbar-text");
		newUserpasswdLabel.setStyleName("weblis-navbar-text");
		loggedUsernameLabel.setStyleName("weblis-navbar-text");
		newUserRepeatPasswdLabel.setStyleName("weblis-navbar-text");
		newUserEmailLabel.setStyleName("weblis-navbar-text");
		
		newUserTextbox.setWidth("100%");
		newUserpasswdTextBox.setWidth("100%");
		newUserRepeatPasswdTextBox.setWidth("100%");
		newUserEmailTextbox.setWidth("100%");
		notLoggedLoginLabel.setWidth("100%");
		notLoggedLoginLabel.addStyleName("weblis-navbar-text");
		notLoggedLoginTextbox.setWidth("100%");
		notLoggedLoginTextbox.addStyleName("weblis-navbar-textbox");
		notLoggedLoginTextbox.setPlaceholder("Nom d'utilisateur");
		notLoggedPasswdLabel.setWidth("100%");
		notLoggedPasswdLabel.addStyleName("weblis-navbar-text");
		notLoggedPasswdTextBox.setWidth("100%");
		notLoggedPasswdTextBox.addStyleName("weblis-navbar-textbox");
		notLoggedPasswdTextBox.setPlaceholder("Mot de passe");

		notLoggedRow.setWidth("100%");
		notLoggedLoginRow.add( notLoggedLoginLabelCol);
		notLoggedLoginRow.add( notLoggedLoginTextboxCol);
		notLoggedRow.add( notLoggedLoginCol);
		notLoggedPasswdRow.add( notLoggedPasswdLabelCol);
		notLoggedPasswdRow.add( notLoggedPasswdTextBoxCol);
		notLoggedButtonPanel.add(notLoggedLoginButton);
//		notLoggedButtonPanel.add(notLoggedNewuserButton);
		notLoggedRow.add( notLoggedPasswdCol);
		notLoggedRow.add( notLoggedButtonCol);
		
		loggedUsernameLabel.addStyleName("weblis-navbar-text");
		loggedRow.add(loggedUsernameLabel);
		loggedRow.add(logoutButton);

		newUserloginLabel.addStyleName("weblis-navbar");
		newUserTextbox.addStyleName("weblis-navbar-textbox");
		newUserpasswdTextBox.addStyleName("weblis-navbar-textbox");
		newUserRepeatPasswdTextBox.addStyleName("weblis-navbar-textbox");
		newUserEmailTextbox.addStyleName("weblis-navbar-textbox");
		newUserloginLabel.addStyleName("weblis-navbar");
		newUserLoginPsswdRow.add(newUserloginLabelCol);
		newUserLoginPsswdRow.add(newUserTextboxCol);
		newUserLoginPsswdRow.add(newUserpasswdLabelCol);
		newUserLoginPsswdRow.add(newUserpasswdTextBoxCol);
		newUserRepeatEmailRow.add(newUserEmailLabelCol);
		newUserRepeatEmailRow.add(newUserEmailTextboxCol);
		newUserRepeatEmailRow.add(newUserRepeatPasswdLabelCol);
		newUserRepeatEmailRow.add(newUserRepeatPasswdTextBoxCol);
		newUserRepeatEmailRow.add(newUserButtonCol);
		
		loadNotLoggedContent();
	}
	
	public void setLogState(LOGIN_STATE state) {
		logState = state;
		if(logState == LOGIN_STATE.LOGGED) {
			loadLoggedContent();
		} else if (logState == LOGIN_STATE.NEW_USER) {
			loadNewUserContent();
		} else {
			loadNotLoggedContent();
		}
	}
	
	private void loadLoggedContent() {
		elementCol.clear();
		
		elementCol.add(loggedRow);
		
	}
	
	private void loadNotLoggedContent() {
		elementCol.clear();

		elementCol.add(notLoggedRow);
	}
	
	private void loadNewUserContent() {
		elementCol.clear();
		
		elementCol.add(newUserCol); 
	}
	
	public void setUsername(String name) {
		this.loggedUsernameLabel.setText(name);
	}
}
