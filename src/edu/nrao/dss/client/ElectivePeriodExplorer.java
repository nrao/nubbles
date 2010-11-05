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
import com.google.gwt.core.client.GWT;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.json.client.JSONObject;

// This class is responsible for handling CRUD on the periods of a given Window.
// This class breaks the pattern that we'd had so far:
// One type of  Explorer -> REST url -> one type of Nell Resource.
// Here we create a different explorer that shares the PeriodResource w/ the PeriodExplorer.

public class ElectivePeriodExplorer extends Explorer {
	
	private int electiveId;
	private String sessionHandle;
	
	private ElectiveInfoPanel ep;
	
	public ElectivePeriodExplorer(int electiveId, String sessionHandle) {
		super("/periods/UTC", new PeriodType(columnTypes));
	    this.electiveId = electiveId;
	    this.sessionHandle = sessionHandle;    
		setShowColumnsMenu(false);
		setAutoHeight(true);
		setCreateFilterToolBar(false);
		initLayout(initColumnModel(), true);
		filterByElective(electiveId);
		viewItem.setVisible(true);
	}
	
	// make sure we pull in only periods belonging to this elective
	private void filterByElective(int electiveId) {
		String url = getRootURL() + "?filterElc=" + Integer.toString(electiveId); //filtersURL;
		RequestBuilder builder = new RequestBuilder(RequestBuilder.GET, url);
		DynamicHttpProxy<BasePagingLoadResult<BaseModelData>> proxy = getProxy();
		proxy.setBuilder(builder);
	}
	
	// override this so that when it get's called in the parent class, we get periods for just this elective 
	public void loadData() {
		filterByElective(electiveId);
		loader.load(0, getPageSize());
	}	
	
	// shit I cut and past from other explorers - don't ask me, I just work here.
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
	
	// when the view button gets pressed, show the Period Summary Dialog.
	public void viewObject() {
		// get the id of the period selected
		BaseModelData bd = grid.getSelectionModel().getSelectedItem();
		if (bd == null) {
			return;
		}
		Double bid = bd.get("id");
		int periodId = bid.intValue();
		// use this to get it's JSON, which includes things like time accounting
	    JSONRequest.get("/periods/UTC/" + Integer.toString(periodId) , new JSONCallbackAdapter() {
            @Override
            public void onSuccess(JSONObject json) {
            	// display the dialog
             	Period period = Period.parseJSON(json.get("period").isObject());
        		ElectivePeriodDlg dlg = new ElectivePeriodDlg(period, ep);	
            }
        });		
	}
	
	public void viewPeriodSummaryDlg(JSONObject jsonPeriod) {
		
	}
	
	public ColumnConfig getSessionConfig() {
		return configs.get(0);
	}
	
	private List<ColumnConfig> configs;

	private static final ColumnType[] columnTypes = {
		new ColumnType("handle",                "Session",               250,false, DisplayField.class),
       	new ColumnType("state",                 "S",                     20, false, DisplayField.class),
        new ColumnType("date",                  "Day",                   70, false, DateEditField.class),
        new ColumnType("time",                  "Time",                  40, false, TimeField.class),
        new ColumnType("lst",                   "LST",                   55, false, DisplayField.class),
        new ColumnType("duration",              "Duration",              55, false, Double.class),
        new ColumnType("time_billed",           "Billed",                55, false, DisplayField.class),
//        new ColumnType("wdefault",              "Default?",              55, false, Boolean.class)
	};	

	public void registerObservers(ElectiveInfoPanel ep) {
		    this.ep = ep;
	}

	// reload all the elective info when a change gets made to the elective's periods
	public void updateObservers() {
		ep.getElective();
	}
	
	// make sure newly added periods belong to this elective & session
	protected void addRecord(HashMap<String, Object> fields) {
		fields.put("elective_id", electiveId);
		fields.put("handle", sessionHandle);
		super.addRecord(fields);
	}
	
}	
	