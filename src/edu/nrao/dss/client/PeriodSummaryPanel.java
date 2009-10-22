package edu.nrao.dss.client;

import com.extjs.gxt.ui.client.Style.Orientation;
import com.extjs.gxt.ui.client.Style.VerticalAlignment;
import com.extjs.gxt.ui.client.event.BaseEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.util.Margins;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.FormPanel;
import com.extjs.gxt.ui.client.widget.form.NumberField;
import com.extjs.gxt.ui.client.widget.form.TextField;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.extjs.gxt.ui.client.widget.layout.RowData;
import com.extjs.gxt.ui.client.widget.layout.RowLayout;
import com.extjs.gxt.ui.client.widget.layout.TableData;
import com.extjs.gxt.ui.client.widget.layout.TableLayout;
import com.google.gwt.core.client.GWT;

public class PeriodSummaryPanel extends ContentPanel {
	
	private Period period;
	private TextField label = new TextField();
	private TextField start = new TextField();
	private TextField dur = new TextField();
	private PeriodTimeAccountPanel ta = new PeriodTimeAccountPanel();
	
    public PeriodSummaryPanel(Period p) {
    	period = p;
    	initLayout();
    }
    
    private void initLayout() {
    	
    	//setLayout(new FitLayout());
    	
    	setHeading("Period Summary Panel");
    	
    	setLayout(new RowLayout(Orientation.VERTICAL));
    	
    	LayoutContainer lc = new LayoutContainer();
    	TableLayout tl = new TableLayout(2);
    	tl.setWidth("100%");
		TableData td = new TableData();
		td.setVerticalAlign(VerticalAlignment.TOP);    	
    	lc.setLayout(tl);
    	
    	FormPanel periodForm = new FormPanel();
    	periodForm.setHeading("Period Form");
        periodForm.setHeaderVisible(false);
        
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
    	
    	lc.add(periodForm, td);
    	
    	FormPanel periodForm2 = new FormPanel();
    	periodForm2.setHeading("Period Form2");
    	periodForm2.setHeaderVisible(false);
    	
    	
    	
    	// score
    	NumberField score = new NumberField();
    	score.setFieldLabel("Score");
    	//start.setValue(period.getStartString());
    	score.setReadOnly(true);
    	periodForm2.add(score);
    	
    	// backup 
    	// what else
    	Button save = new Button();
    	save.setText("Save");
    	save.addListener(Events.OnClick, new Listener<BaseEvent>() {
    		public void handleEvent(BaseEvent be) {
    			GWT.log("Save!", null);
    			// TODO: send back the time accounting json!!!
    			ta.sendUpdates();
    		}
    	});
        periodForm2.add(save);
        
    	lc.add(periodForm2, td);
    	
    	add(lc);
    	add(ta);
    	
    	setValues(period);
    	
    	// Time Accounting get's its own form
    	//PeriodTimeAccountPanel ta = new PeriodTimeAccountPanel(period);
    	ta.setHeading("Period Time Accounting");
    	ta.setPeriod(period);
    	//periodForm.add(ta);
    	
    	//add(periodForm, new RowData(1, -1, new Margins(1)));    	
    }
    
    public void setParent(TimeAccounting parent) {
    	ta.setParent(parent);
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
