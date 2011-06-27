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

package edu.nrao.dss.client.widget;

import java.util.HashMap;

import com.extjs.gxt.ui.client.Style.Orientation;
import com.extjs.gxt.ui.client.event.BaseEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.ComboBox.TriggerAction;
import com.extjs.gxt.ui.client.widget.form.SimpleComboBox;
import com.extjs.gxt.ui.client.widget.layout.RowLayout;
import com.extjs.gxt.ui.client.widget.toolbar.ToolBar;
import com.google.gwt.core.client.GWT;
import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONObject;

import edu.nrao.dss.client.util.JSONCallbackAdapter;
import edu.nrao.dss.client.util.JSONRequest;

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
    private SimpleComboBox<String> cmpFilter;
	String [] cmpOptions = new String[] {"Not Complete", "Complete", "Cmp. & Not Cmp."};
    
	protected abstract void displayPeriodGroups(JSONObject json);
    
	public PeriodGroupsInfoPanel(String url, String type) {
		this.url = url;
		this.type = type;
		initLayout();
		initListeners();
	}
	
	private void initLayout() {

		// Note: originally wanted to use this layout, but can't get first panel to display
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
		
		cmpFilter = new SimpleComboBox<String>();
		cmpFilter.setTriggerAction(TriggerAction.ALL);
		//filter.setWidth(width);
		cmpFilter.setEmptyText("Completion");
		cmpFilter.setTitle("Completion");
		for (String o : cmpOptions) {
			cmpFilter.add(o);
		}
		cmpFilter.setSimpleValue(cmpOptions[0]);
		toolBar.add(cmpFilter);
		
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

		// New Windows & Electives are incomplete by default, so make sure we can still see these
		// by ignoring this filter
		cmpFilter.setSimpleValue(cmpOptions[2]);
		
		JSONRequest.post("/scheduler/" + url //.i.e: "/electives"
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
		// remove anything displayed now to make it clear that we're reloading
		removeAll();
		String msg = "Loading " + type + "s ...";
		setHeading(msg);
		final MessageBox box = MessageBox.wait(msg, msg, "Be Patient ...");
		
		this.sessionId = sessionId;
		this.sessionHandle = sessionHandle;
		
		HashMap<String, Object> keys = new HashMap<String, Object>() {{
	    	  put("filterSessionId", sessionId);
        }};
		
	    // how to filter by complete, if at all?
		String cmpStr = cmpFilter.getSimpleValue();
		if (cmpStr.compareTo(cmpOptions[0]) == 0) {
			keys.put("filterComplete", false);
		} else if (cmpStr.compareTo(cmpOptions[1]) == 0) {
			keys.put("filterComplete", true);
		}
		
		JSONRequest.get("/scheduler/" + url
			      , keys 
			      , new JSONCallbackAdapter() {
			public void onSuccess(JSONObject json) {
				displayPeriodGroups(json);
				box.close();
			}
		});
	}

	
}


