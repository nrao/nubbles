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

package edu.nrao.dss.client;
import java.util.ArrayList;

import java.util.HashMap;
import java.util.Set;

import com.extjs.gxt.ui.client.Style.Orientation;
import com.extjs.gxt.ui.client.Style.Scroll;
import com.extjs.gxt.ui.client.event.BaseEvent;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.FormPanel;
import com.extjs.gxt.ui.client.widget.form.SimpleComboBox;
import com.extjs.gxt.ui.client.widget.form.SimpleComboValue;
import com.extjs.gxt.ui.client.widget.form.TextArea;
import com.extjs.gxt.ui.client.widget.form.TextField;
import com.extjs.gxt.ui.client.widget.form.ComboBox.TriggerAction;
import com.extjs.gxt.ui.client.widget.layout.FlowLayout;
import com.extjs.gxt.ui.client.widget.layout.FormData;
import com.extjs.gxt.ui.client.widget.layout.RowLayout;
import com.google.gwt.core.client.GWT;
import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONObject;
import com.extjs.gxt.ui.client.widget.Window;
//import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.HTML;

import edu.nrao.dss.client.util.JSONCallbackAdapter;
import edu.nrao.dss.client.util.JSONRequest;
import edu.nrao.dss.client.util.JSONRequestCache;
import edu.nrao.dss.client.widget.explorers.FriendExplorer;
import edu.nrao.dss.client.widget.explorers.InvestigatorExplorer;


public class ProjectPage extends ContentPanel {

	private FormPanel projectForm = new FormPanel();
	private Anchor anchor = new Anchor("Observers Project Page", "");	
	private SimpleComboBox<String> projects = new SimpleComboBox<String>();
	private TextField<String> name = new TextField<String>();
	private TextField<String> pi   = new TextField<String>();
	private TextField<String> coi  = new TextField<String>();
	private TextField<String> friends  = new TextField<String>();
	
	private TextArea schNotes      = new TextArea();
	private TextArea obsNotes      = new TextArea();
	private Button save = new Button();
	private Button reset = new Button();
	
	private HashMap<String, Integer> project_ids = new HashMap<String, Integer>();
    private JSONObject projectJson;
    private FormData fd = new FormData(500, 25);
    
    private InvestigatorExplorer investigatorExplorer = new InvestigatorExplorer();
    private FriendExplorer friendExplorer = new FriendExplorer();
	
	public ProjectPage() {
		initLayout();
		initListeners();
		updatePCodeOptions();
	}
	
	public InvestigatorExplorer getInvestigatorExplorer() {
		return investigatorExplorer;
	}
	
	private void initLayout() {

		setLayout(new RowLayout(Orientation.VERTICAL));
		
		setBorders(false);
		setHeaderVisible(false);
		
		// so we can always see everything 
		setScrollMode(Scroll.AUTO); 

		//projectForm.setHeading("Project");
		//projectForm.setBorders(true);
        projectForm.setHeaderVisible(false);
        projectForm.add(new HTML("<h2>Project Information</h2>"));
		
		// the project picker goes in this left-most form panel
        projects.setTriggerAction(TriggerAction.ALL);
		projects.setFieldLabel("Project");
		projectForm.add(projects);
		
		name.setFieldLabel("Title");
		name.setReadOnly(true);
		projectForm.add(name, fd);

		pi.setFieldLabel("PI");
		pi.setReadOnly(true);
		projectForm.add(pi, fd);
		
		coi.setFieldLabel("COI");
		coi.setReadOnly(true);
		projectForm.add(coi, fd);
		
		friends.setFieldLabel("Friends");
		friends.setReadOnly(true);
		projectForm.add(friends, fd);
		
		obsNotes.setFieldLabel("Observer Notes");
		projectForm.add(obsNotes, new FormData(500, 200));

		schNotes.setFieldLabel("Scheduler Notes");
		projectForm.add(schNotes, new FormData(500, 200));

    	anchor.setEnabled(false);
    	anchor.setVisible(false);
    	projectForm.add(anchor);
    	
	   	reset.setText("Reset");
    	projectForm.add(reset);

	   	save.setText("Save");
    	projectForm.add(save);
    	
    	add(projectForm);
		add(new HTML("<h2>Project Team</h2>"));
		add(investigatorExplorer);
		add(new HTML("<h2>Project Friends</h2>"));
		add(friendExplorer);
		
	}

	private void initListeners() {
		
		// when a project gets picked, populate the sessions combo
		projects.addListener(Events.Valid, new Listener<BaseEvent>() {
		  	public void handleEvent(BaseEvent be) {
		  		// go git it
		  		getProject(projects.getSimpleValue());
		   	}
		});		
		
		obsNotes.addListener(Events.Blur, new Listener<BaseEvent>() {
			@Override
			public void handleEvent(BaseEvent be) {
	            markTextAreaChange(be);
			}			
    	});			
		
		schNotes.addListener(Events.Blur, new Listener<BaseEvent>() {
			@Override
			public void handleEvent(BaseEvent be) {
	            markTextAreaChange(be);
			}			
    	});		
		
    	reset.addListener(Events.OnClick, new Listener<BaseEvent>() {
    		public void handleEvent(BaseEvent be) {
    			getProject(projects.getSimpleValue());
    		}
    	});
    	
    	save.addListener(Events.OnClick, new Listener<BaseEvent>() {
    		public void handleEvent(BaseEvent be) {
    			updateProject(projects.getSimpleValue());
    		}
    	});
    	
    	investigatorExplorer.getSaveItem().addSelectionListener(new SelectionListener<ButtonEvent>() {
			@Override
			public void componentSelected(ButtonEvent ce) {
				updateProject(projects.getSimpleValue());
			}
    	});
    	
    	investigatorExplorer.getRemoveApproval().addSelectionListener(new SelectionListener<ButtonEvent>() {
			@Override
			public void componentSelected(ButtonEvent ce) {
    		    updateProject(projects.getSimpleValue());
			}
    	});
    	
    	investigatorExplorer.getAddUser().getSubmit().addSelectionListener(new SelectionListener<ButtonEvent>() {
			public void componentSelected(ButtonEvent ce) {
				updateProject(projects.getSimpleValue());
			}
    	});
    	friendExplorer.getSaveItem().addSelectionListener(new SelectionListener<ButtonEvent>() {
			@Override
			public void componentSelected(ButtonEvent ce) {
				updateProject(projects.getSimpleValue());
			}
    	});
    	
    	friendExplorer.getRemoveApproval().addSelectionListener(new SelectionListener<ButtonEvent>() {
			@Override
			public void componentSelected(ButtonEvent ce) {
    		    updateProject(projects.getSimpleValue());
			}
    	});
    	
    	friendExplorer.getAddUser().getSubmit().addSelectionListener(new SelectionListener<ButtonEvent>() {
			@Override
			public void componentSelected(ButtonEvent ce) {
				updateProject(projects.getSimpleValue());
			}
    	});		
	}
	
	// if the textarea changes value, mark it in red
	private void markTextAreaChange(BaseEvent be) {
        String value = ((TextArea) be.getSource()).getValue();
        String orgvl = ((TextArea) be.getSource()).getOriginalValue();
        String color = (orgvl.compareTo(value) == 0) ? "black" : "red";
        ((TextArea) be.getSource()).el().firstChild().setStyleAttribute("color", color);
	}

	// retrieves the project's JSON
	private void getProject(String pcode) {
		
		// don't bother if it doesn't even look like a valid pcode
		if ((pcode == null) || (pcode.equals(new String("")))) {
			//Window.alert("You must select a valid project code.");
			return;
		}
		
		String url = "/scheduler/projects/" + project_ids.get(pcode);
		HashMap <String, Object> keys = new HashMap<String, Object>();
		
		JSONRequest.get(url, keys, new JSONCallbackAdapter() {
			public void onSuccess(JSONObject json) {
				projectJson = json;
				populateProjectPage(json);
			}
		});
		
		investigatorExplorer.loadProject(project_ids.get(pcode));
		friendExplorer.loadProject(project_ids.get(pcode));
		
		
	}
	
	// populates this page's widgets with the project's JSON values
	protected void populateProjectPage(JSONObject json) {
		
		JSONObject proj = json.get("project").isObject();
		
		// read-only fields
	    name.setValue(proj.get("name").isString().stringValue());
	    pi.setValue(proj.get("pi").isString().stringValue());
	    coi.setValue(proj.get("co_i").isString().stringValue());
	    friends.setValue(proj.get("friends").isString().stringValue());
	    setObserversLink();
	    
	    // for writable fields, set them up again so that changes
	    // can be marked w/ red.
	    String notes = proj.get("schd_notes").isString().stringValue();
	    schNotes.setValue(notes); 
	    schNotes.setOriginalValue(notes);
	    schNotes.el().firstChild().setStyleAttribute("color", "black");

	    notes = proj.get("notes").isString().stringValue();
	    obsNotes.setValue(notes);
	    obsNotes.setOriginalValue(notes);
	    obsNotes.el().firstChild().setStyleAttribute("color", "black");
		
	}

	private void setObserversLink() {
		String name = projects.getSimpleValue();
		String url = "https://dss.gb.nrao.edu/project/" + name;
		anchor.setHref(url);
		anchor.setText(name + " Observers Page");
		anchor.setEnabled(true);
		anchor.setVisible(true);
	}
	
	public Integer getSelectedProjectID() {
		return project_ids.get(projects.getSimpleValue());
	}
	
	public void reload() {
		getProject(projects.getSimpleValue());
	}
	
	// gets all project codes form the server and populates the project combo
	public void updatePCodeOptions() {
		JSONRequestCache.get("/scheduler/sessions/options"
				, new HashMap<String, Object>() {{
	    	  put("mode", "project_codes");
          }}
		, new JSONCallbackAdapter() {
			public void onSuccess(JSONObject json) {
				// get ready to populate the project codes list
				projects.removeAll();
				project_ids.clear();
				JSONArray pcodes = json.get("project codes").isArray();
				JSONArray ids    = json.get("project ids").isArray();
				//GWT.log("got num of pcodes: "+Integer.toString(pcodes.size()), null); 
				for (int i = 0; i < pcodes.size(); ++i){
					String pcode = pcodes.get(i).toString().replace('"', ' ').trim();
					int id = (int) ids.get(i).isNumber().doubleValue();
					project_ids.put(pcode, id);
					projects.add(pcode);
					
				}
			}
		});
	}
	
	// take changes from widgets and send them over to the server to change
	// this project
	private void updateProject(String pcode) {
		// first we have to transfer over the original json we got from the server
		// to a HashMap - except for fields we're changing
		String value;
		HashMap<String, Object> keys = new HashMap<String, Object>();
		// necessary for triggering an update on the server
		keys.put("_method", "put"); 
		JSONObject json = projectJson.get("project").isObject();
		Set<String> set = json.keySet();
		for (String key : set) {
			// avoid the fields we're changing
			if ((!key.equals("notes")) && (!key.equals("shcd_notes"))) {
				GWT.log("iterator: "+ key + " : " + json.get(key).toString(), null);
				if (json.get(key).isString() != null) {
					value = json.get(key).isString().stringValue();
				} else {
					value = json.get(key).toString();
				}
				keys.put(key, value);
			}
		}
        
		// now set the fields we're changing
		String v;
		v = obsNotes.getValue();
		if (v == null) {
			v = "";
		}
		keys.put("notes", v);
		v = schNotes.getValue();
		if (v == null) {
			v = "";
		}			
		keys.put("schd_notes", v);
		v = friends.getValue();
		if (v == null) {
			v = "";
		}
		keys.put("friends", v);
				
		String url = "/scheduler/projects/" + project_ids.get(pcode);
		
		JSONRequest.post(url, keys,
			      new JSONCallbackAdapter() {
			public void onSuccess(JSONObject json) {
	                GWT.log("updateProject onSuccess", null);
	                // refresh the project from the server
	                getProject(projects.getSimpleValue());
				}
		    });
	}

	public void setProject(String pcode) {
		GWT.log("PP.setProject", null);
		projects.setSimpleValue(pcode);
	
	}
	
//	public InvestigatorExplorer getInvestigatorExplorer() {
//		return investigatorExplorer;
//	}
}
