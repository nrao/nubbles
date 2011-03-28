package edu.nrao.dss.client.widget;

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

// This class is responsible for displaying Electives for a given session, and is displayed
// in the SessionPage when an Elective Session is selected.

public class ElectivesInfoPanel extends PeriodGroupsInfoPanel {
	
public ElectivesInfoPanel(String url, String type) {
		super(url, type);
		// TODO Auto-generated constructor stub
	}

    public void getElectives() {
    	this.getPeriodGroups();
    }
    
    public void getElectives(int id, String handle) {
    	this.getPeriodGroups(id, handle);
    }    
    
	protected void displayPeriodGroups(JSONObject json) {
		// get ready to populate the electives
		ids.clear();
	    removeAll();
	    
		JSONArray elecsJson = json.get("electives").isArray();
		setHeading("Electives (" + Integer.toString(elecsJson.size()) + ")");
		
		for (int i = 0; i < elecsJson.size(); ++i){
			
			// turn each elective JSON into a elective panel
			JSONObject elecJson =  elecsJson.get(i).isObject();
			int id = (int) elecJson.get("id").isNumber().doubleValue();
			ids.put(Integer.toString(id), id);
			ElectiveInfoPanel e = new ElectiveInfoPanel(elecJson, "electives", "Elective");
			add(e);
			
			// display the elective as it comes in
			// TODO: should we do this just once?
			layout();	
		}		
	}




}

