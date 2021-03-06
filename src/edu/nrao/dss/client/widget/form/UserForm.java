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

package edu.nrao.dss.client.widget.form;

import java.util.HashMap;

import com.extjs.gxt.ui.client.Style.HorizontalAlignment;  
import com.extjs.gxt.ui.client.event.BaseEvent;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.widget.LayoutContainer;  
import com.extjs.gxt.ui.client.widget.VerticalPanel;  
import com.extjs.gxt.ui.client.widget.Window;
import com.extjs.gxt.ui.client.widget.button.Button;  
import com.extjs.gxt.ui.client.widget.form.FormButtonBinding;  
import com.extjs.gxt.ui.client.widget.form.FormPanel;  
import com.extjs.gxt.ui.client.widget.form.SimpleComboBox;
import com.extjs.gxt.ui.client.widget.form.TextField;  
import com.extjs.gxt.ui.client.widget.form.ComboBox.TriggerAction;
import com.extjs.gxt.ui.client.widget.layout.FormData;  
import com.google.gwt.core.client.GWT;
import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.user.client.Element;  

import edu.nrao.dss.client.util.JSONCallbackAdapter;
import edu.nrao.dss.client.util.JSONRequest;
import edu.nrao.dss.client.widget.explorers.UserProjectExplorer;

// This is a mutlipurpose widget for choosing different types of users from a drop down
// selection.  Currently it is used for add Investigators and Friends to their respective
// Explorers.

public class UserForm extends LayoutContainer {  

    private VerticalPanel vp;
    private SimpleComboBox<String> users      = new SimpleComboBox<String>();
    private HashMap<String, Integer> user_ids = new HashMap<String, Integer>();
    private Window window;  
    private FormData formData;
	private UserProjectExplorer userProjectExplorer;
	private Button submit = new Button("Submit");
	private String type;
	private String urlType;

	// UserProjectExplorer is the abstract class extended by widgets like the InvestigatorExplorer.
    public UserForm(String type, String urlType, Window w, UserProjectExplorer userProjExp) {
    	this.type = type;
    	this.urlType = urlType;
    	setWindow(w);
    	w.add(this);
    	w.setSize(375, 175);
    	userProjectExplorer = userProjExp;
    }
    
	@Override  
    protected void onRender(Element parent, int index) {  
      super.onRender(parent, index);  
      formData = new FormData("-20");  
      vp = new VerticalPanel();  
      vp.setSpacing(10);  
      createForm();
      updateUserOptions();
      add(vp);  
    }  
   
    private void createForm() {  
      FormPanel form = new FormPanel();  
      form.setHeading("Add " + type);  
      form.setFrame(true);  
      form.setWidth(350);  
   
      users.setFieldLabel("User");
      users.setTriggerAction(TriggerAction.ALL);
      form.add(users);
      
      getSubmit().addSelectionListener(new SelectionListener<ButtonEvent>() {

		@Override
		public void componentSelected(ButtonEvent ce) {
			HashMap<String, Object> fields = new HashMap<String, Object>();
        	fields.put("project_id", userProjectExplorer.getProject_id());
        	fields.put("user_id", user_ids.get(users.getSimpleValue()));
        	userProjectExplorer.addRecordInterface(fields);
        	getWindow().hide();
        	
			
		}
    	  
      });
      form.addButton(getSubmit());
      
      Button c = new Button("Cancel");
      c.addSelectionListener(new SelectionListener<ButtonEvent>() {

		@Override
		public void componentSelected(ButtonEvent ce) {
			getWindow().hide();
		} 
      });
      form.addButton(c); 
    
      form.setButtonAlign(HorizontalAlignment.CENTER);  
    
      FormButtonBinding binding = new FormButtonBinding(form);  
      binding.addButton(getSubmit());  
    
      vp.add(form);  
    }
    
    // gets all the appropriate types of users from the server 
    // and populates the drop down.
	public void updateUserOptions() {
		JSONRequest.get("/scheduler/sessions/options"
			      , new HashMap<String, Object>() {{
			    	  put("mode", urlType);
			        }}
			      , new JSONCallbackAdapter() {
			public void onSuccess(JSONObject json) {
				// get ready to populate the user list
				users.removeAll();
				user_ids.clear();
				JSONArray invests = json.get(urlType).isArray();
				JSONArray ids     = json.get("ids").isArray();
				for (int i = 0; i < invests.size(); ++i){
					String invest = invests.get(i).toString().replace('"', ' ').trim();
					int id = (int) ids.get(i).isNumber().doubleValue();
					user_ids.put(invest, id);
					users.add(invest);
				}
			}
		});
	}

	public void setWindow(Window window) {
		this.window = window;
	}

	public Window getWindow() {
		return window;
	}

	public void setSubmit(Button submit) {
		this.submit = submit;
	}

	public Button getSubmit() {
		return submit;
	}
}
