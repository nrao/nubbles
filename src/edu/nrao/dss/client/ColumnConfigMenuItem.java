package edu.nrao.dss.client;

import com.extjs.gxt.ui.client.data.BaseModelData;
import com.extjs.gxt.ui.client.event.MenuEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.widget.grid.ColumnConfig;
import com.extjs.gxt.ui.client.widget.grid.EditorGrid;
import com.extjs.gxt.ui.client.widget.menu.CheckMenuItem;
import com.extjs.gxt.ui.client.widget.menu.MenuItem;
import com.google.gwt.core.client.GWT;
import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONObject;

public class ColumnConfigMenuItem extends CheckMenuItem {
	private EditorGrid<BaseModelData> grid;
	public String config_id;
	
	public ColumnConfigMenuItem(EditorGrid<BaseModelData> grid, String title, String config_id) {
		super(title);
		this.grid = grid;
		this.config_id   = config_id;
		initListener();
	}
	
	private void initListener() {
		this.addSelectionListener(new SelectionListener<MenuEvent>() {

			@Override
			public void componentSelected(MenuEvent ce) {
				// Get the configuration from the server.
				// Set the visibility of the columns based on the configuration.
				JSONRequest.get("/configurations/explorer/columnConfigs/" + config_id, new JSONCallbackAdapter() {
					public void onSuccess(JSONObject json){
						//  Start by showing all columns
						for (ColumnConfig cc : grid.getColumnModel().getColumns()){
							cc.setHidden(false);
						}
						
						//  Then hide the columns given by the server to restore the 
						//  column configuration.
						JSONArray columns = json.get("columns").isArray();
						for (int i = 0; i < columns.size(); ++i) {
							String column_id = columns.get(i).isString().stringValue();
							ColumnConfig column = grid.getColumnModel().getColumnById(column_id);
							column.setHidden(true);
						}
						grid.getView().refresh(true);
					}
				});
			}
			
		});
	}

}
