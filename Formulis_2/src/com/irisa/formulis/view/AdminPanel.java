package com.irisa.formulis.view;

import com.github.gwtbootstrap.client.ui.Button;
import com.github.gwtbootstrap.client.ui.IntegerBox;
import com.github.gwtbootstrap.client.ui.ListBox;
import com.github.gwtbootstrap.client.ui.NavText;
import com.github.gwtbootstrap.client.ui.Paragraph;
import com.github.gwtbootstrap.client.ui.TextArea;
import com.github.gwtbootstrap.client.ui.TextBox;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DisclosurePanel;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.VerticalPanel;

public class AdminPanel extends Composite  {
	
	private DisclosurePanel element = new DisclosurePanel();
	private VerticalPanel content = new VerticalPanel();
	
	
	private HorizontalPanel namespacePanel = new HorizontalPanel();
	
	public TextBox namespacePrefixBox = new TextBox();
	public TextBox namespaceUriBox = new TextBox();
	public Button namespaceDefineButton = new Button("Définir préfixe");
	
	
	private HorizontalPanel limitProfilePanel = new HorizontalPanel();
	
	private HorizontalPanel limitPanel = new HorizontalPanel();
	public IntegerBox limitBox = new IntegerBox();
	private NavText limitLabel = new NavText("Nb. sugg. max: ");
	
	private HorizontalPanel profilePanel = new HorizontalPanel();
	public Button profileModeButton = new Button("Mode profile");
	public Button profileCreateButton = new Button("Créer profile");
	public TextBox profileNameBox = new TextBox();
	public Button profileGoButton = new Button("Go");
	public Button profileDeleteButton = new Button("Suppr.");
	public Button profileClearButton = new Button("Suppr. tous");
	
	public DisclosurePanel profileEditDiscPanel = new DisclosurePanel("Edition directe des profiles");
	public VerticalPanel profileEditPanel = new VerticalPanel();
	public TextArea profileEditArea = new TextArea();
	private HorizontalPanel profileEditButtonPanel = new HorizontalPanel();
	public Button profileEditSave = new Button("Sauvegarder");
	public Button profileEditClear = new Button("Effacer");
	public Button profileEditReload = new Button("Recharger");
	
	public ListBox profileList = new ListBox();
	
	public AdminPanel() {
		initWidget(element);
		
		element.setHeader(new Paragraph("Gestion"));
		element.setContent(content);
		
		
		limitBox.setWidth("15px");
		limitBox.addStyleName("weblis-navbar-textbox");
		limitLabel.addStyleName("weblis-navbar-text");
		
		profileNameBox.addStyleName("weblis-navbar-textbox");
		profileNameBox.setPlaceholder("Nom du profile");
		profileNameBox.setWidth("120px");
		profileList.addStyleName("weblis-navbar-textbox");
		profileList.addItem("");
		profileList.setWidth("150px");
		profileEditDiscPanel.add(profileEditPanel);
		profileEditPanel.add(profileEditArea);
		profileEditPanel.add(profileEditButtonPanel);
		profileEditButtonPanel.add(profileEditSave);
		profileEditButtonPanel.add(profileEditClear);
		profileEditButtonPanel.add(profileEditReload);
		
		
		namespacePrefixBox.setPlaceholder("Préfixe");
		namespacePrefixBox.setWidth("100px");
		namespacePrefixBox.addStyleName("weblis-navbar-textbox");
		namespaceUriBox.setPlaceholder("Uri");
		namespaceUriBox.setWidth("100px");
		namespaceUriBox.addStyleName("weblis-navbar-textbox");
		
		content.add(limitProfilePanel);
		content.add(namespacePanel);
		limitProfilePanel.add(limitPanel);
		limitProfilePanel.add(profilePanel);
		limitPanel.add(limitLabel);
		limitPanel.add(limitBox);
		profilePanel.add(profileModeButton);
		profilePanel.add(profileCreateButton);
		profilePanel.add(profileNameBox);
		profilePanel.add(profileList);
		profilePanel.add(profileGoButton);
		profilePanel.add(profileDeleteButton);
		profilePanel.add(profileClearButton);
		namespacePanel.add(namespacePrefixBox);
		namespacePanel.add(namespaceUriBox);
		namespacePanel.add(namespaceDefineButton);
		namespacePanel.add(profileEditDiscPanel);
		
	}
}
