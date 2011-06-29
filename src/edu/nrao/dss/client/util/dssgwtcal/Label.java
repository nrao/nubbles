// Copyright (C) 2011 Associated Universities, Inc. Washington DC, USA.
// 
// This program is free software; you can redistribute it and/or modify
// it under the terms of the GNU General Public License as published by
// the Free Software Foundation; either version 2 of the License, or
// (at your option) any later version.
// 
// This program is distributed in the hope that it will be useful, but
// WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
// General Public License for more details.
// 
// You should have received a copy of the GNU General Public License
// along with this program; if not, write to the Free Software
// Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.
// 
// Correspondence concerning GBT software should be addressed as follows:
//       GBT Operations
//       National Radio Astronomy Observatory
//       P. O. Box 2
//       Green Bank, WV 24944-0002 USA

package edu.nrao.dss.client.util.dssgwtcal;

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
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.ComplexPanel;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.Widget;

/*
 * The purpose of this Class is to stick text up on the calendar.
 * A specific implementation is the listing of 
 * the score in each quarter.  This was created by adapting the
 * Appointment classes until we got it to work.
 * Anybody understand *how* it works?
 */

public class Label extends Composite implements LabelInterface {

	class Div extends ComplexPanel { 
		
		public Div() {
			
			setElement(DOM.createDiv());
		}

		@Override
		public void add(Widget w) {
			super.add(w, getElement());
		}
	}

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


	public Label() {

		initWidget(mainPanel);

		mainPanel.setStylePrimaryName("gwt-label");
		headerPanel.setStylePrimaryName("header");
		bodyPanel.setStylePrimaryName("body");

		mainPanel.add(headerPanel);
		mainPanel.add(bodyPanel);
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

	public int compareTo(LabelInterface appt) {
		// -1 0 1
		// less, equal, greater
		int compare = this.getStart().compareTo(appt.getStart());

		if (compare == 0) {
			compare = appt.getEnd().compareTo(this.getEnd());
		}

		return compare;
	}
	
}