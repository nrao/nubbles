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

import com.extjs.gxt.ui.client.event.MenuEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.widget.Component;
import com.extjs.gxt.ui.client.widget.form.SimpleComboBox;
import com.extjs.gxt.ui.client.widget.form.SimpleComboValue;
import com.extjs.gxt.ui.client.widget.form.TextField;
import com.extjs.gxt.ui.client.widget.grid.ColumnConfig;
import com.extjs.gxt.ui.client.widget.menu.CheckMenuItem;
import com.extjs.gxt.ui.client.widget.menu.MenuItem;
import com.google.gwt.core.client.GWT;
import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONValue;

import edu.nrao.dss.client.util.JSONCallbackAdapter;
import edu.nrao.dss.client.util.JSONRequest;

public class FilterComboMenuItem extends CheckMenuItem {
	
	private Explorer explorer;
	public String combo_id;
	public int title_len;
	
	public FilterComboMenuItem(Explorer explorer, String title, String combo_id){
		super(title);
		this.title_len = title.length();
		this.explorer = explorer;
		this.combo_id = combo_id;
		initListeners();
	}
	
	private void initListeners() {
		this.addSelectionListener(new SelectionListener<MenuEvent>() {

			@Override
			public void componentSelected(MenuEvent ce) {
				// Get the configuration from the server.
				// Set the visibility of the columns based on the configuration.
				JSONRequest.get("/scheduler/configurations/explorer/filterCombos/" + combo_id, new JSONCallbackAdapter() {
					public void onSuccess(JSONObject json){
						// Set the filters base on server response
						JSONObject filters = json.get("filters").isObject();
						for (SimpleComboBox<String> af : explorer.getAdvancedFilters()){
							JSONValue filter = filters.get(af.getTitle());
							if (filter != null) {
								String value = filter.isString().stringValue();
								af.setSimpleValue(value);
							} else {
								af.reset();
							}
						}
						JSONValue filterText = filters.get("filterText");
						TextField<String> textField = explorer.filter.getTextField();
						if (filterText != null) {
							textField.setValue(filterText.isString().stringValue());
						} else {
							textField.setValue("");
						}
						
					}
				});
			}
		});
	}
}
