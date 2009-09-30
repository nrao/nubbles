package edu.nrao.dss.client;

import com.extjs.gxt.ui.client.Style.Orientation;
import com.extjs.gxt.ui.client.util.Margins;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.form.FormPanel;
import com.extjs.gxt.ui.client.widget.form.NumberField;
import com.extjs.gxt.ui.client.widget.form.TextField;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.extjs.gxt.ui.client.widget.layout.RowData;
import com.extjs.gxt.ui.client.widget.layout.RowLayout;

public class PeriodSummaryPanel extends ContentPanel {
	
	private Period period;
	private TextField label = new TextField();
	private TextField start = new TextField();
	private TextField dur = new TextField();
	private PeriodTimeAccountPanel ta = new PeriodTimeAccountPanel(null);
	
    public PeriodSummaryPanel(Period p) {
    	period = p;
    	initLayout();
    }
    
    private void initLayout() {
    	
    	setLayout(new FitLayout());
    	
    	setHeading("Period Summary Panel");
    	
    	setLayout(new RowLayout(Orientation.VERTICAL));
    	FormPanel periodForm = new FormPanel();
    	periodForm.setHeading("Period Form");
    	
    	// field per attribute, roughly:
    	// name
    	//TextField label = new TextField();
    	label.setFieldLabel("Name");
    	//label.setValue(period.getHandle());
    	label.setReadOnly(true);
    	periodForm.add(label);
    	
    	// start
    	//TextField start = new TextField();
    	start.setFieldLabel("Start (UTC)");
    	//start.setValue(period.getStartString());
    	start.setReadOnly(true);
    	periodForm.add(start);
    	
    	// duration
    	//TextField dur = new TextField();
    	dur.setFieldLabel("Duration (Hrs)");
    	//dur.setValue(period.getDurationString());
    	dur.setReadOnly(true);
    	periodForm.add(dur);
    	
    	// score
    	// backup 
    	// what else

    	setValues(period);
    	
    	// Time Accounting get's its own form
    	//PeriodTimeAccountPanel ta = new PeriodTimeAccountPanel(period);
    	ta.setPeriod(period);
    	periodForm.add(ta);
    	
    	add(periodForm, new RowData(1, -1, new Margins(1)));    	
    }
    
    public void setPeriod(Period period) {
    	setValues(period);
    	ta.setPeriod(period);
    }
    
    private void setValues(Period period) {
        if (period != null) {
        	label.setValue(period.getHandle());
        	start.setValue(period.getStartString());
        	dur.setValue(period.getDurationString());
        }
    	
    }
}
