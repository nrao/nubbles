package edu.nrao.dss.client.widget;

import java.util.HashMap;

import com.extjs.gxt.ui.client.Style.Orientation;
import com.extjs.gxt.ui.client.Style.VerticalAlignment;
import com.extjs.gxt.ui.client.event.BaseEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.util.Margins;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.CheckBox;
import com.extjs.gxt.ui.client.widget.form.FormPanel;
import com.extjs.gxt.ui.client.widget.form.NumberField;
import com.extjs.gxt.ui.client.widget.form.TextField;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.extjs.gxt.ui.client.widget.layout.RowData;
import com.extjs.gxt.ui.client.widget.layout.RowLayout;
import com.extjs.gxt.ui.client.widget.layout.TableData;
import com.extjs.gxt.ui.client.widget.layout.TableLayout;
import com.google.gwt.core.client.GWT;
import com.google.gwt.json.client.JSONObject;

import edu.nrao.dss.client.Period;
import edu.nrao.dss.client.TimeAccounting;
import edu.nrao.dss.client.util.JSONCallbackAdapter;
import edu.nrao.dss.client.util.JSONRequest;

public class PeriodSummaryPanel extends ContentPanel {
	
	private Period period;
	private TextField<String> label = new TextField<String>();
	private TextField<String> start = new TextField<String>();
	private TextField<String> dur = new TextField<String>();
	private NumberField hscore = new NumberField();
	private NumberField cscore = new NumberField();
	private CheckBox backup = new CheckBox();
	private CheckBox moc_ack = new CheckBox();
	private TextField<String> state = new TextField<String>();
	private PeriodTimeAccountPanel ta = new PeriodTimeAccountPanel();
	private Button save = new Button();
	
    public PeriodSummaryPanel(Period p) {
    	period = p;
    	initLayout();
    }
    
    private void initLayout() {
    	
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
        periodForm.setBorders(false);
        periodForm.setBodyBorder(false);
        
    	// field per attribute, roughly:
        setReadOnly("Name", label);
    	setReadOnly("Start", start);
    	setReadOnly("Duration (Hrs)", dur);
        
    	periodForm.add(label);
    	periodForm.add(start);
    	periodForm.add(dur);
    	
    	moc_ack.setFieldLabel("MOC Acknowledged");
    	periodForm.add(moc_ack);
    	moc_ack.addListener(Events.OnClick, new Listener<BaseEvent>() {
    		public void handleEvent(BaseEvent be) {
    			//GWT.log("Updating period's moc_ack field", null);
    			String url = "/scheduler/period/" + Integer.toString(period.getId()) + "/toggle_moc";
    			HashMap<String, Object> keys = new HashMap<String, Object>();
    			JSONRequest.post(url, keys,
    					new JSONCallbackAdapter() {
    						public void onSuccess(JSONObject json) {
    						}
    					});		
    		}
    	});
    	
    	lc.add(periodForm, td);
    	
    	FormPanel periodForm2 = new FormPanel();
    	periodForm2.setHeading("Period Form2");
    	periodForm2.setHeaderVisible(false);
    	periodForm2.setBorders(false);
    	periodForm2.setBodyBorder(false);
    	
    	// score
    	hscore.setFieldLabel("Historical Score");
    	hscore.setReadOnly(true);
    	cscore.setFieldLabel("Current Score");
    	cscore.setReadOnly(true);
    	// TODO
    	hscore.setStyleAttribute("color", "grey");
    	periodForm2.add(hscore);
    	cscore.setStyleAttribute("color", "grey");
    	periodForm2.add(cscore);
    	
    	// backup 
    	backup.setFieldLabel("Backup");
    	backup.setReadOnly(true);
    	// TODO
    	backup.setStyleAttribute("color", "grey");
    	periodForm2.add(backup);
    	
    	setReadOnly("State", state);
    	periodForm2.add(state);
    	
    	// what else?
    	
    	// save the changes?
    	//Button save = new Button();
    	save.setText("Save Period Time Accounting");
    	save.addListener(Events.OnClick, new Listener<BaseEvent>() {
    		public void handleEvent(BaseEvent be) {
    			//GWT.log("Save!", null);
    			ta.sendUpdates();
    		}
    	});
        periodForm2.add(save);
        
    	lc.add(periodForm2, td);
    	
    	add(lc);
    	add(ta);
    	
    	setValues(period);
    	
    	// Time Accounting get's its own form
    	ta.setHeading("Period Time Accounting");
    	ta.setPeriod(period);
    }
    
    public void setSaveButtonVisible(boolean visible) {
    	save.setVisible(visible);
    }
    
    public void updatePeriodForm(int periodId) {
    	// get this period from the server and populate the form
        //GWT.log("updatePeriodForm", null);
        // TODO - should pick up timezone from Schedule
    	JSONRequest.get("/scheduler/periods/UTC/" + Integer.toString(periodId)
    		      , new JSONCallbackAdapter() {
    		public void onSuccess(JSONObject json) {
            	// JSON period -> JAVA period
             	Period period = Period.parseJSON(json.get("period").isObject());
             	setPeriod(period);
                //GWT.log("period onSuccess", null);          
    		}
    	});    		
    	
    }

    private void setReadOnly(String label, TextField<String> tf) {
    	tf.setFieldLabel(label);
    	tf.setReadOnly(true);
    	// TODO: read-only using background color?
    	tf.setStyleAttribute("color", "grey");
    }
    
	public void setParent(TimeAccounting p) {
		ta.setParent(p);
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
        	hscore.setValue(period.getHScore());
        	cscore.setValue(period.getCScore());
        	moc_ack.setValue(period.getMocAck());
        	backup.setValue(period.isBackup());
        	state.setValue(period.getState());
        }
    	
    }
    
    public boolean hasChanged() {
    	return ta.hasChanged();
    }
}
