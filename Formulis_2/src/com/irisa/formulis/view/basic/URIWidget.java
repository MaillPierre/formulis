package com.irisa.formulis.view.basic;

import com.google.gwt.dom.client.Style.Cursor;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOutHandler;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.dom.client.MouseOverHandler;
import com.irisa.formulis.control.ControlUtils;
import com.irisa.formulis.control.Controller;
import com.irisa.formulis.model.basic.URI;
import com.irisa.formulis.view.AbstractFormulisWidget;
import com.irisa.formulis.view.ViewUtils;
import com.irisa.formulis.view.event.ClickWidgetEvent;
import com.irisa.formulis.view.event.DescribeUriEvent;
import com.irisa.formulis.view.event.callback.StringCallback;
import com.irisa.formulis.view.event.callback.FormEventCallback;
import com.irisa.formulis.view.event.callback.AbstractStringCallback;
import com.irisa.formulis.view.event.callback.ActionCallback;
import com.irisa.formulis.view.event.interfaces.DescribeUriHandler;
import com.irisa.formulis.view.event.interfaces.HasDescribeUriHandler;
import com.irisa.formulis.view.form.AbstractFormLineWidget;

import java.util.Iterator;
import java.util.LinkedList;

import com.github.gwtbootstrap.client.ui.Popover;
import com.github.gwtbootstrap.client.ui.base.InlineLabel;
import com.github.gwtbootstrap.client.ui.constants.Placement;
import com.github.gwtbootstrap.client.ui.constants.Trigger;

public class URIWidget extends AbstractFormulisWidget implements HasDescribeUriHandler {
	
	private Popover tooltip = new Popover();
	private InlineLabel element = new InlineLabel(getData().getLabel());
	private LinkedList<DescribeUriHandler> describeUriHandlers = new LinkedList<DescribeUriHandler>();
	private String uriDesc = null;
	
	public URIWidget(URI u, AbstractFormulisWidget par) {
		super(u, par);
		initWidget(element);
		
		tooltip.setHtml(true);
		tooltip.setHeading(u.getLabel());
		tooltip.setText(u.getUri());
		if(par instanceof AbstractFormLineWidget) {
			tooltip.setText(u.getUri() + " <br/> " + ((AbstractFormLineWidget) par).getFormLine().getInfo());
		}
		tooltip.setWidget(element);
		tooltip.setTrigger(Trigger.MANUAL);
		tooltip.setPlacement(Placement.BOTTOM);
		tooltip.setHideDelay(2000);
		tooltip.reconfigure();
		
		if(getData().getKind() == URI.KIND.CLASS) {
			element.setStyleName("class");
		}
		else if(getData().getKind() == URI.KIND.ENTITY) {
			element.setStyleName("entity");
		}
		else if(getData().getKind() == URI.KIND.PROPERTY) {
			element.setStyleName("property");
		}
		
		element.addClickHandler(this);

		element.addMouseOutHandler(new MouseOutHandler() {
			@Override
			public void onMouseOut(MouseOutEvent event) {
				element.getElement().getStyle().setCursor(Cursor.DEFAULT);
				tooltip.hide();
			}
		});
		element.addMouseOverHandler(new MouseOverHandler() {
			@Override
			public void onMouseOver(MouseOverEvent event) {
				element.getElement().getStyle().setCursor(Cursor.MOVE);
//				if(uriDesc != null) {
					tooltip.setText(getTooltipText());

					tooltip.reconfigure();
					tooltip.show();
//				} 
//				else {
//					fireDescribeUriEvent(getTooltipCallback()); // TODO remettre pour interrogation à propos de l'URI
//				}
			}
		});
		
		element.setWordWrap(false);
	}
	
	public String getTooltipText() {
		String text = getData().getUri() + " <br/> ";
		if(getParentWidget() instanceof AbstractFormLineWidget) {
			text += ((AbstractFormLineWidget) getParentWidget()).getFormLine().getInfo();
		}
		if(uriDesc != null) {
			text += uriDesc;
		} 
		return text;
	}
	
	@Override
	public URI getData() {
		return (URI) super.getData();
	}
	
	@Override
	public String toString() {
		return "widget " + this.getData();
	}

	@Override
	public void onClick(ClickEvent event) {
		this.fireClickWidgetEvent(new ClickWidgetEvent(this));
	}

	public void addTooltipLine(String line) {
		this.tooltip.setText(this.tooltip.getText() + ViewUtils.getHTMLBreakLineString() + line);
	}
	
	public FormEventCallback getTooltipCallback() {
		return new AbstractStringCallback() {
			@Override
			public void call(String desc) {
				ControlUtils.debugMessage("URIWidget getTooltipCallback call \"" + desc + "\" ");
				uriDesc = desc;
				tooltip.setText(getTooltipText());

				tooltip.reconfigure();
				tooltip.show();
			}
		};
	}

	@Override
	public void fireDescribeUriEvent(DescribeUriEvent event) {
		ControlUtils.debugMessage("URIWidget fireDescribeUriEvent");
		Iterator<DescribeUriHandler> itHand = this.describeUriHandlers.iterator();
		while(itHand.hasNext()) {
			DescribeUriHandler hand = itHand.next();
			hand.onDescribeUri(event);
		}
	}

	@Override
	public void fireDescribeUriEvent(ActionCallback cb, URI u) {
		fireDescribeUriEvent(new DescribeUriEvent(this, cb, u));
	}
	
	public void fireDescribeUriEvent(URIWidget uri, ActionCallback cb) {
		fireDescribeUriEvent(new DescribeUriEvent(uri, cb, this.getData()));
	}
	
	public void fireDescribeUriEvent(ActionCallback cb) {
		fireDescribeUriEvent(this, cb);
	}

	@Override
	public void addDescribeUriHandler(DescribeUriHandler hand) {
		describeUriHandlers.add(hand);
	}

}
