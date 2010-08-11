package edu.nrao.dss.client;

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
				JSONRequest.get("/configurations/explorer/filterCombos/" + combo_id, new JSONCallbackAdapter() {
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
