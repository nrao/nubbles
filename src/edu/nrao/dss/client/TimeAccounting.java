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

import edu.nrao.dss.client.util.JSONCallbackAdapter;
import edu.nrao.dss.client.util.JSONRequest;
import edu.nrao.dss.client.util.JSONRequestCache;
import edu.nrao.dss.client.util.TimeUtils;
import edu.nrao.dss.client.widget.PeriodSummaryPanel;
import edu.nrao.dss.client.widget.ProjectTimeAccountPanel;
import edu.nrao.dss.client.widget.ProjectTimePanel;
import edu.nrao.dss.client.widget.SessionTimeAccountPanel;
import edu.nrao.dss.client.widget.SessionTimePanel;
import edu.nrao.dss.client.widget.form.DSSTimeValidator;
import edu.nrao.dss.client.widget.form.ProjAllotmentFieldSet;

// This panel (one of the main tabs) is responsible for showing the time accounting at each level of:
// project -> session -> period
// Each level shares some of the same time accounting fields, so for each level, there are essentially two panels:
//    * panel with info specific to the level (project, session, or period)
//    * panel with common time accounting fields
// The user first picks their project in the project panel, then their session from that project's sessions,
// then that session's periods.  Each choice displays the new time accounting info.

public class TimeAccounting extends ContentPanel {


    private ProjectTimePanel projectTimePanel = new ProjectTimePanel();
    private SessionTimePanel sessionTimePanel = new SessionTimePanel();
    private PeriodSummaryPanel periodSummary = new PeriodSummaryPanel(null);
    
    // period level
    //final LayoutContainer periodContainer = new LayoutContainer();
	
	// stores all the time accounting info we get from the server
	private JSONObject timeAccountingJson = new JSONObject();
	
	public TimeAccounting() {
		initLayout();
    }	

	protected void initLayout() {
		setLayout(new RowLayout(Orientation.VERTICAL));		
		setHeight(920);
		setHeaderVisible(false);
		
		// so we can always see everything 
		setScrollMode(Scroll.ALWAYS);

		projectTimePanel.setParent(this);
		add(projectTimePanel);
		
		sessionTimePanel.setParent(this);
		add(sessionTimePanel);
		
		periodSummary.setParent(this);
		add(periodSummary);
		
		noProjectSelected();
	  }
	
	public void noProjectSelected() {
		sessionTimePanel.setVisible(false);
		periodSummary.setVisible(false);
	}
	
	public void projectSelected(String pcode) {
		// the session panel should now be visible, populated w/ project's sessions
		sessionTimePanel.setVisible(true);
		periodSummary.setVisible(false);
		sessionTimePanel.setNewSessions(pcode);
	}
	
	public void sessionSelected(String sessionName) {
		periodSummary.setVisible(true);
		periodSummary.setNewPeriods(projectTimePanel.projects.getSimpleValue(), sessionName);
	}
	
	public void updatePCodeOptions() { 
		projectTimePanel.updatePCodeOptions();
	}
	
	// given the JSON which has all the time accounting info in it, update the current
	// project and session (note: periods are handled separately)
	public void setTimeAccountingFromJSON(JSONObject json) {
       // make sure the project & session panels get updated;
       // periods get updated by default
       timeAccountingJson = json;
       projectTimePanel.populateProjTimeAccounting(json);
       String name = sessionTimePanel.sessionName.getValue();
       if (name != null & name != "") {
           sessionTimePanel.populateSessTimeAccounting(json, name);
       }        
	}
}
