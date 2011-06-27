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

import java.util.Date;
import java.util.HashMap;

import com.extjs.gxt.ui.client.Style.Orientation;
import com.extjs.gxt.ui.client.event.BaseEvent;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.util.Margins;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.Dialog;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.CheckBox;
import com.extjs.gxt.ui.client.widget.form.DateField;
import com.extjs.gxt.ui.client.widget.form.FormPanel;
import com.extjs.gxt.ui.client.widget.form.NumberField;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.extjs.gxt.ui.client.widget.layout.RowData;
import com.extjs.gxt.ui.client.widget.layout.RowLayout;

import com.google.gwt.core.client.GWT;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.json.client.JSONObject;
// comment
import com.google.gwt.json.client.JSONString;

import edu.nrao.dss.client.util.JSONCallbackAdapter;
import edu.nrao.dss.client.util.JSONRequest;
import edu.nrao.dss.client.widget.explorers.ElectivePeriodExplorer;

// This class maps directly to a single elective object on the server side.  It replaces 
// what a single row in the elective explorer used to cover, before multiple periods and date 
// ranges were introduced.

// Note: to highlight unsaved changes see TimeAccounting for one way to do this.

public class ElectiveInfoPanel extends PeriodGroupInfoPanel {
	
	
	public ElectiveInfoPanel(JSONObject json, String url, String groupPeriodType) {
		super(json, url, groupPeriodType);
	}

	// elective attributes not common to windows
	private CheckBox cmp;
	
	private ElectivePeriodExplorer epe;
	
	// translate the json for an elective to the class attributes
	protected void translateJson(JSONObject winJson) {
		String startDate, endDate;
		
		handle = winJson.get("handle").isString().stringValue();
		
		id = (int) winJson.get("id").isNumber().doubleValue();
		
		complete = winJson.get("complete").isBoolean().booleanValue();
		
		// what are the dates that this elective spans?
		JSONString firstPeriod = winJson.get("firstPeriod").isString();
		if (firstPeriod != null) {
    		String startStr = firstPeriod.stringValue(); 
	    	Date start = DateTimeFormat.getFormat("yyyy-MM-dd HH:mm:ss").parse(startStr);
		    startDate = DateTimeFormat.getFormat("yyyy-MM-dd").format(start);
		} else {
			startDate = "None";
		}
		
		JSONString lastPeriod = winJson.get("lastPeriod").isString();
		if (lastPeriod != null) {
    		String endStr = lastPeriod.stringValue(); 
    		Date end = DateTimeFormat.getFormat("yyyy-MM-dd HH:mm:ss").parse(endStr);
	    	endDate = DateTimeFormat.getFormat("yyyy-MM-dd").format(end);
		} else {
			endDate = "None";
		}
		
		// the header is a summary: [date range] time, complete (id)
		header = "Elective [" + startDate + " - " + endDate + "] (" + Integer.toString(id) + "): ";

	}
	
	// class attributes -> widget attributes
	@Override
	protected void loadPeriodGroup() {
	    cmp.setValue(complete);
	    updateHeading();		
	}

	@Override
	protected void updateGroupPeriod(JSONObject json) {
    	JSONObject winJson = json.get("elective").isObject();
    	translateJson(winJson);
    	loadPeriodGroup();
    	// update the elective periods
    	epe.loadData();		
	}

	@Override
	protected void savePeriodGroup() {
		// put the widget values into the hash to pass down in the POST
		HashMap<String, Object> keys = new HashMap<String, Object>();
		keys.put("_method", "put");
		keys.put("complete", cmp.getValue());
		keys.put("handle", handle);
	    JSONRequest.post("/scheduler/electives/" + Integer.toString(id), keys, new JSONCallbackAdapter() {
	            @Override
	            public void onSuccess(JSONObject json) {
	            	// get back from the server this elective & display it again
	            	getPeriodGroup();
	            }
	    });			
	}

	// Sets up the fields exclusive to Electives
	// This is called from the parents initLayout()
	@Override
	protected void initFormFields(FormPanel fp) {
	    cmp = new CheckBox();
	    cmp.setFieldLabel("Complete");
	    cmp.setValue(complete);
	    fp.add(cmp);		
	}

	// Elective Panels need to use the ElectivePeriodExplorers
	// This is called from the parents initLayout()
	@Override
	protected void initGroupPeriodExplorer(FormPanel fp) {
	    epe = new ElectivePeriodExplorer(id, handle);
	    epe.registerObservers(this);
	    fp.add(epe);		
	}
	
}
