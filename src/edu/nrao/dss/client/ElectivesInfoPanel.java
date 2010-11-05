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

// This class is responsible for displaying all the info that the the elective explorer USED to display
// for electives, before they got multiple periods and date ranges.  But for all electives belonging to a single session.

public class ElectivesInfoPanel extends ContentPanel {
	
    private HashMap<String, Integer> elecIds = new HashMap<String, Integer>();
    //private List<electiveInfoPanel> electives;
    private int sessionId;
    private String sessionHandle;
    
    private Button addelective;
    private Button refresh;
    
	public ElectivesInfoPanel() {
		initLayout();
		initListeners();
	}
	
	private void initLayout() {

		// TODO: originally wanted to use this layout, but can't get first panel to display
		//setLayout(new AccordionLayout());
		setLayout(new RowLayout(Orientation.VERTICAL));

		setBorders(false);
		setHeaderVisible(true);
		setHeading("electives");
		setCollapsible(true);
		setVisible(false);
		
		ToolBar toolBar = new ToolBar();
		setTopComponent(toolBar);
		
		addelective = new Button();
		addelective.setText("Add");
		addelective.setToolTip("Add a new elective to this session.");
		toolBar.add(addelective);

		refresh = new Button();
		refresh.setText("Refresh");
		refresh.setToolTip("Not trusting what you see?  Reload it all ...");
		toolBar.add(refresh);
		
	}

	private void initListeners() {
	    addelective.addListener(Events.OnClick, new Listener<BaseEvent>() {
	    	@SuppressWarnings("deprecation")
			public void handleEvent(BaseEvent be) {
	    		addElective();
	    	}
	    });
	    
	    refresh.addListener(Events.OnClick, new Listener<BaseEvent>() {
	    	@SuppressWarnings("deprecation")
			public void handleEvent(BaseEvent be) {
	    		getElectives();
	    	}
	    });				
	    
	}
	
	private void addElective() {
		JSONRequest.post("/electives"
			      , new HashMap<String, Object>() {{
			    	  put("handle", sessionHandle);
			    	  put("_method", "create");
			        }}
			      , new JSONCallbackAdapter() {
			public void onSuccess(JSONObject json) {
				// if we succesffully added a new elective, then reload all of them
				getElectives(sessionId, sessionHandle);
			}
		});		
	}
	
	public void getElectives() {
		getElectives(this.sessionId, this.sessionHandle);
	}
	
	// gets all elective info from the server for the selected session
	public void getElectives(final int sessionId, String sessionHandle) {
		this.sessionId = sessionId;
		this.sessionHandle = sessionHandle;
		JSONRequest.get("/electives"
			      , new HashMap<String, Object>() {{
			    	  put("filterSessionId", sessionId);
			    	  //put("sortField", "start");
			        }}
			      , new JSONCallbackAdapter() {
			public void onSuccess(JSONObject json) {
				GWT.log("getElectives onSuces: "); //+ json.toString());
			    displayElectives(json);	
			}
		});
	}
	
	private void displayElectives(JSONObject json) {
		// get ready to populate the electives
		elecIds.clear();
	    removeAll();
	    
		JSONArray elecsJson = json.get("electives").isArray();
		setHeading("Electives (" + Integer.toString(elecsJson.size()) + ")");
		
		for (int i = 0; i < elecsJson.size(); ++i){
			
			// turn each elective JSON into a elective panel
			JSONObject elecJson =  elecsJson.get(i).isObject();
			int id = (int) elecJson.get("id").isNumber().doubleValue();
			elecIds.put(Integer.toString(id), id);
			ElectiveInfoPanel e = new ElectiveInfoPanel(elecJson);
			add(e);
			
			// display the elective as it comes in
			// TODO: should we do this just once?
			layout();	
		}		
	}

}

