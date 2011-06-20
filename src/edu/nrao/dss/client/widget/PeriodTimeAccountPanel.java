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

package edu.nrao.dss.client.widget;

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

import edu.nrao.dss.client.Period;
import edu.nrao.dss.client.TimeAccounting;
import edu.nrao.dss.client.util.JSONCallbackAdapter;
import edu.nrao.dss.client.util.JSONRequest;

public class PeriodTimeAccountPanel extends TimeAccountingPanel {
	
	private Period period;
	private TimeAccounting parent; // for callbacks
	
	public void setPeriod(Period p) {
		period = p;
		setValues(p);
	}
	
	private void setValues(Period p) {
		//GWT.log("PeriodTimeAccountPanel.setValues", null);
		if (p != null) {
			
			// setting the value also entails maintaining state
			setValue(scheduled, p.getScheduled());
			setValue(observed, p.getObserved());
			setValue(timeBilled, p.getBilled());
			setValue(unaccounted, p.getUnaccounted());
			setValue(notBillable, p.getNot_billable());
			setValue(shortNotice, p.getShort_notice());
			setValue(lt, p.getLost_time());
			setValue(ltw, p.getLost_time_weather());
			setValue(ltr, p.getLost_time_rfi());
			setValue(lto, p.getLost_time_other());
			setValue(lp, p.getLost_time_bill_project());
			setValue(os, p.getOther_session());
			setValue(osw, p.getOther_session_weather());
			setValue(osr, p.getOther_session_rfi());
			setValue(oso, p.getOther_session_other());
			
			// Description is the only field not a NumberField
			// so maintain it's state w/ it's own method
			setDescription(p.getDescription());
		}
	}
	
	public void setValue(NumberField nf, double value) {
		// getting the fields in sync w/ the database should be reflected 
		// in the fields state
	    nf.setValue(value);
	    nf.setOriginalValue(value);
	    if ((!nf.isReadOnly()) && nf.isRendered() ) {
	    	nf.el().firstChild().setStyleAttribute("color", "black");
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
        widgetsToPeriod();
		
		// 2. convert this info to JSON like stuff
		HashMap <String, Object> keys = period.toHashMap();
		
		// 3. send the json
		JSONRequest.post("/scheduler/period/" + Integer.toString(period.getId()) + "/time_accounting", keys,
				new JSONCallbackAdapter() {
					public void onSuccess(JSONObject json) {
						if (parent != null) {
							// changing the time accounting for a period makes changes that bubble up
							// to the session and project.  So if there are other panels to reflect that
							parent.setTimeAccountingFromJSON(json);
						}
						// now, make sure we update ourselves
						if (period != null) {
							updatePeriodForm(period.getId());
						}	
						
					}
				});		
	}
	
	// data in the wigets gets moved to the period object.
    public void widgetsToPeriod() {
		period.setDescription(getDescription());
		period.setScheduled(scheduled.getValue().doubleValue());
		period.setNot_billable(notBillable.getValue().doubleValue());
		period.setShort_notice(shortNotice.getValue().doubleValue());
		period.setLost_time_weather(ltw.getValue().doubleValue());
		period.setLost_time_rfi(ltr.getValue().doubleValue());
		period.setLost_time_other(lto.getValue().doubleValue());
		period.setLost_time_bill_project(lp.getValue().doubleValue());
		period.setOther_session_weather(osw.getValue().doubleValue());
		period.setOther_session_rfi(osr.getValue().doubleValue());
		period.setOther_session_other(oso.getValue().doubleValue());		
	}

	public void updatePeriodForm(int periodId) {
    	// Get this period from the server and populate the form.
        // Note that we are using UTC, regardless of the timezone that may be
    	// being used in this context.
    	JSONRequest.get("/scheduler/periods/UTC/" + Integer.toString(periodId)
    		      , new JSONCallbackAdapter() {
    		public void onSuccess(JSONObject json) {
            	// JSON period -> JAVA period
             	Period period = Period.parseJSON(json.get("period").isObject());
             	setPeriod(period);
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
		setEditable(lp);
		
	}
	
	public boolean hasChanged() {
		// check to see if any field has changed
		if (hasChanged(scheduled) || 
			hasChanged(notBillable) ||
			hasChanged(shortNotice) ||
			hasChanged(ltw) ||
			hasChanged(ltr) ||
			hasChanged(lto) ||
			hasChanged(osw) ||
			hasChanged(osr) ||
			hasChanged(oso) ||
			hasChanged(lp)  ||
			hasChanged(desc)
			) {
			return true;
		} else {
			return false;
		}	
	}	
	
	// used mostly for unit tests
    public Period getPeriod() {
    	return period;
    }
	
}

