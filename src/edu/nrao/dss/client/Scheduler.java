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
import com.google.gwt.user.client.ui.RootPanel;

public class Scheduler extends Viewport implements EntryPoint {
    public void onModuleLoad() {
        initLayout();
    }

    private void initLayout() {
        setLayout(new FitLayout());

        // project explorer tab
        tabPanel.add(addTab(pe, "Project Explorer", "Define and edit projects."));
        
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
        
        // schedule tab
        tabPanel.add(addTab(sch, "Schedule", "Manage the Schedule"));
        
        tabPanel.setHeight(600);

        RootPanel.get().add(tabPanel);
    }
    
    private TabItem addTab(ContentPanel container, String title, String toolTip) {
        TabItem item = new TabItem(title);
        item.add(container);
        item.getHeader().setToolTip(toolTip);
        item.setLayout(new FitLayout());
        return item;
    }

    private final TabPanel         tabPanel = new TabPanel();
    private final ProjectExplorer  pe       = new ProjectExplorer();
    private final SessionExplorer  se       = new SessionExplorer();
    private final Schedule         sch      = new Schedule();
}
