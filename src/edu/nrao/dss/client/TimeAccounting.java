package edu.nrao.dss.client;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.extjs.gxt.ui.client.Style.HorizontalAlignment;
import com.extjs.gxt.ui.client.Style.LayoutRegion;
import com.extjs.gxt.ui.client.Style.Orientation;
import com.extjs.gxt.ui.client.Style.Scroll;
import com.extjs.gxt.ui.client.Style.VerticalAlignment;
import com.extjs.gxt.ui.client.event.BaseEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.util.Margins;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.FieldSet;
import com.extjs.gxt.ui.client.widget.form.FormPanel;
import com.extjs.gxt.ui.client.widget.form.SimpleComboBox;
import com.extjs.gxt.ui.client.widget.form.TextArea;
import com.extjs.gxt.ui.client.widget.form.TextField;
import com.extjs.gxt.ui.client.widget.form.NumberField;
import com.extjs.gxt.ui.client.widget.form.ComboBox.TriggerAction;
import com.extjs.gxt.ui.client.widget.layout.BorderLayoutData;
import com.extjs.gxt.ui.client.widget.layout.FitData;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.extjs.gxt.ui.client.widget.layout.FormLayout;
import com.extjs.gxt.ui.client.widget.layout.RowData;
import com.extjs.gxt.ui.client.widget.layout.RowLayout;
import com.extjs.gxt.ui.client.widget.layout.TableData;
import com.extjs.gxt.ui.client.widget.layout.TableLayout;
import com.google.gwt.core.client.GWT;
import com.google.gwt.i18n.client.NumberFormat;
import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.user.client.Window;

import edu.nrao.dss.client.util.TimeUtils;

public class TimeAccounting extends ContentPanel {

	// project level
    final ArrayList<String> project_codes = new ArrayList<String>();
	final SimpleComboBox<String> projects = new SimpleComboBox<String>();
	final SimpleComboBox<String> sessions = new SimpleComboBox<String>();
	final ProjAllotmentFieldSet projGrade1 = new ProjAllotmentFieldSet();
	final ProjAllotmentFieldSet projGrade2 = new ProjAllotmentFieldSet();
    final ProjectTimeAccountPanel projectTimeAccounting = new ProjectTimeAccountPanel();
	
	// session level
    final LayoutContainer session = new LayoutContainer();
	final TextField<String> sessionName = new TextField<String>();
	final NumberField sessionGrade = new NumberField();
	final NumberField sessionTime = new NumberField();
	final SimpleComboBox<String> periods = new SimpleComboBox<String>();
    final SessionTimeAccountPanel sessionTimeAccounting = new SessionTimeAccountPanel();
	final HashMap<String, Integer> periodInfo = new HashMap<String, Integer>();

    // period level
    final LayoutContainer periodContainer = new LayoutContainer();
    final PeriodSummaryPanel periodSummary = new PeriodSummaryPanel(null);
	
	// stores all the time accounting info we get from the server
	private JSONObject timeAccountingJson = new JSONObject();
	
	public TimeAccounting() {
		super();
		initLayout();
    }	

protected void initLayout() {
	
	setLayout( new FitLayout());
	//setAutoHeight(true);
	setHeight(920);
	

	setHeaderVisible(false);

	final ContentPanel project = new ContentPanel();
	project.setLayout(new RowLayout(Orientation.VERTICAL)); 
	project.setBorders(false);
	project.setHeaderVisible(false);
	
	// so we can always see everything 
	project.setScrollMode(Scroll.ALWAYS); 

	// first the project table!
	LayoutContainer projectTable = new LayoutContainer();
	TableLayout tb = new TableLayout(2);
	tb.setWidth("100%");
	tb.setBorder(1);
	projectTable.setLayout(tb);
	projectTable.setBorders(true);

	TableData td = new TableData();
	td.setVerticalAlign(VerticalAlignment.TOP);
	// TODO: why must I do this, just to get the two forms to share space?
	td.setColspan(1);
	td.setWidth("400px");
	
	// left side of project table lets you pick what project & session you want
	final FormPanel projectForm = new FormPanel();
	projectForm.setHeading("Project");
	projectForm.setBorders(true);

	
	// the project picker goes in this left-most form panel
	projects.setFieldLabel("Project");
	projects.setTriggerAction(TriggerAction.ALL);
	// when a project gets picked, populate the sessions combo
	projects.addListener(Events.Valid, new Listener<BaseEvent>() {
	  	public void handleEvent(BaseEvent be) {
	  		//GWT.log("projects Events.Valid", null);
	  		// what are the sessions we can view?
	  		if (updateProjectSessions()) {
		  		// get all the time accounting info!
		  		getProjectTimeAccounting();
	  		}
	   	}
	});
	updatePCodeOptions();
	projectForm.add(projects);
    
	
	// followed by the session picker
	sessions.setFieldLabel("Session");
	sessions.setTriggerAction(TriggerAction.ALL);
	sessions.addListener(Events.Valid, new Listener<BaseEvent>() {
	  	public void handleEvent(BaseEvent be) {
	  		//GWT.log("session Events.Valid", null);
	  		// now that a session has been picked, what periods can we view?
	  		updateSessionPeriods();
	   	}
	});	
	projectForm.add(sessions);

	Button saveProj = new Button("Save Project Time Accounting");
	saveProj.addListener(Events.OnClick, new Listener<BaseEvent>() {
		public void handleEvent(BaseEvent be) {
			//GWT.log("Save!", null);
			// save changes to project times, and display new time accounting
			sendProjectAllotments();
		}
	});
	projectForm.add(saveProj);
    
	projectTable.add(projectForm, td);

	// The right side of the project table includes allotments by grade.
	// a FieldSet for each grade allotment - the first always gets shown, not the second
	FormPanel projectForm2 = new FormPanel();
	projectForm2.setHeading("Allotments");
	projectForm2.setBorders(true);	
	projectForm2.add(projGrade1);
	projGrade2.setVisible(false);
	projectForm2.add(projGrade2);
	
	projectTable.add(projectForm2, td);
	
    project.add(projectTable, new RowData(1, -1, new Margins(4)));
    
	// the project time accounting panel goes next
    projectTimeAccounting.setHeading("Project Time Accounting");
    project.add(projectTimeAccounting);
    
    // now add the session panel
	session.setLayout(new RowLayout(Orientation.VERTICAL)); //FitLayout());
	session.setBorders(true);
	session.setVisible(false);

	// TODO: a table is used to place two forms side by side
	LayoutContainer sessionTable = new LayoutContainer();
	TableLayout tbSess = new TableLayout(2);
	tbSess.setWidth("100%");
	tbSess.setBorder(1);
	sessionTable.setLayout(tbSess);
	sessionTable.setBorders(true);

	TableData tdSess = new TableData();
	tdSess.setVerticalAlign(VerticalAlignment.TOP);
	
	// TODO: why must I do this, just to get the two forms to share space?
	tdSess.setColspan(1);
	tdSess.setWidth("400px");
	
	final FormPanel sessionForm = new FormPanel();
	sessionForm.setHeading("Session");
	sessionForm.setBorders(true);

	// what's the current session?
	sessionName.setValue("");
	sessionName.setReadOnly(true);
	// TODO: readonly using background color?
	sessionName.setStyleAttribute("color", "grey");
	sessionName.setFieldLabel("Session Name");
	sessionForm.add(sessionName);

	// followed by the period picker
	periods.setTriggerAction(TriggerAction.ALL);
	periods.setFieldLabel("Period");
	periods.addListener(Events.Valid, new Listener<BaseEvent>() {
	  	public void handleEvent(BaseEvent be) {
	  		//GWT.log("period Events.Valid", null);
	  		// a period has been picked! display it!
	  		updatePeriod();
	   	}
	});	
	sessionForm.add(periods);

	sessionTable.add(sessionForm, tdSess);
	
	final FormPanel sessionForm2 = new FormPanel();
	sessionForm2.setHeading("Allotment");
	sessionForm2.setBorders(true);
	
	sessionGrade.setReadOnly(true);
	// TODO: read only using background color?
	sessionGrade.setStyleAttribute("color", "grey");
	sessionGrade.setFieldLabel("Grade");
	sessionForm2.add(sessionGrade);
	
	sessionTime.setFieldLabel("Alloted (Hrs)");
	sessionTime.setFormat(NumberFormat.getFormat("#0.00"));
	sessionTime.setValidator(new DSSTimeValidator()); 	
	sessionTime.addListener(Events.Blur, new Listener<BaseEvent>() {
		@Override
		public void handleEvent(BaseEvent be) {
            //GWT.log("Blur!", null);	
            //GWT.log("original value: " + sessionTime.getOriginalValue().toString(), null);
            //GWT.log("value: " + sessionTime.getValue().toString(), null);
            if (sessionTime.getValue().doubleValue() != sessionTime.getOriginalValue().doubleValue()) {
		        //GWT.log("Value changed!", null);
		        sessionTime.setStyleAttribute("color", "red");
	        } else {
		        sessionTime.setStyleAttribute("color", "black");
	        }
		}
	});	
	
	sessionForm2.add(sessionTime);
	
	Button saveSess = new Button("Save Session Time Accounting");
	saveSess.addListener(Events.OnClick, new Listener<BaseEvent>() {
		public void handleEvent(BaseEvent be) {
			//GWT.log("Save Session!", null);
			// save changes to project times, and display new time accounting
			sendSessionAllotment();
		}
	});
	sessionForm2.add(saveSess);
	
	sessionTable.add(sessionForm2, tdSess);
	
    
    periodSummary.setVisible(false); 
    periodSummary.setParent(this);

    // display session basics, then it's time accounting details
    session.add(sessionTable, new RowData(1, -1, new Margins(4)));
    sessionTimeAccounting.setHeading("Session Time Accounting");
	session.add(sessionTimeAccounting, new RowData(1, -1, new Margins(4)));
	// then the period info
	session.add(periodSummary, new RowData(1, -1, new Margins(4)));
    
    project.add(session, new RowData(1, -1, new Margins(4)));
    
	add(project, new FitData(10));
	
  }

// saves off changes to project allotments, then redisplays latest time accounting info
private void sendProjectAllotments() {
	// TODO: boy, this really sucks.  I really need to learn how to send heirarchal data!
	// send a JSON request for each grade being updated.
	// First grade is always visible:
    sendProjectAllotment(projGrade1, projectTimeAccounting.getDescription());
    if (projGrade2.isVisible()) {
        sendProjectAllotment(projGrade2, projectTimeAccounting.getDescription());
    }
}

// handles one grade's allotment numbers at a time
private void sendProjectAllotment(ProjAllotmentFieldSet fs, String desc) {
	
	String url = "/projects/time_accounting/" + projects.getSimpleValue();
	HashMap <String, Object> keys = new HashMap<String, Object>();

	keys.put("grade", fs.getGrade());
	keys.put("total_time", fs.getAllotment());
	keys.put("description", desc);
	
	JSONRequest.post(url, keys, new JSONCallbackAdapter() {
		// this url returns all the time accounting for the whole proj., 
		// so use it to update the whole UI
		public void onSuccess(JSONObject json) {
			timeAccountingJson = json;
			//populateProjTimeAccounting(json);
			setTimeAccountingFromJSON(json);
		}
	});
}

//saves off changes to session allotments, then redisplays latest time accounting info
private void sendSessionAllotment() {
	
	String url = "/sessions/time_accounting/" + sessionName.getValue();
	HashMap <String, Object> keys = new HashMap<String, Object>();

	keys.put("total_time", sessionTime.getValue().doubleValue());
	keys.put("description", sessionTimeAccounting.getDescription());
	//GWT.log("sending to url: " + url, null);
	
	JSONRequest.post(url, keys, new JSONCallbackAdapter() {
		// this url returns all the time accounting for the whole proj., 
		// so use it to update the whole UI
		public void onSuccess(JSONObject json) {
			//GWT.log("sendSessoinAllotment onsuccess; setting json", null);
			timeAccountingJson = json;
			//populateProjTimeAccounting(json);
			setTimeAccountingFromJSON(json);
		}
	});
}

// retrieves the JSON that describes every detail about time accounting for given project
protected void getProjectTimeAccounting() {
    
	JSONRequest.get("/projects/time_accounting/" + projects.getSimpleValue()
		      , new JSONCallbackAdapter() {
		public void onSuccess(JSONObject json) {
			timeAccountingJson = json;
			// take the project level time accounting info, and populate the panel w/ it.
			//populateProjTimeAccounting(json);
			setTimeAccountingFromJSON(json);
            //GWT.log("/projects/time_accounting onSuccess", null);          
		}
	});    			
}

// given the JSON which has all the time accounting info in it, update the current
// project and session (note: periods are handled separately)
public void setTimeAccountingFromJSON(JSONObject json) {
	// make sure the project & session panels get updated;
	// periods get updated by default
	//GWT.log("setTimeAccountingFromJSON!", null);
	timeAccountingJson = json;
	populateProjTimeAccounting(json);
	String name = sessionName.getValue();
	//GWT.log("setting sess: ? " + name, null);
	if (name != null & name != "") {
		populateSessTimeAccounting(json, name);
	}
}

//given the JSON which has all the time accounting info in it, update the current project
private void populateProjTimeAccounting(JSONObject json) {
	
	projGrade2.setVisible(false);
	JSONArray times = json.get("times").isArray();
	for (int i = 0; i < times.size(); ++i){
		// TODO: we only can deal with up to two grades right now! this code sucks!
		JSONObject time = times.get(i).isObject();
		if (i == 0) {
			// first grade field list
			projGrade1.setValues(time);
		} 
		if (i == 1) {
			// second grade field list - make it visible!
			projGrade2.setVisible(true);
			projGrade2.setValues(time);
			
		}
	}	
	projectTimeAccounting.setValues(json);
}

//given the JSON which has all the time accounting info in it, update the current session
private void populateSessTimeAccounting(JSONObject json, String sessName) {
	// find the section of the json that has our session in it:
    JSONArray names = json.get("sessions").isArray();
    for (int i = 0; i < names.size(); ++i) {
    	//Period period = Period.parseJSON(names.get(i).isObject());
    	JSONObject session = names.get(i).isObject();
    	String name = session.get("name").isString().stringValue();
    	if (name.equals(sessName)) {
    		// got it!
    		//GWT.log("matched session name " + sessName, null);
    		sessionName.setValue(sessName);
    		sessionGrade.setValue(session.get("grade").isNumber().doubleValue());
    		double time = session.get("total_time").isNumber().doubleValue();
    		sessionTime.setValue(time); 
    		sessionTime.setOriginalValue(time);
    		sessionTime.setStyleAttribute("color", "black");
    		sessionTimeAccounting.setValues(session);
    		
    	}	
    }	
}

// a period has been selected - so update the period summary panel
protected void updatePeriod() {
	//GWT.log("updatePeriod", null);
	// what's the period id for chosen period (displayed using time info)?
	String name = periods.getSimpleValue();
	int periodId = periodInfo.get(name);
	// get this period from the server and update this panel
	periodSummary.updatePeriodForm(periodId);
	periodSummary.setVisible(true);
}

// a session has been selected - populate it's panel 
protected void updateSessionPeriods() {
	
	//GWT.log("updateSessionPeriods", null);
	// show the session panel
	session.setVisible(true);
	
    // what are the periods for this session?
	String name = sessions.getSimpleValue();
	String pcode = projects.getSimpleValue();
	updatePeriodOptions(pcode, name);
	
	// extract the time accounting info for session just picked, and populate
	// it's time accounting panel
    populateSessTimeAccounting(timeAccountingJson, name);
    
	// hide the period panel until a period is chosen
	periodContainer.setVisible(false);
	periodSummary.setVisible(false);
	
}


// a session has been selected, so now what are the periods that we can choose from?
private void updatePeriodOptions(final String pcode, final String sessionName) {
    //GWT.log("updatePeriodOptions", null);
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
				// the periods drop down is populated w/ descriptions of the periods
				String p = ps.get(i).toString().replace('"', ' ').trim();
				String id = ids.get(i).toString().replace('"', ' ').trim();
				// the labels displayed need to be unique, so we add the period id at the end
				String label = p + " (" + id + ")";
				periods.add(label);
				// we need to save the mapping from 'description' to 'id'
				periodInfo.put(label, Integer.parseInt(id));
				
			}
			//session.setVisible(false);
		}
	});    	
}

// a project has been selected - populate the panel w/ info, and 
// display candidate sessions to also view.
protected boolean updateProjectSessions() {
	//GWT.log("updateProjectSessions", null);
	// update the sessions drop down and clear the current selection
	String pcode = projects.getSimpleValue();
	
	// don't bother if it doesn't even look like a valid pcode
	if ((pcode == null) || (pcode.equals(new String("")))) {
		Window.alert("You must select a valid project code.");
		return false;
	}
	
	updateSessionOptions(pcode);
	// hide the session panel until a session is chosen
	session.setVisible(false);
	
	return true;
}

// gets the session names from the server and populates the session combobox
private void updateSessionOptions(final String pcode) {
    //GWT.log("updateSessionOptions", null);
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
			JSONArray names = json.get("session names").isArray();
			for (int i = 0; i < names.size(); ++i){
				String name = names.get(i).toString().replace('"', ' ').trim();
				sessions.add(name);
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
