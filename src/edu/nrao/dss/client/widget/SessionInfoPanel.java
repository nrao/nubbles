package edu.nrao.dss.client.widget;


import java.util.HashMap;

import com.extjs.gxt.ui.client.Style.Orientation;
import com.extjs.gxt.ui.client.event.BaseEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.Window;
import com.extjs.gxt.ui.client.widget.form.FormPanel;
import com.extjs.gxt.ui.client.widget.form.SimpleComboBox;
import com.extjs.gxt.ui.client.widget.form.TextField;
import com.extjs.gxt.ui.client.widget.form.ComboBox.TriggerAction;
import com.extjs.gxt.ui.client.widget.layout.RowLayout;
import com.google.gwt.core.client.GWT;
import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.user.client.ui.Anchor;

import edu.nrao.dss.client.util.JSONCallbackAdapter;
import edu.nrao.dss.client.util.JSONRequest;
import edu.nrao.dss.client.util.JSONRequestCache;

public class SessionInfoPanel extends ContentPanel {
	
	private FormPanel sessForm = new FormPanel();
	private Anchor anchor = new Anchor("Observers Project Page", "");	
	private SimpleComboBox<String> sessions = new SimpleComboBox<String>();
	// TODO: what other fields?
	private TextField<String> type = new TextField<String>();

    private HashMap<String, Integer> sessIds = new HashMap<String, Integer>();
    private JSONObject sessJson;
    private WindowsInfoPanel wp;
    //private TestAccordionPanel ap;
    
	public SessionInfoPanel() {
		initLayout();
		initListeners();
		updateSessionOptions();
	}
	
	private void initLayout() {

		setLayout(new RowLayout(Orientation.VERTICAL));
		

		setBorders(false);
		setHeaderVisible(false);
		setCollapsible(true);

        sessForm.setHeaderVisible(false);

        sessions.setTriggerAction(TriggerAction.ALL);
		sessions.setFieldLabel("Session");
		sessForm.add(sessions);	

		type.setFieldLabel("Type");
		sessForm.add(type);
		
		add(sessForm);
	}
	
	private void initListeners() {
		
		// when a project gets picked, populate the sessions combo
		sessions.addListener(Events.Valid, new Listener<BaseEvent>() {
		  	public void handleEvent(BaseEvent be) {
		  		// go git it
		  		getSession(sessions.getSimpleValue());
		   	}
		});		
	}		
	
	public void setWindowsInfoPanel(WindowsInfoPanel wp) {
		this.wp = wp;
	}
	    
	// retrieves the project's JSON
	private void getSession(String sessionHandle) {
		
		// don't bother if it doesn't even look like a valid pcode
		if ((sessionHandle == null) || (sessionHandle.equals(new String("")))) {
			//Window.alert("You must select a valid project code.");
			return;
		}
		
		String url = "/sessions/" + sessIds.get(sessionHandle);
		HashMap <String, Object> keys = new HashMap<String, Object>();
		
		JSONRequest.get(url, keys, new JSONCallbackAdapter() {
			// this url returns all the time accounting for the whole proj., 
			// so use it to update the whole UI
			public void onSuccess(JSONObject json) {
			    sessJson = json;
			    GWT.log(json.toString(), null);
				populateSessionPage(json);
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
	public void updateSessionOptions() {
		JSONRequestCache.get("/sessions/options"
				, new HashMap<String, Object>() {{
			    	  put("mode", "session_handles");
			        }}
				, new JSONCallbackAdapter() {
			public void onSuccess(JSONObject json) {
				// get ready to populate the project codes list
				sessions.removeAll();
				sessIds.clear();
				JSONArray sessHandles = json.get("session handles").isArray();
				JSONArray ids    = json.get("ids").isArray();
				//GWT.log("got num of sessions: "+Integer.toString(sessHandles.size()), null); 
				for (int i = 0; i < sessHandles.size(); ++i){
					String sessHandle = sessHandles.get(i).toString().replace('"', ' ').trim();
					int id = (int) ids.get(i).isNumber().doubleValue();
					sessIds.put(sessHandle, id);
					sessions.add(sessHandle);
					
				}
			}
		});
	}
	
	private void populateSessionPage(JSONObject json) {
		GWT.log("session json: " + json, null);
		JSONObject s = json.get("session").isObject();
		type.setValue(s.get("type").isString().stringValue());
		
		// window's panel
		// TODO: standardize all this session handle crap.
		int sessionId = (int) s.get("id").isNumber().doubleValue();
		String name = s.get("name").isString().stringValue();
		String pcode = s.get("pcode").isString().stringValue();
		String handle = name + " (" + pcode + ")";
		GWT.log("handle: "+ handle, null);
		
		this.wp.getWindows(sessionId, handle);
		
		//this.ap.update();
	}
}
