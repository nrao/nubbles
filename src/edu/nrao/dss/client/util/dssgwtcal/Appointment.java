package edu.nrao.dss.client.util.dssgwtcal;

import com.google.gwt.dom.client.Element;
import java.util.Date;

import com.google.gwt.event.dom.client.HasAllMouseHandlers;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.event.dom.client.MouseMoveEvent;
import com.google.gwt.event.dom.client.MouseMoveHandler;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOutHandler;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.dom.client.MouseOverHandler;
import com.google.gwt.event.dom.client.MouseUpEvent;
import com.google.gwt.event.dom.client.MouseUpHandler;
import com.google.gwt.event.dom.client.MouseWheelEvent;
import com.google.gwt.event.dom.client.MouseWheelHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.ComplexPanel;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.Widget;

// This class is what actually get's displayed in the calendar!
// Q: why do we need the AppointmentInterface?

public class Appointment extends Composite implements AppointmentInterface {

	// the different parts of the Appointment are all Div's; here we make
	// sure they all can handle mouse events.
	class Div extends ComplexPanel implements HasAllMouseHandlers {
		
		public Div() {
			
			setElement(DOM.createDiv());
		}

		@Override
		public void add(Widget w) {
			super.add(w, getElement());
		}

		public HandlerRegistration addMouseDownHandler(MouseDownHandler handler) {
			return addDomHandler(handler, MouseDownEvent.getType());
		}

		public HandlerRegistration addMouseUpHandler(MouseUpHandler handler) {
			return addDomHandler(handler, MouseUpEvent.getType());
		}

		public HandlerRegistration addMouseOutHandler(MouseOutHandler handler) {
			return addDomHandler(handler, MouseOutEvent.getType());
		}

		public HandlerRegistration addMouseOverHandler(MouseOverHandler handler) {
			return addDomHandler(handler, MouseOverEvent.getType());
		}

		public HandlerRegistration addMouseMoveHandler(MouseMoveHandler handler) {
			return addDomHandler(handler, MouseMoveEvent.getType());
		}

		public HandlerRegistration addMouseWheelHandler(MouseWheelHandler handler) {
			return addDomHandler(handler, MouseWheelEvent.getType());
		}
	}

	// NOTE: for many of these attributes, we not only set them, but 
	// (as can be seen below in their set* functions) we also set the
	// DOM attribute of the same name.
	private int eventId;
	private String title;
	private String description;
	private Date start;
	private Date end;
	private boolean selected;
	private float top;
	private float left;
	private float width;
	private float height;
	private AbsolutePanel mainPanel = new AbsolutePanel();
	private Panel headerPanel = new Div();
	private Panel bodyPanel = new Div();
	private Panel footerPanel = new Div();
	private Panel timelinePanel = new Div();
	private Panel timelineFillPanel = new Div();

	public Appointment() {

		initWidget(mainPanel);

		// set the CSS
		mainPanel.setStylePrimaryName("gwt-appointment");
		headerPanel.setStylePrimaryName("header");
		bodyPanel.setStylePrimaryName("body");
		footerPanel.setStylePrimaryName("footer");
		timelinePanel.setStylePrimaryName("timeline");
		timelineFillPanel.setStylePrimaryName("timeline-fill");

		// add all the Div's to the AbsolutePanel
		mainPanel.add(headerPanel);
		mainPanel.add(bodyPanel);
		mainPanel.add(footerPanel);
		mainPanel.add(timelinePanel);
		timelinePanel.add(timelineFillPanel);
		
		DOM.setStyleAttribute(mainPanel.getElement(), "position", "absolute");
	}

	public Date getStart() {
		return start;
	}

	public void setStart(Date start) {
		this.start = start;
	}

	public Date getEnd() {
		return end;
	}

	public void setEnd(Date end) {
		this.end = end;
	}

	public boolean isSelected() {
		return selected;
	}

	// maintains state, and changes style name
	public void setSelected(boolean selected) {

		// set selected
		this.selected = selected;

		// remove selected style (if exists)
		this.removeStyleName("gwt-appointment-selected");

		// if selected, add the selected style
		if (selected) {
			this.addStyleName("gwt-appointment-selected");
		}
	}

	public float getTop() {
		return top;
	}

	public void setTop(float top) {
		this.top = top;
		DOM.setStyleAttribute(mainPanel.getElement(), "top", top + "px");
	}

	public float getLeft() {
		return left;
	}

	public void setLeft(float left) {
		this.left = left;
		DOM.setStyleAttribute(mainPanel.getElement(), "left", left + "%");
	}

	public float getWidth() {
		return width;
	}

	public void setWidth(float width) {
		this.width = width;
		DOM.setStyleAttribute(mainPanel.getElement(), "width", width + "%");
	}

	public float getHeight() {
		return height;
	}

	public void setHeight(float height) {
		this.height = height;
		DOM.setStyleAttribute(mainPanel.getElement(), "height", height + "px");
	}

	@Override
	public String getTitle() {
		return title;
	}

	@Override
	public void setTitle(String title) {
		this.title = title;
		DOM.setInnerText(headerPanel.getElement(), title);
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
		DOM.setInnerHTML(bodyPanel.getElement(), description);
	}

	public void formatTimeline(float top, float height) {
		timelineFillPanel.setHeight(height + "%");
		DOM.setStyleAttribute(timelineFillPanel.getElement(), "top", top + "%");
	}

	// Two appointments are considered equal if their start & end dates are equal.
	public int compareTo(AppointmentInterface appt) {
		// -1 0 1
		// less, equal, greater
		int compare = this.getStart().compareTo(appt.getStart());

		if (compare == 0) {
			compare = appt.getEnd().compareTo(this.getEnd());
		}

		return compare;
	}

	public Widget getMoveHandle() {
		return headerPanel;
	}

	public Widget getResizeHandle() {
		return footerPanel;
	}

	public void setEventId(int eventId) {
		this.eventId = eventId;
	}

	public int getEventId() {
		return eventId;
	}
}
