package edu.nrao.dss.client;

import java.util.HashMap;

import com.extjs.gxt.ui.client.Style.Orientation;
import com.extjs.gxt.ui.client.event.BaseEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.layout.RowLayout;
import com.extjs.gxt.ui.client.widget.toolbar.ToolBar;
import com.google.gwt.core.client.GWT;
import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONObject;

// Windows and Electives are a type of grouping of periods, thus the 'PeriodGroup*' classes.
// This abstract class can be implemented in order to expose Electives or Windows in the UI.

public abstract class PeriodGroupsInfoPanel extends ContentPanel {
	
    protected HashMap<String, Integer> ids = new HashMap<String, Integer>();
    private int sessionId;
    private String sessionHandle;
    
    protected String url;
    protected String type;
    
    private Button add;
    private Button refresh;
    
	protected abstract void displayPeriodGroups(JSONObject json);
    
	public PeriodGroupsInfoPanel(String url, String type) {
		this.url = url;
		this.type = type;
		initLayout();
		initListeners();
	}
	
	private void initLayout() {

		// TODO: originally wanted to use this layout, but can't get first panel to display
		//setLayout(new AccordionLayout());
		setLayout(new RowLayout(Orientation.VERTICAL));

		setBorders(false);
		setHeaderVisible(true);
		setCollapsible(true);
		setVisible(false);
		
		ToolBar toolBar = new ToolBar();
		setTopComponent(toolBar);
		
		add = new Button();
		add.setText("Add");
		add.setToolTip("Add a new " + type + " to this session.");
		toolBar.add(add);

		refresh = new Button();
		refresh.setText("Refresh");
		refresh.setToolTip("Not trusting what you see?  Reload it all ...");
		toolBar.add(refresh);
		
	}

	private void initListeners() {
	    add.addListener(Events.OnClick, new Listener<BaseEvent>() {
	    	@SuppressWarnings("deprecation")
			public void handleEvent(BaseEvent be) {
	    		//addElective();
	    		addPeriodGroup();
	    	}
	    });
	    
	    refresh.addListener(Events.OnClick, new Listener<BaseEvent>() {
	    	@SuppressWarnings("deprecation")
			public void handleEvent(BaseEvent be) {
	    		//getElectives();
	    		getPeriodGroups();
	    	}
	    });				
	    
	}
	
	private void addPeriodGroup() {
		JSONRequest.post("/" + url //.i.e: "/electives"
			      , new HashMap<String, Object>() {{
			    	  put("handle", sessionHandle);
			    	  put("_method", "create");
			        }}
			      , new JSONCallbackAdapter() {
			public void onSuccess(JSONObject json) {
				// if we succesffully added a new one, then reload all of them
				getPeriodGroups(sessionId, sessionHandle);
			}
		});		
	}
	
	public void getPeriodGroups() {
		getPeriodGroups(this.sessionId, this.sessionHandle);
	}
	
	// gets all 'groups' info from the server for the selected session
	public void getPeriodGroups(final int sessionId, String sessionHandle) {
		this.sessionId = sessionId;
		this.sessionHandle = sessionHandle;
		JSONRequest.get("/" + url
		
			      , new HashMap<String, Object>() {{
			    	  put("filterSessionId", sessionId);
			        }}
			      , new JSONCallbackAdapter() {
			public void onSuccess(JSONObject json) {
				displayPeriodGroups(json);
			}
		});
	}

	
}


