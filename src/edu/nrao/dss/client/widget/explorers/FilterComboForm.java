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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.extjs.gxt.ui.client.Style.HorizontalAlignment;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.widget.Component;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.VerticalPanel;
import com.extjs.gxt.ui.client.widget.Window;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.button.SplitButton;
import com.extjs.gxt.ui.client.widget.form.FormButtonBinding;
import com.extjs.gxt.ui.client.widget.form.FormPanel;
import com.extjs.gxt.ui.client.widget.form.SimpleComboBox;
import com.extjs.gxt.ui.client.widget.form.SimpleComboValue;
import com.extjs.gxt.ui.client.widget.form.TextField;
import com.extjs.gxt.ui.client.widget.grid.ColumnConfig;
import com.extjs.gxt.ui.client.widget.layout.FormData;
import com.google.gwt.core.client.GWT;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.user.client.Element;

import edu.nrao.dss.client.util.JSONCallbackAdapter;
import edu.nrao.dss.client.util.JSONRequest;
import edu.nrao.dss.client.widget.FilterMenu;

public class FilterComboForm extends LayoutContainer {
	
	private VerticalPanel vp;
    private Window window = new Window();  
    private FormData formData;
	private Button submit = new Button("Submit");
	private Explorer explorer;
	
	public FilterComboForm(Explorer e) {
    	explorer = e;
    	window.add(this);
        window.setSize(375, 175);
    }
	
	@Override  
    protected void onRender(Element parent, int index) {  
      super.onRender(parent, index);  
      formData = new FormData("-20");  
      vp = new VerticalPanel();  
      vp.setSpacing(10);  
      createForm();
      add(vp);
    }
	
	private void createForm(){
		FormPanel form = new FormPanel();  
    	form.setHeading("Save Filter Combination");  
    	form.setFrame(true);  
    	form.setWidth(350);  
   
    	final TextField<String> name = new TextField<String>();
    	name.setFieldLabel("Name");
    	name.setAllowBlank(false);
    	form.add(name);
    
    	submit.addSelectionListener(new SelectionListener<ButtonEvent>() {

			@SuppressWarnings("deprecation")
			@Override
			public void componentSelected(ButtonEvent ce) {
				// Get the hidden status on each column in the explorer.
				final HashMap<String, Object> fields = new HashMap<String, Object>();
				for (SimpleComboBox<String> af : explorer.getAdvancedFilters()){
					SimpleComboValue<String> value = af.getValue();
					if (value != null){
						fields.put(af.getTitle(), value.getValue());
					}
				}
				
				String filterText = explorer.filter.getTextField().getValue();
				if (filterText != null) {
				    fields.put("filterText", filterText);
				}
				fields.put("name", name.getValue().toString());
				
				// Used to identify the specific explorer on the server.
				fields.put("explorer", explorer.rootURL);
				
				JSONRequest.post("/scheduler/configurations/explorer/filterCombos", fields, new JSONCallbackAdapter() {
					@Override
					public void onSuccess(JSONObject json) {
						String new_id = json.get("id").isNumber().toString();
						// Update saved filter combo list if we created a new combo
						if (! explorer.filterComboIds.contains(new_id)) {
						    FilterMenu menu = explorer.getFilterMenu();
							menu.add(new FilterComboMenuItem(explorer
													, fields.get("name").toString()
								              		, new_id));
							explorer.filterComboIds.add(new_id);
							int nilen = fields.get("name").toString().length();
							List<Boolean> status = new ArrayList<Boolean>();
							for (FilterComboMenuItem item : menu.getFilterItems()) {
								int ilen = item.title_len;
								status.add(nilen > ilen);
							}
							if (!status.contains(false)) {
								menu.setWidth(nilen * 10);
							}
							
						}
					}
				});
				getWindow().close();
				
			}
    	  
    	});
    	form.addButton(submit);
      
    	Button c = new Button("Cancel");
    	c.addSelectionListener(new SelectionListener<ButtonEvent>() {
	
			@SuppressWarnings("deprecation")
			@Override
			public void componentSelected(ButtonEvent ce) {
				getWindow().close();
			} 
    	});
    	form.addButton(c); 
    
    	form.setButtonAlign(HorizontalAlignment.CENTER);  
    
    	FormButtonBinding binding = new FormButtonBinding(form);  
    	binding.addButton(submit);  
    
    	vp.add(form);
	}
	
	public Window getWindow() {
		return window;
	}
}
