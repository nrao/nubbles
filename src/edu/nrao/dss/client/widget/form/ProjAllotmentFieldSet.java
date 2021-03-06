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

package edu.nrao.dss.client.widget.form;

import com.extjs.gxt.ui.client.event.BaseEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.widget.form.FieldSet;
import com.extjs.gxt.ui.client.widget.form.NumberField;
import com.extjs.gxt.ui.client.widget.layout.FormLayout;
import com.google.gwt.core.client.GWT;
import com.google.gwt.i18n.client.NumberFormat;
import com.google.gwt.json.client.JSONObject;


public class ProjAllotmentFieldSet extends FieldSet {

	private NumberField proj_alloted = new NumberField();
	private NumberField proj_sess_alloted = new NumberField();
	private double grade;
	
	public ProjAllotmentFieldSet() {
		initLayout();
	}
	
	private void initLayout() {
		setHeading("Grade");  
		setCollapsible(true);  
		   
		FormLayout layout1 = new FormLayout();  
		layout1.setLabelWidth(75);  
		setLayout(layout1);


		setEditableField("Alloted (Hrs)", proj_alloted);
		add(proj_alloted);

		setReadOnlyField("Sess. alloted (Hrs)", proj_sess_alloted);
		add(proj_sess_alloted);
	}

	public void setValues(JSONObject time) {
		grade = time.get("grade").isNumber().doubleValue();
		String heading = "Grade: " + Double.toString(grade);
		setHeading(heading);
	    proj_sess_alloted.setValue(time.get("sess_total_time").isNumber().doubleValue());
	    
	    // writable fields should reseet their state
	    double t = time.get("total_time").isNumber().doubleValue();
	    proj_alloted.setValue(t);
	    proj_alloted.setOriginalValue(t);
	    proj_alloted.el().firstChild().setStyleAttribute("color", "black");
	}
	
	private void setReadOnlyField(String label, NumberField nf) {
	    nf.setFieldLabel(label);
	    nf.setReadOnly(true);
	    // Note: this does not work!
	    nf.setStyleAttribute("color", "grey");
		nf.setFormat(NumberFormat.getFormat("#0.00"));
	}
	
	private void setEditableField(String label, NumberField nf) {
		nf.setFieldLabel(label);
		nf.setFormat(NumberFormat.getFormat("#0.00"));
		nf.setValidator(new DSSTimeValidator());
		nf.addListener(Events.Blur, new Listener<BaseEvent>() {
			@Override
			public void handleEvent(BaseEvent be) {
	            double value = ((NumberField) be.getSource()).getValue().doubleValue();
	            double orgvl = ((NumberField) be.getSource()).getOriginalValue().doubleValue();
	            String color = (orgvl == value) ? "black" : "red";
	            ((NumberField) be.getSource()).el().firstChild().setStyleAttribute("color", color);
//	            if (orgvl == value) {
//	            	((NumberField) be.getSource()).setStyleAttribute("color", "black");
//	            } else {
//	            	((NumberField) be.getSource()).setStyleAttribute("color", "red");
//	            }
			}			
    	});		
	}
	
	public double getGrade() {
		return grade;
	}
	
	public double getAllotment() {
		return proj_alloted.getValue().doubleValue();
	}

	public double getSessAllotment() {
		return proj_sess_alloted.getValue().doubleValue();
	}
	
}
