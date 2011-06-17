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

package edu.nrao.dss.client.widget.explorers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.extjs.gxt.ui.client.data.BaseModelData;
import com.extjs.gxt.ui.client.data.BasePagingLoadResult;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.widget.button.SplitButton;
import com.extjs.gxt.ui.client.widget.form.CheckBox;
import com.extjs.gxt.ui.client.widget.form.SimpleComboValue;
import com.extjs.gxt.ui.client.widget.grid.CellEditor;
import com.extjs.gxt.ui.client.widget.grid.CheckColumnConfig;
import com.extjs.gxt.ui.client.widget.grid.ColumnConfig;
import com.extjs.gxt.ui.client.widget.grid.ColumnModel;
import com.google.gwt.core.client.GWT;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.json.client.JSONObject;

import edu.nrao.dss.client.Period;
import edu.nrao.dss.client.widget.PeriodSummaryDlg;
import edu.nrao.dss.client.widget.form.DateEditField;
import edu.nrao.dss.client.widget.form.DisplayField;
import edu.nrao.dss.client.widget.form.TimeField;

// This class is responsible for handling CRUD on the periods of a given Window.
// This class breaks the pattern that we'd had so far:
// One type of  Explorer -> REST url -> one type of Nell Resource.
// Here we create a different explorer that shares the PeriodResource w/ the PeriodExplorer.
// The meat of the functionality is in the extended abstract parent class.

public class WindowedPeriodExplorer extends GroupPeriodExplorer {
	
public WindowedPeriodExplorer(int periodGroupId, String sessionHandle) {
		super(periodGroupId, sessionHandle, getColumnTypes());
		setHeaderVisible(true);
		setHeading("Window Periods");
	}

    // Note: should we make this a static variable rather then a method?
    // Here's where we distinguish Windowed from Elective Periods
    protected static ColumnType[] getColumnTypes() {
    	ColumnType[] columnTypes = {
		new ColumnType("handle",                "Session",               250,false, DisplayField.class),
       	new ColumnType("state",                 "S",                     20, false, DisplayField.class),
        new ColumnType("date",                  "Day",                   70, false, DateEditField.class),
        new ColumnType("time",                  "Time",                  40, false, TimeField.class),
        new ColumnType("lst",                   "LST",                   55, false, DisplayField.class),
        new ColumnType("duration",              "Duration",              55, false, Double.class),
        new ColumnType("time_billed",           "Billed",                55, false, DisplayField.class),
        new ColumnType("wdefault",              "Default?",              55, false, Boolean.class)
	    };	
    	return columnTypes;
    }	

	// make sure newly added periods belong to this window & session
	protected void addRecord(HashMap<String, Object> fields) {
		fields.put("window_id", periodGroupId);
		fields.put("handle", sessionHandle);
		super.addRecord(fields);
	}
	
	@Override
	protected String getFilterUrl(int id) {
		return getRootURL() + "?filterWnd=" + Integer.toString(id) + "&scores=false";
	}
	
	@Override
	protected void showDlg(Period period) {
		PeriodSummaryDlg dlg = new PeriodSummaryDlg(period, null, pg);
		dlg.notForScheduling();
		dlg.show();
	}	
}	
	