package edu.nrao.dss.client;

import com.extjs.gxt.ui.client.data.BaseModelData;
import com.extjs.gxt.ui.client.event.MenuEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.widget.grid.EditorGrid;
import com.extjs.gxt.ui.client.widget.menu.MenuItem;
import com.google.gwt.core.client.GWT;

public class ColumnConfigMenuItem extends MenuItem {
	private EditorGrid<BaseModelData> grid;
	
	public ColumnConfigMenuItem(EditorGrid<BaseModelData> grid, String title) {
		super(title);
		this.grid = grid;
		initListener();
	}
	
	private void initListener() {
		this.addSelectionListener(new SelectionListener<MenuEvent>() {

			@Override
			public void componentSelected(MenuEvent ce) {
				// TODO Auto-generated method stub
				// Get the configuration from the server.
				// Set the visibility of the columns based on the configuration.
				GWT.log("Getting config!");
			}
			
		});
	}

}
