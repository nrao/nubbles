package edu.nrao.dss.client;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;
import java.util.TreeMap;
import java.util.List;

import com.extjs.gxt.ui.client.Style.Orientation;
import com.extjs.gxt.ui.client.Style.Scroll;
import com.extjs.gxt.ui.client.util.Margins;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.form.FormPanel;
import com.extjs.gxt.ui.client.widget.form.NumberField;
import com.extjs.gxt.ui.client.widget.form.TextArea;
import com.extjs.gxt.ui.client.widget.layout.RowData;
import com.extjs.gxt.ui.client.widget.layout.RowLayout;
import com.google.gwt.core.client.GWT;

public class PeriodTimeAccountPanel extends FormPanel {
	
	private Period period;
    private ArrayList<String> keys = new ArrayList<String>();
	private HashMap<String, NumberField> numberFields = new HashMap<String, NumberField>();
	
	public PeriodTimeAccountPanel(Period p) {
		period = p;
		initLayout();
	}
	
	private void initLayout() {
		setHeading("Period Time Accounting");
		//setLayout(new RowLayout(Orientation.VERTICAL));
		//setScrollMode(Scroll.AUTO);
		
		// time accounting fields:
		TextArea desc = new TextArea();
		desc.setFieldLabel("Description");
		if (period != null) {
		    desc.setValue(period.getDescription());
		}    
		desc.setReadOnly(true);
		add(desc);
		
		// TODO: I really want a map that keeps the keys in the order in which they're put in
        //ArrayList<String> keys = new ArrayList<String>();
        keys.add("Scheduled");
        keys.add("Not Billable");
        keys.add("Short Notice");
        keys.add("Lost Time [LT]");
        keys.add("LT Weather");
        keys.add("LT RFI");
        keys.add("LT Other");
        keys.add("Other Session [OS]");
        keys.add("OS Weather");
        keys.add("OS RFI");
        keys.add("OS Other");
       
        HashMap<String, Double> fields = new HashMap<String, Double>();
        if (period != null) {
	        setValues(period);	
//	        fields.put(keys.get(0), period.getScheduled());
//	        fields.put(keys.get(1), period.getNot_billable());
//	        fields.put(keys.get(2), period.getShort_notice());
//	        fields.put(keys.get(3), period.getLost_time());
//	        fields.put(keys.get(4), period.getLost_time_weather());
//	        fields.put(keys.get(5), period.getLost_time_rfi());
//	        fields.put(keys.get(6), period.getLost_time_other());
//	        fields.put(keys.get(7), period.getOther_Session());
//	        fields.put(keys.get(8), period.getOther_session_weather());
//	        fields.put(keys.get(9), period.getOther_session_rfi());
//	        fields.put(keys.get(10),period.getOther_session_other());
	        
	        for (String key : keys) {
	        	GWT.log(key, null);
	        	addNumberField(key, fields.get(key));
	        }
        } else {
	        for (String key : keys) {
	        	GWT.log(key, null);
	        	addNumberField(key, null);
	        }
        	
        }
        

		
	}
	
	private void addNumberField(String label, Double value) {
		NumberField nb = new NumberField();
		nb.setFieldLabel(label + " (Hrs)");
		if (value != null) {
		    nb.setValue(value);
		}    
		nb.setReadOnly(true);
		add(nb);
		numberFields.put(label, nb);
	}
	
	public void setPeriod(Period p) {
	    period = p;
	    setValues(p);
	}
	
	private void setValues(Period p) {

	    if (period != null) {
		    	
		    numberFields.get(keys.get(0)).setValue(period.getScheduled());
		    numberFields.get(keys.get(1)).setValue(period.getNot_billable());
		    numberFields.get(keys.get(2)).setValue(period.getShort_notice());
		    numberFields.get(keys.get(3)).setValue(period.getLost_time());
		    numberFields.get(keys.get(4)).setValue(period.getLost_time_weather());
		    numberFields.get(keys.get(5)).setValue(period.getLost_time_rfi());
		    numberFields.get(keys.get(6)).setValue(period.getLost_time_other());
		    numberFields.get(keys.get(7)).setValue(period.getOther_Session());
		    numberFields.get(keys.get(8)).setValue(period.getOther_session_weather());
		    numberFields.get(keys.get(9)).setValue(period.getOther_session_rfi());
		    numberFields.get(keys.get(10)).setValue(period.getOther_session_other());
	    }
	
	}
}
