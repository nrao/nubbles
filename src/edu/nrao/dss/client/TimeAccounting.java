package edu.nrao.dss.client;

import java.util.HashMap;
import java.util.List;

import com.extjs.gxt.ui.client.Style.LayoutRegion;
import com.extjs.gxt.ui.client.Style.Orientation;
import com.extjs.gxt.ui.client.event.BaseEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.util.Margins;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.form.FormPanel;
import com.extjs.gxt.ui.client.widget.form.SimpleComboBox;
import com.extjs.gxt.ui.client.widget.form.TextArea;
import com.extjs.gxt.ui.client.widget.form.TextField;
import com.extjs.gxt.ui.client.widget.layout.BorderLayoutData;
import com.extjs.gxt.ui.client.widget.layout.FitData;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.extjs.gxt.ui.client.widget.layout.RowData;
import com.extjs.gxt.ui.client.widget.layout.RowLayout;
import com.extjs.gxt.ui.client.widget.layout.TableData;
import com.extjs.gxt.ui.client.widget.layout.TableLayout;
import com.google.gwt.core.client.GWT;

import edu.nrao.dss.client.util.TimeUtils;

public class TimeAccounting extends ContentPanel{

	final SimpleComboBox<String> projects = new SimpleComboBox<String>();
	final SimpleComboBox<String> sessions = new SimpleComboBox<String>();
    final LayoutContainer session = new LayoutContainer();
	final SimpleComboBox<String> periods = new SimpleComboBox<String>();
    final LayoutContainer period = new LayoutContainer();
	
	
	public TimeAccounting() {
		super();
		initLayout();
    }	

protected void initLayout() {
	
	setLayout(new FitLayout());
	
	// bells & whistles for this content panel
	//setHeading("Project Time Accounting");
	
	setCollapsible(false);
	setBodyBorder(true);
	setFrame(true);
	setHeaderVisible(true);
	setBodyStyle("backgroundColor: white;");

	final LayoutContainer project = new LayoutContainer();
	project.setLayout(new RowLayout(Orientation.VERTICAL)); //FitLayout());
	project.setBorders(true);
	
	// now place some controls in the first column
	final FormPanel projectFormLeft = new FormPanel();
	projectFormLeft.setHeading("Project");
	projectFormLeft.setBorders(true);
	
	// the project picker goes in this left-most form panel
	//final SimpleComboBox<String> projects = new SimpleComboBox<String>();
	projects.setFieldLabel("Project");
	projects.add("proj1");
	projects.add("proj2");
	// when a project gets picked, populate the sessions combo
	projects.addListener(Events.Valid, new Listener<BaseEvent>() {
	  	public void handleEvent(BaseEvent be) {
	  		GWT.log("projects Events.Valid", null);
	  		updateProjectSessions();
	   	}
	});	
	projectFormLeft.add(projects);
    
	
	// followed by the session picker
	//SimpleComboBox<String> sessions = new SimpleComboBox<String>();
	sessions.setFieldLabel("Session");
	//sessions.add("sess1");
	//sessions.add("sess2");
	sessions.addListener(Events.Valid, new Listener<BaseEvent>() {
	  	public void handleEvent(BaseEvent be) {
	  		GWT.log("session Events.Valid", null);
	  		updateSessionPeriods();
	   	}
	});	
	
	projectFormLeft.add(sessions);
	

	// the project time accounting comments goes in this second form panel
    TextArea projectComments = new TextArea();
    projectComments.setFieldLabel("Comments");
    projectFormLeft.add(projectComments);

    ContentPanel projectTimeAccounting = new ContentPanel();
    projectTimeAccounting.setLayout(new TableLayout(5));
    projectTimeAccounting.setHeaderVisible(true);
    projectTimeAccounting.setHeading("Project Time Accounting");
    projectTimeAccounting.setBorders(true);
    
    TextField tf1 = new TextField();
    tf1.setValue("time accounting 1.");
    tf1.setReadOnly(true);
    projectTimeAccounting.add(tf1);
    
    TextField tf2 = new TextField();
    tf2.setValue("time accounting 2.");
    tf2.setReadOnly(true);
    projectTimeAccounting.add(tf2);
    
	project.add(projectFormLeft, new RowData(1, -1, new Margins(4)));
    project.add(projectTimeAccounting, new RowData(1, -1, new Margins(4)));
    
    // now add the session panel
    //LayoutContainer session = new LayoutContainer();
	session.setLayout(new RowLayout(Orientation.VERTICAL)); //FitLayout());
	session.setBorders(true);
	session.setVisible(false);

	// first use form to affect this session and pick periods
	final FormPanel sessionForm = new FormPanel();
	sessionForm.setHeading("Session");
	sessionForm.setBorders(true);
	
	// the session picker goes in this left-most form panel
	TextField sessionName = new TextField();
	sessionName.setValue("This Session");
	sessionName.setReadOnly(true);
	sessionName.setFieldLabel("Session Name");
	sessionForm.add(sessionName);
	
	// followed by the period picker
	//SimpleComboBox<String> periods = new SimpleComboBox<String>();
	periods.setFieldLabel("Period");
	//periods.add("p1");
	//periods.add("p2");
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
    sessionForm.add(sessionComments);

    // the session form is followed by a panel w/ the session time accounting
    ContentPanel sessionTA = new ContentPanel();
    sessionTA.setLayout(new TableLayout(5));
    sessionTA.setHeaderVisible(true);
    sessionTA.setHeading("Session Time Accounting");
    
    TextField stf1 = new TextField();
    stf1.setValue("time accounting 1.");
    stf1.setReadOnly(true);
    sessionTA.add(stf1);
    
    TextField stf2 = new TextField();
    stf2.setValue("time accounting 2.");
    stf2.setReadOnly(true);
    sessionTA.add(stf2);
    
	// the session panel contains a period panel (just like the project contained a session)
    //LayoutContainer period = new LayoutContainer();
	period.setLayout(new RowLayout(Orientation.VERTICAL)); //FitLayout());
	period.setBorders(true);
	period.setVisible(false);

	// here's the form for setting all the period time accounting stuff 
	final FormPanel periodForm = new FormPanel();
	periodForm.setHeading("Period");
	periodForm.setBorders(true);
	
	TextField periodName = new TextField();
	periodName.setValue("This Period");
	periodName.setReadOnly(true);
	periodName.setFieldLabel("Session Name");
	periodForm.add(periodName);
	
	SimpleComboBox<String> times = new SimpleComboBox<String>();
	final HashMap<String, Integer> timeChoices = new HashMap<String, Integer>();
	for (int m = 0; m < 24*60; m += 15) {
		String key = TimeUtils.min2sex(m);
		timeChoices.put(key, m);
		times.add(key);
	}	
	times.setFieldLabel("times (Hrs):");
	times.setToolTip("Set the Hrs for this type of time");
	periodForm.add(times);
	
	
	
	period.add(periodForm, new RowData(1, -1, new Margins(4)));
	
    session.add(sessionForm, new RowData(1, -1, new Margins(4)));
	session.add(sessionTA, new RowData(1, -1, new Margins(4)));
    session.add(period, new RowData(1, -1, new Margins(4)));
    
    
    project.add(session, new RowData(1, -1, new Margins(4)));
    
	add(project, new FitData(10));
  }

protected void updatePeriod() {
	GWT.log("updatePeriod", null);
	// show the period panel
	period.setVisible(true);

	
}

protected void updateSessionPeriods() {
	GWT.log("updateSessionPeriods", null);
	// show the session panel
	session.setVisible(true);
	// update the sessions drop down 
	String sessionName = sessions.getSimpleValue();
	periods.clearSelections();
	periods.removeAll();
	periods.add(sessionName);
	periods.add(sessionName);
	// hide the period panel until a period is choosen
	period.setVisible(false);
	

}

protected void updateProjectSessions() {
	GWT.log("updateProjectSessions", null);
	// update the sessions drop down and clear the current selection
	String pcode = projects.getSimpleValue();
	sessions.clearSelections();
	sessions.removeAll();
	sessions.add(pcode);
	sessions.add(pcode);
	// hide the session panel until a session is choosen
	session.setVisible(false);
}
	

}
