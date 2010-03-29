package edu.nrao.dss.client;

import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;

import com.extjs.gxt.ui.client.data.BaseModelData;
import com.extjs.gxt.ui.client.data.BasePagingLoadResult;
import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.event.BaseEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.widget.Component;
import com.extjs.gxt.ui.client.widget.Dialog;
import com.extjs.gxt.ui.client.widget.Window;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.CheckBox;
import com.extjs.gxt.ui.client.widget.form.SimpleComboBox;
import com.extjs.gxt.ui.client.widget.form.SimpleComboValue;
import com.extjs.gxt.ui.client.widget.form.TextField;
import com.extjs.gxt.ui.client.widget.form.ComboBox.TriggerAction;
import com.extjs.gxt.ui.client.widget.grid.CellEditor;
import com.extjs.gxt.ui.client.widget.grid.CheckColumnConfig;
import com.extjs.gxt.ui.client.widget.grid.ColumnConfig;
import com.extjs.gxt.ui.client.widget.grid.ColumnModel;
import com.extjs.gxt.ui.client.widget.menu.CheckMenuItem;
import com.extjs.gxt.ui.client.widget.toolbar.SeparatorToolItem;
import com.extjs.gxt.ui.client.widget.toolbar.PagingToolBar;
import com.google.gwt.core.client.GWT;
import com.google.gwt.http.client.RequestBuilder;
import edu.nrao.dss.client.ProjectEmailPagingToolBar;

public class ProjectExplorer extends Explorer {
	public ProjectExplorer() {
		super("/projects", new ProjectType(), new ProjectEmailPagingToolBar(50));
		// ugly downcast, but we know that we have a ProjectEmailPagingToolBar now
		selectionPagingToolBar = (ProjectEmailPagingToolBar)pagingToolBar;
		initFilters();
		initLayout(initColumnModel(), true);  // creates grid
		selectionPagingToolBar.setGrid(grid); // uses grid, must come after initLayout
		
		clearButton = new Button("Clear selections");
		clearButton.setToolTip("Press to de-select grid items");
		toolBar.insert(new SeparatorToolItem(), 17);
		toolBar.insert(clearButton, 18);
		
		emailButton = new Button("Email");
		toolBar.insert(new SeparatorToolItem(), 19);
		toolBar.insert(emailButton, 20);
		emailButton.setToolTip("Email investigators");
		setEmailButtonListener();
		setClearButtonListener();
		viewItem.setVisible(true);
	}
	
	private void setClearButtonListener() {
		clearButton.addSelectionListener(new SelectionListener<ButtonEvent>() {
			@Override
			public void componentSelected(ButtonEvent be) {
				grid.getSelectionModel().deselectAll();
			}
		});
	}
	
	private void setEmailButtonListener() {
		emailButton.addSelectionListener(new SelectionListener<ButtonEvent>() {
			@Override
			public void componentSelected(ButtonEvent be) {
				int count = store.getCount();
				String text = "The number of cached objects in the store is " + count;
				text += "\n";
				List<BaseModelData> selection_list = grid.getSelectionModel().getSelectedItems();
				
				if (!selection_list.isEmpty())
				{
					for (int i = 0; i < selection_list.size(); ++i)
					{
						text += " " + selection_list.get(i).get("pcode");
					}
				}
				else
				{
					for (int i = 0; i < count; ++i)
					{	
						text += " " + store.getAt(i).get("pcode");
					}
				}
				
				testDialog = new Dialog();
				testDialog.setHeading("Testing dialog box");
				testDialog.addText(text);
				testDialog.setButtons(Dialog.OK);
				
				Button ok = testDialog.getButtonById(Dialog.OK);
				ok.addListener(Events.OnClick, new Listener<BaseEvent>() {
					public void handleEvent(BaseEvent be) {
						testDialog.hide();
					}
				});

				testDialog.show();
			}
		});
	}

	private void initFilters() {
		advancedFilters.add(initCombo("Project Type", new String[] {"science", "non-science"}, 100));
		advancedFilters.add(initCombo("Trimester", trimesters, 80));
		advancedFilters.add(initCombo("Complete", new String[] {"True", "False"}, 80));
		initFilterAction();
	}
	
	private void initFilterAction() {
		filterAction = new Button("Filter");
		filterAction.addSelectionListener(new SelectionListener<ButtonEvent>() {
			@Override
			public void componentSelected(ButtonEvent be){
				String filtersURL = "?";
				
				SimpleComboValue<String> value;
				String[] filterNames = new String[] {"filterType", "filterSem", "filterClp"};
				for (int i = 0; i < advancedFilters.size(); i++) {
					value = advancedFilters.get(i).getValue();
					if (value != null) {
						filtersURL += (filtersURL.equals("?") ? filterNames[i] + "=" : "&" + filterNames[i] + "=") + value.getValue();
					}
				}

				String filterText = filter.getTextField().getValue();
				if (filterText != null) {
					filterText = filtersURL.equals("?") ? "filterText=" + filterText : "&filterText=" + filterText;
				} else {
					filterText = "";
				}
				String url = getRootURL() + filtersURL + filterText;
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

	    column = new ColumnConfig("semester", "Trimester", 80);
	    column.setEditor(new CellEditor(new TextField<String>()));
	    configs.add(column);
	    
	    column = new ColumnConfig("pi", "PI", 100);
	    CellEditor editor   = new CellEditor(new TextField<String>());
	    editor.disable();
	    column.setEditor(editor);
	    configs.add(column);

	    column = new ColumnConfig("co_i", "Co-I", 150);
	    editor   = new CellEditor(new TextField<String>());
	    editor.disable();
	    column.setEditor(editor);
	    configs.add(column);

	    column = new ColumnConfig("type", "Type", 80);
	    column.setEditor(initCombo(new String[]{"science", "non-science"}));
	    configs.add(column);
	    
	    column = new ColumnConfig("total_time", "Total Time(s)", 80);
	    column.setEditor(new CellEditor(new TextField<String>()));
	    configs.add(column);

	    column = new ColumnConfig("PSC_time", "PSC Time(s)", 80);
	    column.setEditor(new CellEditor(new TextField<String>()));
	    configs.add(column);

	    column = new ColumnConfig("sem_time", "Max. Trimester Time(s)", 130);
	    column.setEditor(new CellEditor(new TextField<String>()));
	    configs.add(column);

	    column = new ColumnConfig("remaining", "Remaining Time", 130);
	    column.setEditor(new CellEditor(new TextField<String>()));
	    configs.add(column);
	    
	    column = new ColumnConfig("grade", "Grade(s)", 80);
	    column.setEditor(new CellEditor(new TextField<String>()));
	    configs.add(column);

	    column = new ColumnConfig("notes", "Notes", 80);
	    column.setEditor(new CellEditor(new TextField<String>()));
	    configs.add(column);

	    column = new ColumnConfig("schd_notes", "Schd. Notes", 80);
	    column.setEditor(new CellEditor(new TextField<String>()));
	    configs.add(column);
	    
	    CheckColumnConfig checkColumn = new CheckColumnConfig("thesis", "Thesis?", 55);
	    checkColumn.setEditor(new CellEditor(new CheckBox()));
	    configs.add(checkColumn);
	    checkBoxes.add(checkColumn);
	    
	    checkColumn = new CheckColumnConfig("complete", "Complete?", 65);
	    checkColumn.setEditor(new CellEditor(new CheckBox()));
	    configs.add(checkColumn);
	    checkBoxes.add(checkColumn);
	    
	    return new ColumnModel(configs);
	}
	
	// when the view button gets pressed, go to the Project Page tab.
	public void viewObject() {
		BaseModelData bd = grid.getSelectionModel().getSelectedItem();
		((Scheduler) parent).showProject((String) bd.get("pcode"));
	}
	
	public void setParent(Component parent) {
		this.parent = parent;
	}
	
	private Component parent;
	private Button emailButton;
	private Button clearButton;
	private Dialog testDialog;
	private ProjectEmailPagingToolBar selectionPagingToolBar;
}
