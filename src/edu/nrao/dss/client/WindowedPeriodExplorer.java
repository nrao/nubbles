package edu.nrao.dss.client;

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
import com.google.gwt.http.client.RequestBuilder;

// This class is responsible for handling CRUD on the periods of a given Window.
// This class breaks the pattern that we'd had so far:
// One type of  Explorer -> REST url -> one type of Nell Resource.
// Here we create a different explorer that shares the PeriodResource w/ the PeriodExplorer.

public class WindowedPeriodExplorer extends Explorer {
	
	private int windowId;
	
	public WindowedPeriodExplorer(int windowId) {
		super("/periods/UTC", new PeriodType(columnTypes));
	    this.windowId = windowId;	
		setShowColumnsMenu(false);
		setAutoHeight(true);
		setCreateFilterToolBar(false);
		initLayout(initColumnModel(), true);
		filterByWindow(windowId);
		
	}
	
	// make sure we pull in only periods belonging to this window
	private void filterByWindow(int windowId) {
		String url = getRootURL() + "?filterWnd=" + Integer.toString(windowId); //filtersURL;
		RequestBuilder builder = new RequestBuilder(RequestBuilder.GET, url);
		DynamicHttpProxy<BasePagingLoadResult<BaseModelData>> proxy = getProxy();
		proxy.setBuilder(builder);
	
	}
	
	// override this so that when it get's called in the parent class, we apply the appropriate filter 
	public void loadData() {
		filterByWindow(windowId);
		loader.load(0, getPageSize());
	}	
	
	// shit I cut and past from other explorers
	private ColumnModel initColumnModel() {
		configs = new ArrayList<ColumnConfig>();
		CheckColumnConfig checkColumn;
		for (ColumnType ct : columnTypes) {
			if (ct.getClasz() != Boolean.class) {
			    configs.add(new PeriodColConfig(ct.getId(), ct.getName(), ct.getLength(), ct.getClasz()));
			} else {
				checkColumn = new CheckColumnConfig(ct.getId(), ct.getName(), ct.getLength());
			    checkColumn.setEditor(new CellEditor(new CheckBox()));
			    configs.add(checkColumn);
			    checkBoxes.add(checkColumn);
			}
		}
	    return new ColumnModel(configs);
	}
	
	public ColumnConfig getSessionConfig() {
		return configs.get(0);
	}
	
	private List<ColumnConfig> configs;

	private static final ColumnType[] columnTypes = {
       	new ColumnType("state",                 "S",                     20, false, DisplayField.class),
        new ColumnType("date",                  "Day",                   70, false, DateEditField.class),
        new ColumnType("time",                  "Time",                  40, false, TimeField.class),
        new ColumnType("lst",                   "LST",                   55, false, DisplayField.class),
        new ColumnType("duration",              "Duration",              55, false, Double.class),
        new ColumnType("wdefault",              "Default?",              55, false, Boolean.class)
	};	


	// make sure newly added periods belong to this window
	protected void addRecord(HashMap<String, Object> fields) {
		fields.put("window_id", windowId);
		super.addRecord(fields);
	}
	
}	
	