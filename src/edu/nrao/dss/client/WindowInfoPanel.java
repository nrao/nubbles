package edu.nrao.dss.client;
// comment

import java.util.HashMap;

import com.extjs.gxt.ui.client.Style.Orientation;
import com.extjs.gxt.ui.client.event.BaseEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.form.FormPanel;
import com.extjs.gxt.ui.client.widget.form.SimpleComboBox;
import com.extjs.gxt.ui.client.widget.form.TextField;
import com.extjs.gxt.ui.client.widget.form.ComboBox.TriggerAction;
import com.extjs.gxt.ui.client.widget.layout.RowLayout;
import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONObject;

public class WindowInfoPanel extends ContentPanel {
	
	private FormPanel winForm = new FormPanel();
	
	private SimpleComboBox<String> windows = new SimpleComboBox<String>();
	// TODO: what other fields?
	private TextField<String> start = new TextField<String>();

    private HashMap<String, Integer> winIds = new HashMap<String, Integer>();
    private JSONObject winJson;
    
	public WindowInfoPanel() {
		initLayout();
		initListeners();
		//updateWindowOptions();
	}
	
	private void initLayout() {

		setLayout(new RowLayout(Orientation.VERTICAL));
		

		setBorders(false);
		setHeaderVisible(false);
		setCollapsible(true);

        winForm.setHeaderVisible(false);

        windows.setTriggerAction(TriggerAction.ALL);
		windows.setFieldLabel("Window");
		winForm.add(windows);	

		//type.setFieldLabel("Type");
		//sessForm.add(type);
		
		add(winForm);
	}
	
	private void initListeners() {
		
		// when a project gets picked, populate the sessions combo
		windows.addListener(Events.Valid, new Listener<BaseEvent>() {
		  	public void handleEvent(BaseEvent be) {
		  		// go git it
		  		getWindow(windows.getSimpleValue());
		   	}
		});		
	}		
	
	// retrieves the project's JSON
	private void getWindow(String winHandle) {
		
		// don't bother if it doesn't even look like a valid pcode
		if ((winHandle == null) || (winHandle.equals(new String("")))) {
			//Window.alert("You must select a valid project code.");
			return;
		}
		
		String url = "/windows/" + winIds.get(winHandle);
		HashMap <String, Object> keys = new HashMap<String, Object>();
		
		JSONRequest.get(url, keys, new JSONCallbackAdapter() {
			// this url returns all the time accounting for the whole proj., 
			// so use it to update the whole UI
			public void onSuccess(JSONObject json) {
			    winJson = json;
			    //GWT.log(json.toString(), null);
				populateWindowPage(json);
			}
		});		
		
//		// TODO: just a test!
//		JSONRequest.get("/windows", keys, new JSONCallbackAdapter() {
//			// this url returns all the time accounting for the whole proj., 
//			// so use it to update the whole UI
//			public void onSuccess(JSONObject json) {
//			    //sessJson = json;
//				GWT.log("Got windows", null);
//			    GWT.log(json.toString(), null);
//				//populateSessionPage(json);
//			}
//		});				
	}
	
	// gets all project codes form the server and populates the project combo
	public void updateWindowOptions(final int sessionId) {
		JSONRequest.get("/windows"
			      , new HashMap<String, Object>() {{
			    	  put("filterSessionId", sessionId);
			        }}
			      , new JSONCallbackAdapter() {
			public void onSuccess(JSONObject json) {
				// get ready to populate the project codes list
				windows.removeAll();
				winIds.clear();
				// TODO: first check to see if there ARE any windows
				JSONArray winsJson = json.get("windows").isArray();
				//JSONArray ids    = json.get("ids").isArray();
				//GWT.log("got num of sessions: "+Integer.toString(sessHandles.size()), null); 
				for (int i = 0; i < winsJson.size(); ++i){
					//String winHandle = winHandles.get(i).toString().replace('"', ' ').trim();
					//int id = (int) ids.get(i).isNumber().doubleValue();
					//winIds.put(winHandle, id);
					//JSONObject winJson =  winsJson.get(i).
					winIds.put(Integer.toString(i), i);
					windows.add(Integer.toString(i));
					
				}
			}
		});
	}
	
	private void populateWindowPage(JSONObject json) {
//		JSONObject s = json.get("session").isObject();
//		type.setValue(s.get("type").isString().stringValue());
	}
}
