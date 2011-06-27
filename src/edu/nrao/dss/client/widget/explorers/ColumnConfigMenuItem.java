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

import edu.nrao.dss.client.util.JSONCallbackAdapter;
import edu.nrao.dss.client.util.JSONRequest;

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
				JSONRequest.get("/scheduler/configurations/explorer/columnConfigs/" + config_id, new JSONCallbackAdapter() {
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
