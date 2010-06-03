package edu.nrao.dss.client;
import java.util.ArrayList;

import java.util.HashMap;
import java.util.Set;

import com.extjs.gxt.ui.client.Style.Orientation;
import com.extjs.gxt.ui.client.event.BaseEvent;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.FormPanel;
import com.extjs.gxt.ui.client.widget.form.SimpleComboBox;
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


public class ProjectPage extends ContentPanel {

	private FormPanel projectForm = new FormPanel();
	private Anchor anchor = new Anchor("Observers Project Page", "");	
	private SimpleComboBox<String> projects = new SimpleComboBox<String>();
	private TextField<String> name = new TextField<String>();
	private TextField<String> pi   = new TextField<String>();
	private TextField<String> coi  = new TextField<String>();
	private TextArea schNotes      = new TextArea();
	private TextArea obsNotes      = new TextArea();
	private Button save = new Button();
	private Button reset = new Button();
	
    private HashMap<String, Integer> project_ids = new HashMap<String, Integer>();
    private JSONObject projectJson;
    private FormData fd = new FormData(500, 25);
    
    private InvestigatorExplorer investigatorExplorer = new InvestigatorExplorer();
	
	public ProjectPage() {
		initLayout();
		initListeners();
		updatePCodeOptions();
	}
	
	private void initLayout() {

		setLayout(new RowLayout(Orientation.VERTICAL));
		

		setBorders(false);
		setHeaderVisible(false);
		setAutoHeight(true);
		
		// so we can always see everything 
		//setScrollMode(Scroll.ALWAYS); 

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
		name.setStyleAttribute("color", "grey");
		projectForm.add(name, fd);

		pi.setFieldLabel("PI");
		pi.setReadOnly(true);
		pi.setStyleAttribute("color", "grey");
		projectForm.add(pi, fd);
		
		coi.setFieldLabel("COI");
		coi.setReadOnly(true);
		coi.setStyleAttribute("color", "grey");
		projectForm.add(coi, fd);
		
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
    	
    	investigatorExplorer.saveItem.addSelectionListener(new SelectionListener<ButtonEvent>() {
			@Override
			public void componentSelected(ButtonEvent ce) {
				updateProject(projects.getSimpleValue());
			}
    	});
    	
    	investigatorExplorer.removeApproval.addSelectionListener(new SelectionListener<ButtonEvent>() {
			@Override
			public void componentSelected(ButtonEvent ce) {
    		    updateProject(projects.getSimpleValue());
			}
    	});
    	
    	investigatorExplorer.getAddInvest().getSubmit().addSelectionListener(new SelectionListener<ButtonEvent>() {
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
        if (orgvl.compareTo(value) == 0) {
        	((TextArea) be.getSource()).setStyleAttribute("color", "black");
        } else {
        	((TextArea) be.getSource()).setStyleAttribute("color", "red");
        }
	}

	// retrieves the project's JSON
	private void getProject(String pcode) {
		
		// don't bother if it doesn't even look like a valid pcode
		if ((pcode == null) || (pcode.equals(new String("")))) {
			//Window.alert("You must select a valid project code.");
			return;
		}
		
		String url = "/projects/" + project_ids.get(pcode);
		HashMap <String, Object> keys = new HashMap<String, Object>();
		
		JSONRequest.get(url, keys, new JSONCallbackAdapter() {
			// this url returns all the time accounting for the whole proj., 
			// so use it to update the whole UI
			public void onSuccess(JSONObject json) {
				projectJson = json;
				populateProjectPage(json);
			}
		});
		
		investigatorExplorer.loadProject(project_ids.get(pcode));
		
	}
	
	// populates this page's widgets with the project's JSON values
	protected void populateProjectPage(JSONObject json) {
		
		JSONObject proj = json.get("project").isObject();
		//GWT.log("name: " + proj.get("name").toString(), null);
		
		// read-only fields
	    name.setValue(proj.get("name").isString().stringValue());
	    pi.setValue(proj.get("pi").isString().stringValue());
	    coi.setValue(proj.get("co_i").isString().stringValue());
	    setObserversLink();
	    
	    // for writable fields, set them up again so that changes
	    // can be marked w/ red.
	    String notes = proj.get("schd_notes").isString().stringValue();
	    schNotes.setValue(notes); 
	    schNotes.setOriginalValue(notes);
	    schNotes.setStyleAttribute("color", "black");

	    notes = proj.get("notes").isString().stringValue();
	    obsNotes.setValue(notes);
	    obsNotes.setOriginalValue(notes);
	    obsNotes.setStyleAttribute("color", "black");
		
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
		JSONRequest.get("/sessions/options"
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
				
		String url = "/projects/" + project_ids.get(pcode);
		
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
}
