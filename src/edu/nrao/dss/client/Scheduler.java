package edu.nrao.dss.client;

import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.event.TabPanelEvent;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.TabItem;
import com.extjs.gxt.ui.client.widget.TabPanel;
import com.extjs.gxt.ui.client.widget.Viewport;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.RootPanel;

public class Scheduler extends Viewport implements EntryPoint {
    public void onModuleLoad() {
        initLayout();
    }

    private void initLayout() {
        //setLayout(new FitLayout());

        // project explorer tab
        tabPanel.add(addTab(pe, "Project Explorer", "Define and edit projects."));
        pe.setParent(this);
        
        // session explorer tab - we need to update the project info when it
        // comes into focus
        TabItem seTab = addTab(se, "Session Explorer", "Define and edit sessions.");
        seTab.addListener(Events.Select, new SelectionListener<TabPanelEvent>(){
        	@Override
        	public void componentSelected(TabPanelEvent tpe){
        		SessionColConfig pcodeConfig = (SessionColConfig) se.getPcodeConfig();
        		pcodeConfig.updatePCodeOptions();
        		se.loadData();
        	}
        });
        tabPanel.add(seTab);

        tabPanel.add(addTab(we, "Window Explorer", "Define and edit windows."));
        tabPanel.add(addTab(wc, "Window Calendar", "Display Windows."));
        
        // schedule tab - we need to update the session/project info when it
        // comes into focus
        TabItem schTab = addTab(sch, "Schedule", "Manage the Schedule");
        schTab.addListener(Events.Select, new SelectionListener<TabPanelEvent>(){
        	@Override
        	public void componentSelected(TabPanelEvent tpe){
        		PeriodColConfig sessionConfig = (PeriodColConfig) sch.west.pe.getSessionConfig();
        		sessionConfig.updateSessionOptions();
        		sch.west.pe.loadData();
        	}
        });
        tabPanel.add(schTab);
        
        TabItem taTab = addTab(ta, "Time Accounting", "Manage Time Accounting");
        taTab.addListener(Events.Select, new SelectionListener<TabPanelEvent>(){
	    	@Override
	    	public void componentSelected(TabPanelEvent tpe){
	    		ta.updatePCodeOptions();
	    	}
        });        
        tabPanel.add(taTab);
        
        TabItem ppTab = addTab(pp, "Project Page", "Per Project");
        ppTab.addListener(Events.Select, new SelectionListener<TabPanelEvent>(){
        	@Override
        	public void componentSelected(TabPanelEvent tpe){
        		pp.updatePCodeOptions();
        	}
        });	
        tabPanel.add(ppTab);
        
        tabPanel.add(addTab(rs, "Receiver Schedule", "Change the Receiver Schedule"));
        
        // TODO Why does not "tabPanel.setAutoHeight(true);? work?
        tabPanel.setHeight(920);
        
        // Factor access
        factors = new Factors(sch.getFactorsDlg(), new FactorsTab(tabPanel));

        RootPanel rp = RootPanel.get();
        rp.add(new Image("http://www.gb.nrao.edu/~dss/images/banner.jpg"));
        rp.add(new Anchor("Helpdesk ", true, "mailto:helpdesk-dss@gb.nrao.edu"));
        rp.add(new Anchor(" ICAL", "/projects/ical"));
        rp.add(tabPanel);
    }
    
    private TabItem addTab(ContentPanel container, String title, String toolTip) {
        TabItem item = new TabItem(title);
        item.setId(title);
        item.add(container);
        item.getHeader().setToolTip(toolTip);
        item.setLayout(new FitLayout());
        return item;
    }

    // brings focus to the Project Page tab and selects given period.
    // this is called by any child widget (ex: Project Explorer) 
    public void showProject(String pcode) {
        TabItem ppTab =	tabPanel.findItem("Project Page", false);
    	tabPanel.setSelection(ppTab);
    	pp.setProject(pcode);
    }
    
    private final TabPanel         tabPanel = new TabPanel();
    private final ProjectExplorer  pe       = new ProjectExplorer();
    private final SessionExplorer  se       = new SessionExplorer();
    private final WindowExplorer  we       = new WindowExplorer();
    private final WindowCalendar  wc       = new WindowCalendar();
    private final Schedule         sch      = new Schedule();
    private final TimeAccounting   ta       = new TimeAccounting();
    private Factors                factors;
    private final ProjectPage      pp       = new ProjectPage();
    private final ReceiverSchedule rs       = new ReceiverSchedule();
}
