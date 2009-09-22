package edu.nrao.dss.client;

import com.extjs.gxt.ui.client.Style.LayoutRegion;
import com.extjs.gxt.ui.client.Style.Orientation;
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

public class TimeAccounting extends ContentPanel{

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
	
	//project.setHeading("North: Control Widgets");	
	
//    TextArea projectComments = new TextArea();
//    projectComments.setValue("dog");
//    projectComments.setVisible(true);
//    //project.add(projectComments, new FitData(5));
//    
//    add(projectComments); //, new FitData(10));
	
	// always there: the one project that we're looking at
	// here's the parent project container
	//final LayoutContainer project = new LayoutContainer();
	//north.setHeading("North: Control Widgets");
	//TableLayout tl = new TableLayout(2);
	//tl.setBorder(1);
	//tl.setWidth("100%");
	//project.setLayout(tl);

	
	// now place some controls in the first column
	final FormPanel projectFormLeft = new FormPanel();
	projectFormLeft.setHeading("Project");
	projectFormLeft.setBorders(true);
	//northCalendar.setHeight(300);

	//td.setHeight("100%");
	//td.setVerticalAlign(VerticalAlignment.TOP);
	
	// the project picker goes in this left-most form panel
	SimpleComboBox<String> projects = new SimpleComboBox<String>();
	projects.setFieldLabel("Project");
	projects.add("proj1");
	projects.add("proj2");
	projectFormLeft.add(projects);
	
	// followed by the session picker
	SimpleComboBox<String> sessions = new SimpleComboBox<String>();
	sessions.setFieldLabel("Session");
	sessions.add("sess1");
	sessions.add("sess2");
	projectFormLeft.add(sessions);
	
	//TableData td = new TableData();
	//td.setWidth("35%");	
	//project.add(projectFormLeft, td);
	

	// now place some controls in the second column
	//final FormPanel projectFormRight = new FormPanel();
	//projectFormRight.setHeading("Project Comments");
	//projectFormRight.setBorders(true);
	
	//northCalendar.setHeight(300);
	//td.setHeight("100%");
	//td.setVerticalAlign(VerticalAlignment.TOP);
	
	// the project time accounting comments goes in this second form panel
    TextArea projectComments = new TextArea();
    projectComments.setFieldLabel("Comments");
    projectFormLeft.add(projectComments);

	//TableData td2 = new TableData();
	//td2.setWidth("50%");
	//project.add(projectFormRight, td2);

	
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
    
//    // now add the session panel
    LayoutContainer session = new LayoutContainer();
	session.setLayout(new RowLayout(Orientation.VERTICAL)); //FitLayout());
	session.setBorders(true);

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
	SimpleComboBox<String> periods = new SimpleComboBox<String>();
	periods.setFieldLabel("Period");
	periods.add("p1");
	periods.add("p2");
	sessionForm.add(periods);
	
	// the project time accounting comments goes in this second form panel
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
    
    session.add(sessionForm, new RowData(1, -1, new Margins(4)));
	session.add(sessionTA, new RowData(1, -1, new Margins(4)));

    
    
    project.add(session, new RowData(1, -1, new Margins(4)));
    
	add(project, new FitData(10));
  }
	

}
