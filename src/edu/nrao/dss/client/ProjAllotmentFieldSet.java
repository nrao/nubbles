package edu.nrao.dss.client;

import com.extjs.gxt.ui.client.widget.form.FieldSet;
import com.extjs.gxt.ui.client.widget.form.NumberField;
import com.extjs.gxt.ui.client.widget.layout.FormLayout;
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


		proj_alloted.setFieldLabel("Alloted (Hrs)");
		add(proj_alloted);

		setReadOnlyField("Sess. alloted (Hrs)", proj_sess_alloted);
		add(proj_sess_alloted);
	}

	public void setValues(JSONObject time) {
		grade = time.get("grade").isNumber().doubleValue();
		String heading = "Grade: " + Double.toString(grade);
		setHeading(heading);
	    proj_alloted.setValue(time.get("total_time").isNumber().doubleValue());
	    proj_sess_alloted.setValue(time.get("sess_total_time").isNumber().doubleValue());
	}
	
	private void setReadOnlyField(String label, NumberField nf) {
	    nf.setFieldLabel(label);
	    nf.setReadOnly(true);
	    // TODO: use background color!
	    nf.setStyleAttribute("color", "grey");
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
