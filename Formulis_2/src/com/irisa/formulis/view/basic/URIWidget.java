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
import com.irisa.formulis.view.event.interfaces.DescribeUriHandler;
import com.irisa.formulis.view.event.interfaces.HasDescribeUriHandler;
import com.irisa.formulis.view.form.FormEventCallback;
import com.irisa.formulis.view.form.FormLineWidget;

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
	
	public interface DescribeUriCallback extends FormEventCallback {
		
		public void call(String description);
		
	}

	public URIWidget(URI u, AbstractFormulisWidget par) {
		super(u, par);
		initWidget(element);
		
		tooltip.setHtml(true);
		tooltip.setHeading(u.getLabel());
		tooltip.setText(u.getUri());
		if(par instanceof FormLineWidget) {
			tooltip.setText(u.getUri() + " <br/> " + ((FormLineWidget) par).getFormLine().getInfo());
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
				if(uriDesc != null) {
					String text = tooltip.getText();
					text += uriDesc;
					tooltip.setText(text);
				} 
				else {
					fireDescribeUriEvent(getTooltipCallback());
				}
				tooltip.show();
			}
		});
		
		element.setWordWrap(false);
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
		return new DescribeUriCallback() {
			@Override
			public void call(String desc) {
				String text = tooltip.getText();
				text += desc;
				tooltip.setText(text);
				uriDesc = desc;
			}
			
			@Override
			public void call(Controller control) {
				// Should never happen
			}
		};
	}

	@Override
	public void fireDescribeUriEvent(DescribeUriEvent event) {
		Iterator<DescribeUriHandler> itHand = this.describeUriHandlers.iterator();
		while(itHand.hasNext()) {
			DescribeUriHandler hand = itHand.next();
			hand.onDescribeUri(event);
		}
	}

	@Override
	public void fireDescribeUriEvent(FormEventCallback cb) {
		fireDescribeUriEvent(new DescribeUriEvent(this, cb));
	}

	@Override
	public void addDescribeUriHandler(DescribeUriHandler hand) {
		describeUriHandlers.add(hand);
	}

}
