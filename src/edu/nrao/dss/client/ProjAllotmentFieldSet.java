package edu.nrao.dss.client;

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
	    proj_alloted.setStyleAttribute("color", "black");
	}
	
	private void setReadOnlyField(String label, NumberField nf) {
	    nf.setFieldLabel(label);
	    nf.setReadOnly(true);
	    // TODO: use background color!
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
	            GWT.log("Blur!", null);
	            double value = ((NumberField) be.getSource()).getValue().doubleValue();
	            double orgvl = ((NumberField) be.getSource()).getOriginalValue().doubleValue();
	            if (orgvl == value) {
	            	((NumberField) be.getSource()).setStyleAttribute("color", "black");
	            } else {
	            	((NumberField) be.getSource()).setStyleAttribute("color", "red");
	            }
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
