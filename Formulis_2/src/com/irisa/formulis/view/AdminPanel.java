package com.irisa.formulis.view;

import com.github.gwtbootstrap.client.ui.Button;
import com.github.gwtbootstrap.client.ui.Column;
import com.github.gwtbootstrap.client.ui.FluidRow;
import com.github.gwtbootstrap.client.ui.ListBox;
import com.github.gwtbootstrap.client.ui.Paragraph;
import com.github.gwtbootstrap.client.ui.TextBox;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DisclosurePanel;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * Settings panel
 * @author pmaillot
 *
 */
public class AdminPanel extends Composite  {
	
	private DisclosurePanel element = new DisclosurePanel();
	private VerticalPanel content = new VerticalPanel();
	
	
	private FluidRow namespaceRow = new FluidRow();
	private FluidRow nsListRow = new FluidRow();
	private Column nsListCol = new Column(6, nsListRow);
	private HorizontalPanel nsCreateRow = new HorizontalPanel();
	private Column nsCreateCol = new Column(6, nsCreateRow);
	
	private FluidRow permalinkRow = new FluidRow();
//	private FluidRow permalinkBoxRow = new FluidRow();
//	private Column permalinkCol = new Column(12, permalinkBoxRow);
	private TextBox permalinkBox = new TextBox();
	
	private TextBox namespacePrefixBox = new TextBox();
	private TextBox namespaceUriBox = new TextBox();
	private Button namespaceDefineButton = new Button("Define prefix");
	private ListBox nsListBox = new ListBox();
	
	public AdminPanel() {
		initWidget(element);
		
		element.setHeader(new Paragraph("Settings"));
		element.setContent(content);
		element.setWidth("100%");
		content.setWidth("100%");
		
		
		getNamespacePrefixBox().setPlaceholder("Prefix");
		getNamespacePrefixBox().setWidth("100px");
		getNamespaceUriBox().setPlaceholder("Uri");
		getNamespaceUriBox().setWidth("100px");
		nsListBox.setVisibleItemCount(5);
		nsListBox.setWidth("100%");
		
//		permalinkRow.add(permalinkCol);
//		permalinkBoxRow.add(permalinkBox);
//		permalinkBox.setReadOnly(true);
//		permalinkBox.setVisibleLength(500);
		
		content.add(namespaceRow);
		content.add(permalinkRow);
		namespaceRow.add(nsListCol);
		namespaceRow.add(nsCreateCol);
		nsCreateRow.add(getNamespacePrefixBox());
		nsCreateRow.add(getNamespaceUriBox());
		nsCreateRow.add(getNamespaceDefineButton());
		nsListRow.add(nsListBox);
		
	}
	
	public ListBox getNsListBox() {
		return this.nsListBox;
	}

	public Button getNamespaceDefineButton() {
		return namespaceDefineButton;
	}

	public void setNamespaceDefineButton(Button namespaceDefineButton) {
		this.namespaceDefineButton = namespaceDefineButton;
	}

	public TextBox getNamespacePrefixBox() {
		return namespacePrefixBox;
	}

	public void setNamespacePrefixBox(TextBox namespacePrefixBox) {
		this.namespacePrefixBox = namespacePrefixBox;
	}

	public TextBox getNamespaceUriBox() {
		return namespaceUriBox;
	}

	public void setNamespaceUriBox(TextBox namespaceUriBox) {
		this.namespaceUriBox = namespaceUriBox;
	}
	
	public void setStatePermalink(String link) {
//		ControlUtils.debugMessage("AdminPanel setStatePermalink ( "+ link + " )");
		this.permalinkBox.setText(link);
	}
}
