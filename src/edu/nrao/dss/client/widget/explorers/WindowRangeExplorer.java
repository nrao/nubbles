package edu.nrao.dss.client.widget.explorers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.extjs.gxt.ui.client.data.BaseModelData;
import com.extjs.gxt.ui.client.data.BasePagingLoadResult;
import com.extjs.gxt.ui.client.widget.form.CheckBox;
import com.extjs.gxt.ui.client.widget.grid.CellEditor;
import com.extjs.gxt.ui.client.widget.grid.CheckColumnConfig;
import com.extjs.gxt.ui.client.widget.grid.ColumnConfig;
import com.extjs.gxt.ui.client.widget.grid.ColumnModel;
import com.google.gwt.http.client.RequestBuilder;

import edu.nrao.dss.client.data.WindowRangeType;
import edu.nrao.dss.client.util.DynamicHttpProxy;
import edu.nrao.dss.client.widget.PeriodGroupInfoPanel;
import edu.nrao.dss.client.widget.form.DateEditField;
import edu.nrao.dss.client.widget.form.DisplayField;

public class WindowRangeExplorer extends Explorer {
	
	private int windowId;
	private String sessionHandle;
	private List<ColumnConfig> configs;
	// the parent panel who needs to be updated frequently
	protected PeriodGroupInfoPanel pg;
	
	public WindowRangeExplorer(int windowId, String sessionHandle) {
		super("/windowRanges", "", new WindowRangeType(columnTypes));
	    this.windowId = windowId;
	    this.sessionHandle = sessionHandle;    
		setShowColumnsMenu(false);
		setAutoHeight(true);
		setCreateFilterToolBar(false);
		initLayout(initColumnModel(), true);
		filterByWindow(windowId);
	    setHeaderVisible(true);
	    setHeading("Window Ranges");
	}
	
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
	
	// make sure we pull in only ranges belonging to this window
	private void filterByWindow(int id) {
		String url = getRootURL() + "?filterWindowId=" + Integer.toString(id);
		RequestBuilder builder = new RequestBuilder(RequestBuilder.GET, url);
		DynamicHttpProxy<BasePagingLoadResult<BaseModelData>> proxy = getProxy();
		proxy.setBuilder(builder);
	}
	
	// override Explorer's loadData so that when it get's called in the parent class,
	// we get ranges for just this window 
	public void loadData() {
		filterByWindow(windowId);
		loader.load(0, getPageSize());
	}	
	
	// make sure newly added ranges belong to this window
	protected void addRecord(HashMap<String, Object> fields) {
		fields.put("window_id", windowId);
		//fields.put("handle", sessionHandle);
		super.addRecord(fields);
	}
	
	// what gets displayed/edited, and how?
	private static final ColumnType[] columnTypes = {
	    new ColumnType("start",                  "Start",           70, false, DateEditField.class),
	    new ColumnType("duration",               "Days",            70, false, Integer.class),
	    new ColumnType("end",                    "End",             70, false, DisplayField.class)
	};	
	
	// who gets updates when changes are made?
	public void registerObservers(PeriodGroupInfoPanel pg) {
	    this.pg = pg;
	}
	
	// reload all the window info when a change gets made to the window range
	public void updateObservers() {
		pg.getPeriodGroup();
	}
	
}
