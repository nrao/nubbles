package edu.nrao.dss.client;


import java.util.HashMap;
import java.util.List;

import com.extjs.gxt.ui.client.Style.Orientation;
import com.extjs.gxt.ui.client.event.BaseEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.FormPanel;
import com.extjs.gxt.ui.client.widget.form.SimpleComboBox;
import com.extjs.gxt.ui.client.widget.form.TextField;
import com.extjs.gxt.ui.client.widget.form.ComboBox.TriggerAction;
import com.extjs.gxt.ui.client.widget.layout.AccordionLayout;
import com.extjs.gxt.ui.client.widget.layout.RowLayout;
import com.extjs.gxt.ui.client.widget.toolbar.ToolBar;
import com.google.gwt.core.client.GWT;
import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONObject;

// This class is responsible for displaying all the info that the the window explorer USED to display
// for windows, before they got multiple periods and date ranges.  But for all windows belonging to a single session.

public class WindowsInfoPanel extends ContentPanel {
	
    private HashMap<String, Integer> winIds = new HashMap<String, Integer>();
    private List<WindowInfoPanel> windows;
    private int sessionId;
    private String sessionHandle;
    
    private Button addWindow;
    private Button refresh;
    
	public WindowsInfoPanel() {
		initLayout();
		initListeners();
	}
	
	private void initLayout() {

		// TODO: originally wanted to use this layout, but can't get first panel to display
		//setLayout(new AccordionLayout());
		setLayout(new RowLayout(Orientation.VERTICAL));

		setBorders(false);
		setHeaderVisible(true);
		setHeading("Windows");
		setCollapsible(true);
		setVisible(false);
		
		ToolBar toolBar = new ToolBar();
		setTopComponent(toolBar);
		
		addWindow = new Button();
		addWindow.setText("Add");
		addWindow.setToolTip("Add a new window to this session.");
		toolBar.add(addWindow);

		refresh = new Button();
		refresh.setText("Refresh");
		refresh.setToolTip("Not trusting what you see?  Reload it all ...");
		toolBar.add(refresh);
		
	}

	private void initListeners() {
	    addWindow.addListener(Events.OnClick, new Listener<BaseEvent>() {
	    	@SuppressWarnings("deprecation")
			public void handleEvent(BaseEvent be) {
	    		addWindow();
	    	}
	    });
	    
	    refresh.addListener(Events.OnClick, new Listener<BaseEvent>() {
	    	@SuppressWarnings("deprecation")
			public void handleEvent(BaseEvent be) {
	    		getWindows();
	    	}
	    });				
	    
	}
	
	private void addWindow() {
		JSONRequest.post("/windows"
			      , new HashMap<String, Object>() {{
			    	  put("handle", sessionHandle);
			    	  put("_method", "create");
			        }}
			      , new JSONCallbackAdapter() {
			public void onSuccess(JSONObject json) {
				// if we succesffully added a new window, then reload all of them
				getWindows(sessionId, sessionHandle);
			}
		});		
	}
	
	public void getWindows() {
		getWindows(this.sessionId, this.sessionHandle);
	}
	
	// gets all window info from the server for the selected session
	public void getWindows(final int sessionId, String sessionHandle) {
		this.sessionId = sessionId;
		this.sessionHandle = sessionHandle;
		JSONRequest.get("/windows"
			      , new HashMap<String, Object>() {{
			    	  put("filterSessionId", sessionId);
			    	  put("sortField", "start");
			        }}
			      , new JSONCallbackAdapter() {
			public void onSuccess(JSONObject json) {
			    displayWindows(json);	
			}
		});
	}
	
	private void displayWindows(JSONObject json) {
		// get ready to populate the windows
		winIds.clear();
	    removeAll();
	    
		JSONArray winsJson = json.get("windows").isArray();
		setHeading("Windows (" + Integer.toString(winsJson.size()) + ")");
		
		for (int i = 0; i < winsJson.size(); ++i){
			
			// turn each window JSON into a window panel
			JSONObject winJson =  winsJson.get(i).isObject();
			int id = (int) winJson.get("id").isNumber().doubleValue();
			winIds.put(Integer.toString(id), id);
			WindowInfoPanel w = new WindowInfoPanel(winJson);
			add(w);
			
			// display the window as it comes in
			// TODO: should we do this just once?
			layout();	
		}		
	}

}

