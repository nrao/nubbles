package edu.nrao.dss.client.widget.explorers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.extjs.gxt.ui.client.data.BaseModelData;
import com.extjs.gxt.ui.client.data.BasePagingLoadResult;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.button.SplitButton;
import com.extjs.gxt.ui.client.widget.form.CheckBox;
import com.extjs.gxt.ui.client.widget.form.DateField;
import com.extjs.gxt.ui.client.widget.form.SimpleComboValue;
import com.extjs.gxt.ui.client.widget.grid.CellEditor;
import com.extjs.gxt.ui.client.widget.grid.CheckColumnConfig;
import com.extjs.gxt.ui.client.widget.grid.ColumnConfig;
import com.extjs.gxt.ui.client.widget.grid.ColumnModel;
import com.google.gwt.core.client.GWT;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONObject;
import com.extjs.gxt.ui.client.widget.grid.ColumnModel;
import com.extjs.gxt.ui.client.widget.toolbar.ToolBar;

import edu.nrao.dss.client.data.WindowType;
import edu.nrao.dss.client.util.DynamicHttpProxy;
import edu.nrao.dss.client.util.JSONCallbackAdapter;
import edu.nrao.dss.client.util.JSONRequest;
import edu.nrao.dss.client.widget.AssignWindowPeriodDlg;
import edu.nrao.dss.client.widget.form.DateEditField;
import edu.nrao.dss.client.widget.form.DisplayField;
import edu.nrao.dss.client.widget.form.SessionField;




public class WindowExplorer extends Explorer {
	
    private AssignWindowPeriodDlg assignDlg;
	
	public WindowExplorer() {
		super("/scheduler/windows", "", new WindowType(columnTypes));
		initFilters();
		initLayout(initColumnModel(), true);
		updateSessionOptions();

	}

	protected void initLayout(ColumnModel cm) {
	    super.initLayout(cm, true);
	    
	    actionItem.setVisible(true);
	    actionItem.setText("Assign Period");
	    
	    assignDlg = new AssignWindowPeriodDlg();
	    assignDlg.hide();
	    
	}
	
	public void actionOnObject() {
	    //MessageBox.alert("got it", "dog", null);
		BaseModelData bd = getGrid().getSelectionModel().getSelectedItem();
		//String sessionId = (String) bd.get("id");
		double windowId = bd.get("id");	 
		String sessionHandle = bd.get("handle");
		
		GWT.log("Got Session ID: " + Double.toString(windowId), null);
		
		assignDlg.show(sessionHandle, (int) windowId);
	}
	
	private ColumnModel initColumnModel() {
		configs = new ArrayList<ColumnConfig>();
		CheckColumnConfig checkColumn;
		for (ColumnType ct : columnTypes) {
			if (ct.getClasz() != Boolean.class) {
				//GWT.log("WE: "+ct.getName(), null);				
			    configs.add(new WindowColConfig(ct.getId(), ct.getName(), ct.getLength(), ct.getClasz()));
			} else {
				checkColumn = new CheckColumnConfig(ct.getId(), ct.getName(), ct.getLength());
			    checkColumn.setEditor(new CellEditor(new CheckBox()));
			    configs.add(checkColumn);
			    checkBoxes.add(checkColumn);
			}
		}
	    return new ColumnModel(configs);
	}
	private void initFilters() {
		advancedFilters.add(initCombo("Session", new String[] {}, 120));
		advancedFilters.add(initCombo("Start", getDates(), 100));
		advancedFilters.add(initCombo("Days", getDays(), 50));
		initFilterAction();
	}
		
	private void initFilterAction() {
		filterAction = new SplitButton("Filter");
		filterAction.setMenu(initFilterMenu());
		filterAction.addSelectionListener(new SelectionListener<ButtonEvent>() {
			@Override
			public void componentSelected(ButtonEvent be){
				String filtersURL = "?";
				
				SimpleComboValue<String> value;
				String[] filterNames = new String[] {"filterSession", "filterStartDate", "filterDuration"};
				for (int i = 0; i < advancedFilters.size(); i++) {
					value = advancedFilters.get(i).getValue();
					if (value != null) {
						filtersURL += (filtersURL.equals("?") ? filterNames[i] + "=" : "&" + filterNames[i] + "=") + value.getValue();
					}
				}

				String filterText = filter.getTextField().getValue();
				if (filterText != null) {
					filterText = filtersURL.equals("?") ? "filterSession=" + filterText : "&filterText=" + filterText;
				} else {
					filterText = "";
				}
				String url = getRootURL() + filtersURL + filterText;
				RequestBuilder builder = new RequestBuilder(RequestBuilder.GET, url);
				DynamicHttpProxy<BasePagingLoadResult<BaseModelData>> proxy = getProxy();
				proxy.setBuilder(builder);
				GWT.log("loading data w/ filter: " + filterText, null);
				loadData();
				
			}
		});
	}
	
	public ColumnConfig getSessionConfig() {
		return configs.get(0);
	}
	
	private List<ColumnConfig> configs;

	private static final ColumnType[] columnTypes = {
		new ColumnType("handle",           "Session (Project) VP", 220, false, SessionField.class),
        new ColumnType("start",            "Start",                70,  false, DateEditField.class),
        new ColumnType("duration",         "Duration",             55,  false, Integer.class),
        new ColumnType("end",              "Last",                 70,  false, DateEditField.class),
        new ColumnType("total_time",       "Time (Hrs)",           90,  false, Float.class),
        new ColumnType("time_billed",      "Billed (Hrs)",         90,  false, DisplayField.class),
        new ColumnType("time_remaining",   "Remaining (Hrs)",     100,  false, DisplayField.class),
       	new ColumnType("complete",         "Completed?",           70,  false, Boolean.class),       
        new ColumnType("num_periods",      "Periods",              55,  false, Integer.class),
	};
	
	// creates list of dates that are the first of each month 
	private String[] getDates() {
		// TODO: generalize this so we don't have to update it!
		String[] years = new String[] {"2009", "2010", "2011"};
		String[] months = new String[] {"01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12"};
		String[] dates = new String[years.length*months.length];
		int index;
		for (int y = 0; y < years.length; y++) {
			for (int m = 0; m < months.length; m++) {
				index = (months.length * y) + m;
				dates[index] = years[y] + "-" + months[m] + "-01";
			}
		}
		return dates;
	}
	
	// creates list of day lengths by month
	private String[] getDays() {
		int daysPerMonth = 30;
		String[] days = new String[12];
		for (int i = 0; i < days.length; i++) {
			days[i] = Integer.toString((i+1)*daysPerMonth);
		}
		return days;
	}
	
	// gets all windowed session names form the server and populates the project combo
	public void updateSessionOptions() {
		JSONRequest.get("/scheduler/sessions/options"
			      , new HashMap<String, Object>() {{
			    	  put("mode", "windowed_session_handles");
			        }}
			      , new JSONCallbackAdapter() {
			public void onSuccess(JSONObject json) {
				advancedFilters.get(0).removeAll();
				
				JSONArray sessHandles = json.get("session handles").isArray();
				//GWT.log("got num of sessions: "+Integer.toString(sessHandles.size()), null); 
				for (int i = 0; i < sessHandles.size(); ++i){
					String sessHandle = sessHandles.get(i).toString().replace('"', ' ').trim();
					String sessName = sessHandle.split(" ")[0];
					advancedFilters.get(0).add(sessName);
					
				}
			}
		});
	}	
}
