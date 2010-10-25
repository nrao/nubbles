package edu.nrao.dss.client;


import java.util.HashMap;
import java.util.List;

import com.extjs.gxt.ui.client.Style.Orientation;
import com.extjs.gxt.ui.client.event.BaseEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.form.FormPanel;
import com.extjs.gxt.ui.client.widget.form.SimpleComboBox;
import com.extjs.gxt.ui.client.widget.form.TextField;
import com.extjs.gxt.ui.client.widget.form.ComboBox.TriggerAction;
import com.extjs.gxt.ui.client.widget.layout.AccordionLayout;
import com.extjs.gxt.ui.client.widget.layout.RowLayout;
import com.google.gwt.core.client.GWT;
import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONObject;

// This class is responsible for displaying all the info that the the window explorer USED to display
// for windows, before they got multiple periods and date ranges.  But for all windows belonging to a single session.

// TODO: need an ADD button for creating new window objects.
// TODO: why does the first section (window) never expand?
// TODO: why does expanding a section the first time trigger the session to be selected again???

public class WindowsInfoPanel extends ContentPanel {
	
    private HashMap<String, Integer> winIds = new HashMap<String, Integer>();
    private List<WindowInfoPanel> windows;
    
	public WindowsInfoPanel() {
		initLayout();
	}
	
	private void initLayout() {

		setLayout(new AccordionLayout());

		setBorders(false);
		setHeaderVisible(true);
		setHeading("Windows");
		setCollapsible(true);
		
	}
	
	// gets all form the server for the selected session
	public void updateWindowOptions(final int sessionId) {
		JSONRequest.get("/windows"
			      , new HashMap<String, Object>() {{
			    	  put("filterSessionId", sessionId);
			    	  put("sortField", "start");
			        }}
			      , new JSONCallbackAdapter() {
			public void onSuccess(JSONObject json) {
				
				// get ready to populate the windows
				winIds.clear();
			    removeAll();
			    
				JSONArray winsJson = json.get("windows").isArray();
				GWT.log("got num of windows: "+Integer.toString(winsJson.size()), null);
				
				setHeading("Windows (" + Integer.toString(winsJson.size()) + ")");
				
				for (int i = 0; i < winsJson.size(); ++i){
					JSONObject winJson =  winsJson.get(i).isObject();
					int id = (int) winJson.get("id").isNumber().doubleValue();
					GWT.log(Integer.toString(id), null);
					winIds.put(Integer.toString(id), id);
					WindowInfoPanel w = new WindowInfoPanel(winJson);
				
					add(w);
					
					// display the window as it comes in
					// TODO: should we do this just once?
    				layout();	
				}
			}
		});
	}

}

