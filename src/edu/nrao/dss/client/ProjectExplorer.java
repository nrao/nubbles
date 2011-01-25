package edu.nrao.dss.client;

import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import com.extjs.gxt.ui.client.data.BaseModelData;
import com.extjs.gxt.ui.client.data.BasePagingLoadResult;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.ComponentEvent;
import com.extjs.gxt.ui.client.event.FieldEvent;
import com.extjs.gxt.ui.client.event.GridEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.event.BaseEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.WidgetListener;
import com.extjs.gxt.ui.client.widget.Component;
import com.extjs.gxt.ui.client.widget.Dialog;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.button.SplitButton;
import com.extjs.gxt.ui.client.widget.form.CheckBox;
import com.extjs.gxt.ui.client.widget.form.SimpleComboBox;
import com.extjs.gxt.ui.client.widget.form.SimpleComboValue;
import com.extjs.gxt.ui.client.widget.form.TextField;
import com.extjs.gxt.ui.client.widget.grid.CellEditor;
import com.extjs.gxt.ui.client.widget.grid.CheckColumnConfig;
import com.extjs.gxt.ui.client.widget.grid.ColumnConfig;
import com.extjs.gxt.ui.client.widget.grid.ColumnModel;
import com.extjs.gxt.ui.client.widget.menu.Menu;
import com.extjs.gxt.ui.client.widget.menu.MenuItem;
import com.extjs.gxt.ui.client.widget.toolbar.SeparatorToolItem;
import com.google.gwt.core.client.GWT;
import com.google.gwt.http.client.RequestBuilder;
import edu.nrao.dss.client.ProjectEmailPagingToolBar;
import edu.nrao.dss.client.ProjectsEmailDialogBox;
import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONObject;

public class ProjectExplorer extends Explorer {
	public ProjectExplorer() {
		super("/projects", "?filterClp=False", new ProjectType(), new ProjectEmailPagingToolBar(50));
		// downcast, but we know that we have a ProjectEmailPagingToolBar now
		selectionPagingToolBar = (ProjectEmailPagingToolBar)pagingToolBar;
		initFilters();
		initLayout(initColumnModel(), true);  // creates grid
		selectionPagingToolBar.setGrid(grid); // uses grid, must come after initLayout
		
		clearButton = new Button("Clear selections");
		clearButton.setToolTip("Press to de-select grid items");
		toolBar.insert(clearButton, 6);
		
		emailButton = new Button("Email");
		toolBar.insert(emailButton, 7);
		emailButton.setToolTip("Email investigators");
		setEmailButtonListener();
		setClearButtonListener();
		viewItem.setVisible(true);
	}
	
	private void setClearButtonListener() {
		clearButton.addSelectionListener(new SelectionListener<ButtonEvent>() {
			@Override
			public void componentSelected(ButtonEvent be) {
				grid.getSelectionModel().deselectAll();   // clear actual grid
				selectionPagingToolBar.clearSelections(); // clear multi-page selections
			}
		});
	}
	
	private void setEmailButtonListener() {
		emailButton.addSelectionListener(new SelectionListener<ButtonEvent>() {
			@Override
			public void componentSelected(ButtonEvent be) {
				String pcodes = "";
				List<String> selection_list = selectionPagingToolBar.getSelections();
				
				if (!selection_list.isEmpty())
				{
					for (int i = 0; i < selection_list.size(); ++i)
					{
						pcodes += selection_list.get(i) + " ";
					}
				}
				
				getEmailAddresses(pcodes);
			}
		});
	}
	
	
	private String unpackJSONArray(JSONArray ja)
	{
		String s = "";

		for (int i = 0; i < ja.size(); ++i)
		{
			s += ja.get(i).isString().stringValue() + ", ";
		}

		if (s.length() > 2)
		{
			s = s.substring(0, s.length() - 2); // Get rid of last comma.
		}
		
		return s;
	}
	
	
	private void getEmailAddresses(final String pcodes)
	{
		HashMap<String, Object> keys = new HashMap<String, Object>();
		String msg = "Generating email addresses for selected projects";
		final MessageBox box = MessageBox.wait("Getting Email Addresses", msg, "Be Patient ...");
		String url = "/projects/email";
		
		if (pcodes.equals(""))
		{
			SimpleComboValue<String> value;
			
			for (int i = 0; i < advancedFilters.size(); ++i)
			{
				value = advancedFilters.get(i).getValue();
			
				if (value != null)
				{
					keys.put(filterNames[i], value.getValue());
				}
			}
			
			String filterText = filter.getTextField().getValue();
			
			if (filterText != null)
			{
				keys.put("filterText", filterText);
			}
		}
		else
		{
			keys.put("pcodes", pcodes);
		}

		JSONRequest.get(url, keys,
				new JSONCallbackAdapter()
				{
					public void onSuccess(JSONObject json)
					{
						String pi_addr;
						String pc_addr;
						String ci_addr;
						String ob_addr;

						JSONArray pi_emails = json.get("PI-Addresses").isArray();
						pi_addr = unpackJSONArray(pi_emails);

						JSONArray pc_emails = json.get("PC-Addresses").isArray();
						pc_addr = unpackJSONArray(pc_emails);

						JSONArray ci_emails = json.get("CO-I-Addresses").isArray();
						ci_addr = unpackJSONArray(ci_emails);
						
						JSONArray ob_emails = json.get("OBS-Addresses").isArray();
						ob_addr = unpackJSONArray(ob_emails);
						
						box.close();
						ProjectsEmailDialogBox dlg = new ProjectsEmailDialogBox(pcodes, pi_addr, pc_addr, ci_addr, ob_addr);
						dlg.show();
					}
					
					public void onError(String error, JSONObject json)
					{
						super.onError(error, json);
						box.close();
					}
				});
	}

	private void initFilters() {
		advancedFilters.add(initCombo("Project Type", new String[] {"science", "non-science"}, 100));
		advancedFilters.add(initCombo("Semester", semesters, 80));
		SimpleComboBox<String> project_complete = initCombo("Proj Status", new String[] {pcomplete, pincomplete, pall}, 110);
		project_complete.setSimpleValue(pincomplete);
		advancedFilters.add(project_complete);
		initFilterAction();
	}
	
	private void initFilterAction() {
		filterAction = new SplitButton("Filter");
		filterAction.setMenu(initFilterMenu());
		filterAction.addSelectionListener(new SelectionListener<ButtonEvent>() {
			@Override
			public void componentSelected(ButtonEvent be){
				selectionPagingToolBar.clearSelections(); // filtering should clear old selections.
				filtersURL = "?";
				String filterVal;
				filterNames = new String[] {"filterType", "filterSem", "filterClp"};
				for (int i = 0; i < advancedFilters.size(); i++) {
					SimpleComboValue<String> value = advancedFilters.get(i).getValue();
					if (value != null) {
						GWT.log(filterNames[i]);
						if (filterNames[i] == "filterClp") {
							filterVal = value.getValue();
							if (filterVal == pcomplete) {
								filterVal = "True";
							} else if (filterVal == pincomplete) {
								filterVal = "False";
							} else {
								filterVal = null;
							}
						} else {
							filterVal = value.getValue();
						}
						if (filterVal != null) {
							filtersURL += (filtersURL.equals("?") ? filterNames[i] + "=" : "&" + filterNames[i] + "=") + filterVal;
						}
					}
				}

				String filterText = filter.getTextField().getValue();
				if (filterText != null) {
					filterText = filtersURL.equals("?") ? "filterText=" + filterText : "&filterText=" + filterText;
				} else {
					filterText = "";
				}
				
				filtersURL += filterText;
				String url = getRootURL() + filtersURL;
				RequestBuilder builder = new RequestBuilder(RequestBuilder.GET, url);
				DynamicHttpProxy<BasePagingLoadResult<BaseModelData>> proxy = getProxy();
				proxy.setBuilder(builder);
				loadData();
				
			}
		});
	}
	
	private ColumnModel initColumnModel() {
		
		List<ColumnConfig> configs = new ArrayList<ColumnConfig>();

	    ColumnConfig column = new ColumnConfig("pcode", "PCode", 100);
	    column.setEditor(new CellEditor(new TextField<String>()));
	    configs.add(column);

	    column = new ColumnConfig("name", "Name", 400);
	    column.setEditor(new CellEditor(new TextField<String>()));
	    configs.add(column);

	    column = new ColumnConfig("semester", "Semester", 80);
	    column.setEditor(new CellEditor(new TextField<String>()));
	    configs.add(column);
	    
	    column = new ColumnConfig("pi", "PI", 100);
	    CellEditor editor   = new CellEditor(new TextField<String>());
	    editor.disable();
	    column.setEditor(editor);
	    column.setSortable(false);
	    configs.add(column);

	    column = new ColumnConfig("co_i", "Co-I", 150);
	    editor   = new CellEditor(new TextField<String>());
	    editor.disable();
	    column.setEditor(editor);
	    column.setSortable(false);
	    configs.add(column);

	    column = new ColumnConfig("friend", "Friend", 150);
	    editor   = new CellEditor(new TextField<String>());
	    editor.disable();
	    column.setEditor(editor);
	    column.setSortable(false);
	    configs.add(column);
	    
	    column = new ColumnConfig("type", "Type", 80);
	    column.setEditor(initCombo(new String[]{"science", "non-science"}));
	    configs.add(column);
	    
	    column = new ColumnConfig("total_time", "Total Time(s)", 80);
	    column.setSortable(false);
	    column.setEditor(new CellEditor(new TextField<String>()));
	    configs.add(column);

	    column = new ColumnConfig("PSC_time", "PSC Time(s)", 80);
	    column.setSortable(false);
	    column.setEditor(new CellEditor(new TextField<String>()));
	    configs.add(column);

	    column = new ColumnConfig("sem_time", "Max. Semester Time(s)", 130);
	    column.setSortable(false);
	    column.setEditor(new CellEditor(new TextField<String>()));
	    configs.add(column);

	    column = new ColumnConfig("remaining", "Remaining Time", 130);
	    column.setEditor(new CellEditor(new TextField<String>()));
	    configs.add(column);
	    
	    column = new ColumnConfig("grade", "Grade(s)", 80);
	    column.setSortable(false);
	    column.setEditor(new CellEditor(new TextField<String>()));
	    configs.add(column);
	    
	    CheckColumnConfig checkColumn = new CheckColumnConfig("thesis", "Thesis?", 55);
	    checkColumn.setEditor(new CellEditor(new CheckBox()));
	    configs.add(checkColumn);
	    checkBoxes.add(checkColumn);

	    checkColumn = new CheckColumnConfig("blackouts", "Blackouts?", 70);
	    checkColumn.setEditor(new CellEditor(new CheckBox()));
	    configs.add(checkColumn);
	    checkBoxes.add(checkColumn);
	    
	    checkColumn = new CheckColumnConfig("complete", "Complete?", 65);
	    checkColumn.setEditor(new CellEditor(new CheckBox()));
	    configs.add(checkColumn);
	    checkBoxes.add(checkColumn);
	    
	    return new ColumnModel(configs);
	}
	
	public void registerObservers(SessionColConfig sePcodeConfig
			                    , TimeAccounting ta
			                    , ProjectPage pp){
		this.sePcodeConfig = sePcodeConfig;
		timeAccounting     = ta;
		projectPage        = pp;
	}
	
	public void updateObservers() {
		sePcodeConfig.updatePCodeOptions();
		timeAccounting.updatePCodeOptions();
		projectPage.updatePCodeOptions();
	}
	
	// when the view button gets pressed, go to the Project Page tab.
	public void viewObject() {
		BaseModelData bd = grid.getSelectionModel().getSelectedItem();
		((Scheduler) parent).showProject((String) bd.get("pcode"));
	}
	
	public void setParent(Component parent) {
		this.parent = parent;
	}
	
	private String filtersURL;
	private String filterNames[];
	
	private Component parent;
	private Button emailButton;
	private Button clearButton;
	private ProjectEmailPagingToolBar selectionPagingToolBar;
	
	// Observers
	private SessionColConfig sePcodeConfig;
	private TimeAccounting timeAccounting;
	private ProjectPage projectPage;
	
    private static String pcomplete   = "Proj Complete";
    private static String pincomplete = "Proj Incomplete";
    private static String pall        = "Proj All";
}
