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

package edu.nrao.dss.client;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Set;
import java.util.TreeSet;

import com.extjs.gxt.ui.client.Style.Orientation;
import com.extjs.gxt.ui.client.Style.Scroll;
import com.extjs.gxt.ui.client.event.BaseEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.util.Margins;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.layout.RowData;
import com.extjs.gxt.ui.client.widget.layout.RowLayout;
import com.google.gwt.core.client.GWT;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.FlexTable.FlexCellFormatter;

import edu.nrao.dss.client.data.RcvrScheduleData;
import edu.nrao.dss.client.util.JSONCallbackAdapter;
import edu.nrao.dss.client.util.JSONRequest;
import edu.nrao.dss.client.widget.RcvrChangePanel;
import edu.nrao.dss.client.widget.RcvrSchdGridPanel;

public class ReceiverSchedule extends ContentPanel {
	
	//04/11/2009
    private static final DateTimeFormat DATE_FORMAT = DateTimeFormat.getFormat("MM/dd/yyyy");
    private int count = 0;

    // displays the rx calendar, along with the controls for it
    private RcvrSchdGridPanel grid = new RcvrSchdGridPanel();
    
    // widgets for editing the rx calendar
    private RcvrChangePanel change = new RcvrChangePanel();
    
	public ReceiverSchedule() {
		initLayout();

		// make the first call to the server to get the rx calendar
		grid.setupCalendar();

	}
	
	private void initLayout() {

		setLayout(new RowLayout(Orientation.VERTICAL));

		setScrollMode(Scroll.AUTO);
		setBorders(false);
		setHeaderVisible(false);
		
        add(grid);
        add(change, new RowData(1, -1, new Margins(4)));
       
        grid.setParent(this);
        change.setParent(this);
	}	

	// make the call to the server to retrieve the rx calendar
	public void getRcvrSchedule(Date start, int numDays, boolean showMaintenanceDays) {

		String startStr = DateTimeFormat.getFormat("yyyy-MM-dd").format(start) + " 00:00:00";
		
		HashMap<String, Object> keys = new HashMap<String, Object>();
		keys.put("startdate", startStr);
		keys.put("duration", numDays);
		JSONRequest.get("/scheduler/receivers/schedule", keys  
			      , new JSONCallbackAdapter() {
			public void onSuccess(JSONObject json) {
				jsonToRcvrSchedule(json);
			}
		});
	}

	public void updateRcvrSchedule() {
	    grid.getRcvrSchedule();	
	}
	
	// converts the JSON representation of the rx calendar (and related info) into a Java representation
	// and passes this on to widgets for display
	public void jsonToRcvrSchedule(JSONObject json) {
		
		RcvrScheduleData rsd = RcvrScheduleData.parseJSON(json);
		
		// populate drop-down widget for rx, and dates for the other drop-downs
		change.loadRcvrScheduleData(rsd);
		
		// use these to create the grid
		grid.loadRcvrScheduleData(rsd);
	}
}
