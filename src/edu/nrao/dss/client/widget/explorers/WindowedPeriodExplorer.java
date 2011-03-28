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
import edu.nrao.dss.client.widget.GroupPeriodDlg;
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

    // TODO: should we make this a static variable rather then a method?
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
		GroupPeriodDlg dlg = new GroupPeriodDlg(period, pg);
	}	
}	
	