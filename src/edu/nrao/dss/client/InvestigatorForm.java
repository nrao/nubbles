package edu.nrao.dss.client;

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
import com.extjs.gxt.ui.client.widget.layout.FormData;  
import com.google.gwt.core.client.GWT;
import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.user.client.Element;  

public class InvestigatorForm extends LayoutContainer {  

    private VerticalPanel vp;
    private SimpleComboBox<String> users      = new SimpleComboBox<String>();
    private HashMap<String, Integer> user_ids = new HashMap<String, Integer>();
    private Window window;
    private String projectURL;
    
    private FormData formData;  

    public InvestigatorForm(Window w, String projectURL) {
    	window     = w;
    	this.projectURL = projectURL;
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
      form.setHeading("Add Investigator");  
      form.setFrame(true);  
      form.setWidth(350);  
   
      users.setFieldLabel("User");
      form.add(users);
    
      Button b = new Button("Submit");
      b.addSelectionListener(new SelectionListener<ButtonEvent>() {

		@Override
		public void componentSelected(ButtonEvent ce) {
			String user = users.getSimpleValue();
			HashMap<String, Object> data = new HashMap<String, Object>();
			data.put("investigator", user);
			
			JSONRequest.post(projectURL, data, new JSONCallbackAdapter() {
				@SuppressWarnings("deprecation")
				public void onSuccess(JSONObject json) {
					window.close();
				}
			});
		}
    	  
      });
      form.addButton(b);
      
      Button c = new Button("Cancel");
      c.addSelectionListener(new SelectionListener<ButtonEvent>() {

		@SuppressWarnings("deprecation")
		@Override
		public void componentSelected(ButtonEvent ce) {
			window.close();
		} 
      });
      form.addButton(c); 
    
      form.setButtonAlign(HorizontalAlignment.CENTER);  
    
      FormButtonBinding binding = new FormButtonBinding(form);  
      binding.addButton(b);  
    
      vp.add(form);  
    }
    
 // gets all project codes form the server and populates the project combo
	public void updateUserOptions() {
		JSONRequest.get("/sessions/options"
			      , new HashMap<String, Object>() {{
			    	  put("mode", "users");
			        }}
			      , new JSONCallbackAdapter() {
			public void onSuccess(JSONObject json) {
				// get ready to populate the project codes list
				users.removeAll();
				user_ids.clear();
				JSONArray invests = json.get("users").isArray();
				JSONArray ids     = json.get("ids").isArray();
				GWT.log("got num of users: "+Integer.toString(invests.size()), null); 
				for (int i = 0; i < invests.size(); ++i){
					String invest = invests.get(i).toString().replace('"', ' ').trim();
					int id = (int) ids.get(i).isNumber().doubleValue();
					user_ids.put(invest, id);
					users.add(invest);
				}
			}
		});
	}
}