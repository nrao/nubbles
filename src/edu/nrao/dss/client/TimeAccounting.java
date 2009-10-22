package edu.nrao.dss.client;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.extjs.gxt.ui.client.Style.LayoutRegion;
import com.extjs.gxt.ui.client.Style.Orientation;
import com.extjs.gxt.ui.client.Style.Scroll;
import com.extjs.gxt.ui.client.event.BaseEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.util.Margins;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.FormPanel;
import com.extjs.gxt.ui.client.widget.form.SimpleComboBox;
import com.extjs.gxt.ui.client.widget.form.TextArea;
import com.extjs.gxt.ui.client.widget.form.TextField;
import com.extjs.gxt.ui.client.widget.form.NumberField;
import com.extjs.gxt.ui.client.widget.layout.BorderLayoutData;
import com.extjs.gxt.ui.client.widget.layout.FitData;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.extjs.gxt.ui.client.widget.layout.RowData;
import com.extjs.gxt.ui.client.widget.layout.RowLayout;
import com.extjs.gxt.ui.client.widget.layout.TableData;
import com.extjs.gxt.ui.client.widget.layout.TableLayout;
import com.google.gwt.core.client.GWT;
import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONObject;

import edu.nrao.dss.client.util.TimeUtils;

public class TimeAccounting extends ContentPanel {

	final SimpleComboBox<String> projects = new SimpleComboBox<String>();
    final TextArea projectComments = new TextArea();
	final SimpleComboBox<String> sessions = new SimpleComboBox<String>();
    final LayoutContainer session = new LayoutContainer();
	final SimpleComboBox<String> periods = new SimpleComboBox<String>();
    final LayoutContainer periodContainer = new LayoutContainer();
    final PeriodSummaryPanel periodSummary = new PeriodSummaryPanel(null);
	final TextField sessionName = new TextField();
//	final TextField periodName = new TextField();
//	final NumberField periodNotBillable = new NumberField();
//	final NumberField periodLostTimeWeather = new NumberField();
    final ProjectTimeAccountPanel projectTimeAccounting = new ProjectTimeAccountPanel();
    final SessionTimeAccountPanel sessionTimeAccounting = new SessionTimeAccountPanel();
	
    final ArrayList<String> project_codes = new ArrayList<String>();
	final HashMap<String, Integer> periodInfo = new HashMap<String, Integer>();
	
	private JSONObject timeAccountingJson = new JSONObject();
	
	public TimeAccounting() {
		super();
		initLayout();
    }	

protected void initLayout() {
	
	setLayout( new FitLayout());
	
	// bells & whistles for this content panel
	//setHeading("Project Time Accounting");
	
	setCollapsible(false);
	setBodyBorder(true);
	setFrame(true);
	setHeaderVisible(true);
	setBodyStyle("backgroundColor: white;");

	final LayoutContainer project = new ContentPanel();
	project.setLayout(new RowLayout(Orientation.VERTICAL)); //FitLayout());
	project.setBorders(true);
	
	// now place some controls in the first column
	final FormPanel projectForm = new FormPanel();
	projectForm.setHeading("Project");
	projectForm.setBorders(true);

	
	// the project picker goes in this left-most form panel
	//final SimpleComboBox<String> projects = new SimpleComboBox<String>();
	projects.setFieldLabel("Project");
	//projects.add("proj1");
	//projects.add("proj2");
	// when a project gets picked, populate the sessions combo
	projects.addListener(Events.Valid, new Listener<BaseEvent>() {
	  	public void handleEvent(BaseEvent be) {
	  		GWT.log("projects Events.Valid", null);
	  		updateProjectSessions();
	  		// get all the time accounting info!
	  		getProjectTimeAccounting();
	   	}
	});	
	projectForm.add(projects);
    
	
	// followed by the session picker
	sessions.setFieldLabel("Session");
	sessions.addListener(Events.Valid, new Listener<BaseEvent>() {
	  	public void handleEvent(BaseEvent be) {
	  		GWT.log("session Events.Valid", null);
	  		updateSessionPeriods();
	   	}
	});	
	
	projectForm.add(sessions);

	Button save = new Button("Save");
	save.addListener(Events.OnClick, new Listener<BaseEvent>() {
		public void handleEvent(BaseEvent be) {
			GWT.log("Save!", null);
			// TODO: send back the time accounting json!!!
		}
	});
	
	projectForm.add(save);
	
    project.add(projectForm);

	// the project time accounting comments goes in this second form panel
    //projectComments.setFieldLabel("Comments");
    //projectFormLeft.add(projectComments);

//    ProjectTimeAccountPanel projectTimeAccounting = new ProjectTimeAccountPanel();
    projectTimeAccounting.setHeading("Project Time Accounting");
    project.add(projectTimeAccounting);
    
    // now add the session panel
	session.setLayout(new RowLayout(Orientation.VERTICAL)); //FitLayout());
	session.setBorders(true);
	session.setVisible(false);

	// first use form to affect this session and pick periods
	final FormPanel sessionForm = new FormPanel();
	sessionForm.setHeading("Session");
	sessionForm.setBorders(true);
	
	// the session picker goes in this left-most form panel
	sessionName.setValue("");
	sessionName.setReadOnly(true);
	sessionName.setFieldLabel("Session Name");
	sessionForm.add(sessionName);
	
	// followed by the period picker
	periods.setFieldLabel("Period");
	periods.addListener(Events.Valid, new Listener<BaseEvent>() {
	  	public void handleEvent(BaseEvent be) {
	  		GWT.log("period Events.Valid", null);
	  		updatePeriod();
	   	}
	});	
	sessionForm.add(periods);
	
	// the session time accounting comments goes in this second form panel
    TextArea sessionComments = new TextArea();
    sessionComments.setFieldLabel("Comments");
    //sessionForm.add(sessionComments);
    
    periodSummary.setVisible(true); // -> false
    periodSummary.setParent(this);
    
    session.add(sessionForm, new RowData(1, -1, new Margins(4)));
//    SessionTimeAccountPanel sessionTimeAccounting = new SessionTimeAccountPanel();
    sessionTimeAccounting.setHeading("Session Time Accounting");
	session.add(sessionTimeAccounting, new RowData(1, -1, new Margins(4)));
	session.add(periodSummary, new RowData(1, -1, new Margins(4)));
    
    
    project.add(session, new RowData(1, -1, new Margins(4)));
    
	add(project, new FitData(10));
	
  }

protected void getProjectTimeAccounting() {
    
	JSONRequest.get("/projects/time_accounting/" + projects.getSimpleValue()
		      , new JSONCallbackAdapter() {
		public void onSuccess(JSONObject json) {
			timeAccountingJson = json;
			// take the project level time accounting info, and populate the panel w/ it.
			populateProjTimeAccounting(timeAccountingJson);
           GWT.log("/projects/time_accounting onSuccess", null);          
		}
	});    			
}

public void setTimeAccountingFromJSON(JSONObject json) {
	// make sure the project & session panels get updated;
	// periods get updated by default
	GWT.log("setTimeAccountingFromJSON!", null);
	timeAccountingJson = json;
	populateProjTimeAccounting(json);
	String name = sessionName.getValue().toString();
	populateSessTimeAccounting(json, name);
}

private void populateProjTimeAccounting(JSONObject json) {
	
	String comments = json.get("notes").isString().stringValue() ;
	projectComments.setValue(comments);
	
	projectTimeAccounting.setValues(json);
	
	
	
}
// a period has been selected - now what?
protected void updatePeriod() {
	GWT.log("updatePeriod", null);
	// show the period pane with the periods info
	String name = periods.getSimpleValue();
	int periodId = periodInfo.get(name);
	// get this period from the server to fill in the time accnting form
	updatePeriodForm(periodId);
	//periodName.setValue(name);
	periodSummary.setVisible(true);

	
}

private void updatePeriodForm(int periodId) {
	// get this period from the server and populate the form
    GWT.log("updatePeriodForm", null);
    // TODO - should pick up timezone from Schedule
	JSONRequest.get("/periods/UTC/" + Integer.toString(periodId)
		      , new JSONCallbackAdapter() {
		public void onSuccess(JSONObject json) {
        	// JSON period -> JAVA period
         	Period period = Period.parseJSON(json.get("period").isObject());
         	updatePeriodForm(period);
            GWT.log("period onSuccess", null);          
		}
	});    		
	
}

protected void updatePeriodForm(Period period) {

	
	periodSummary.setPeriod(period);

}

// a session has been selected - what to do?
protected void updateSessionPeriods() {
	GWT.log("updateSessionPeriods", null);
	// show the session panel
	session.setVisible(true);
	// update the periods drop down 
	String name = sessions.getSimpleValue();
	String pcode = projects.getSimpleValue();
	sessionName.setValue(name);
	
	// extract the time accounting info for session just picked, and populate
	// it's time accounting panel
    populateSessTimeAccounting(timeAccountingJson, name);
    
	updatePeriodOptions(pcode, name);
	// hide the period panel until a period is choosen
	periodContainer.setVisible(false);
}

private void populateSessTimeAccounting(JSONObject json, String sessName) {
	// find the section of the json that has our session in it:
    //List<Period> periods = new ArrayList<Period>();
    JSONArray names = json.get("sessions").isArray();
    for (int i = 0; i < names.size(); ++i) {
    	//Period period = Period.parseJSON(names.get(i).isObject());
    	JSONObject session = names.get(i).isObject();
    	String name = session.get("name").isString().stringValue();
    	GWT.log("comparing: <" + sessName + "> and <" + name + ">", null);
    	if (name.equals(sessName)) {
    		// got it!
    		GWT.log("match!", null);
    		sessionTimeAccounting.setValues(session);
    		
    	}	
    	
//    	if (period != null){
//    		// TODO: really we should be using period state to keep these periods out
//    		if (period.getDuration() > 0) {
//        		periods.add(period);
//            }
    	
    }	
}

private void updatePeriodOptions(final String pcode, final String sessionName) {
    GWT.log("updatePeriodOptions", null);
	JSONRequest.get("/sessions/options"
		      , new HashMap<String, Object>() {{
		    	  put("mode", "periods");
		    	  put("pcode", pcode);
		    	  put("session_name", sessionName);
		        }}
		      , new JSONCallbackAdapter() {
		public void onSuccess(JSONObject json) {
			// get ready to populate the sessions codes list
			periods.clearSelections();
			periods.removeAll();
			periodInfo.clear();
			//project_codes.clear();
			JSONArray ps = json.get("periods").isArray();
			JSONArray ids = json.get("period ids").isArray();
			for (int i = 0; i < ps.size(); ++i){
				GWT.log(ps.get(i).toString(), null);
				String p = ps.get(i).toString().replace('"', ' ').trim();
				periods.add(p);
				String id = ids.get(i).toString().replace('"', ' ').trim();
				periodInfo.put(p, Integer.parseInt(id));
				
			}
			//session.setVisible(false);
		}
	});    	
}

// a project has been selected - what to do?
protected void updateProjectSessions() {
	GWT.log("updateProjectSessions", null);
	// update the sessions drop down and clear the current selection
	String pcode = projects.getSimpleValue();
	updateSessionOptions(pcode);
	// hide the session panel until a session is choosen
	session.setVisible(false);
}

// gets the session names from the server and populates the session combobox
private void updateSessionOptions(final String pcode) {
    GWT.log("updateSessionOptions", null);
	JSONRequest.get("/sessions/options"
		      , new HashMap<String, Object>() {{
		    	  put("mode", "session_names");
		    	  put("pcode", pcode);
		        }}
		      , new JSONCallbackAdapter() {
		public void onSuccess(JSONObject json) {
			// get ready to populate the sessions codes list
			sessions.clearSelections();
			sessions.removeAll();
			//project_codes.clear();
			JSONArray names = json.get("session names").isArray();
			for (int i = 0; i < names.size(); ++i){
				String name = names.get(i).toString().replace('"', ' ').trim();
				sessions.add(name);
				//projects.add(pcode);
				
			}
			//session.setVisible(false);
		}
	});    
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
			project_codes.clear();
			JSONArray pcodes = json.get("project codes").isArray();
			for (int i = 0; i < pcodes.size(); ++i){
				String pcode = pcodes.get(i).toString().replace('"', ' ').trim();
				project_codes.add(pcode);
				projects.add(pcode);
				
			}
		}
	});
}	

}
