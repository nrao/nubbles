package edu.nrao.dss.client;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;
import java.util.TreeMap;
import java.util.List;

import com.extjs.gxt.ui.client.Style.Orientation;
import com.extjs.gxt.ui.client.Style.Scroll;
import com.extjs.gxt.ui.client.Style.VerticalAlignment;
import com.extjs.gxt.ui.client.util.Margins;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.form.FormPanel;
import com.extjs.gxt.ui.client.widget.form.NumberField;
import com.extjs.gxt.ui.client.widget.form.TextArea;
import com.extjs.gxt.ui.client.widget.layout.RowData;
import com.extjs.gxt.ui.client.widget.layout.RowLayout;
import com.extjs.gxt.ui.client.widget.layout.TableData;
import com.extjs.gxt.ui.client.widget.layout.TableLayout;
import com.google.gwt.core.client.GWT;
import com.google.gwt.json.client.JSONObject;

public class PeriodTimeAccountPanel extends TimeAccountingPanel {
	
	private Period period;
	private TimeAccounting parent; // for callbacks
	
//	protected void initLayout() {
//	    super.initLayout();	
//	    
//	    // add the save bar below the description 
//	}
	
	public void setPeriod(Period p) {
		period = p;
		setValues(p);
	}
	
	private void setValues(Period p) {
		GWT.log("PeriodTimeAccountPanel.setValues", null);
		if (p != null) {
			GWT.log(String.valueOf(p.getScheduled()), null);
			scheduled.setValue(p.getScheduled());
			observed.setValue(p.getObserved());
			timeBilled.setValue(p.getBilled());
			unaccounted.setValue(p.getUnaccounted());
			notBillable.setValue(p.getNot_billable());
			shortNotice.setValue(p.getShort_notice());
			lt.setValue(p.getLost_time());
			ltw.setValue(p.getLost_time_weather());
			ltr.setValue(p.getLost_time_rfi());
			lto.setValue(p.getLost_time_other());
			os.setValue(p.getOther_session());
			osw.setValue(p.getOther_session_weather());
			osr.setValue(p.getOther_session_rfi());
			oso.setValue(p.getOther_session_other());
			desc.setValue(p.getDescription());
		}
	}
	
	public void sendUpdates() {
		
		if (period == null) {
			GWT.log("sendUpdates has null period", null);
			return;
		}
		
		// send all the non-derived values to the server so that time accounting
		// for the entire project can be updated
		// 1. update the period object
        GWT.log("setting period values", null);
        
        // watch out for allowed null descriptions in the DB
        if (desc.getValue() == null) {
        	period.setDescription("");
        } else {
        	period.setDescription(desc.getValue());
        }
		period.setDescription(desc.getValue());
		period.setScheduled(scheduled.getValue().doubleValue());
		period.setNot_billable(notBillable.getValue().doubleValue());
		period.setShort_notice(shortNotice.getValue().doubleValue());
		period.setLost_time_weather(ltw.getValue().doubleValue());
		period.setLost_time_rfi(ltr.getValue().doubleValue());
		period.setLost_time_other(lto.getValue().doubleValue());
		period.setOther_session_weather(osw.getValue().doubleValue());
		period.setOther_session_rfi(osr.getValue().doubleValue());
		period.setOther_session_other(oso.getValue().doubleValue());
		
		// 2. convert this info to JSON like stuff
		HashMap <String, Object> keys = period.toHashMap();
		GWT.log("setting keys", null);
		
		// 3. send the json
		JSONRequest.post("/period/" + Integer.toString(period.getId()) + "/time_accounting", keys,
				new JSONCallbackAdapter() {
					public void onSuccess(JSONObject json) {
						GWT.log("periods/time_accounting onSuccess", null);
                        // TODO: now get all the project level time accounting again, since
						// it all may have changed.
						if (parent != null) {
							GWT.log("calling parent.setTimeAcctFromJSON", null);
							parent.setTimeAccountingFromJSON(json);
						}
						
					}
				});		
	}
	
	public void setParent(TimeAccounting p) {
		parent = p;
	}
	
	protected void setFieldAttributes() {
		
		super.setFieldAttributes();
		
		// they almost all can be set!
		setEditable(scheduled);
		setEditable(notBillable);
		setEditable(shortNotice);
		setEditable(ltw);
		setEditable(ltr);
		setEditable(lto);
		setEditable(osw);
		setEditable(osr);
		setEditable(oso);
		
	}
}

