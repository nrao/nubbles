package edu.nrao.dss.client;

import com.extjs.gxt.ui.client.Style.Orientation;
import com.extjs.gxt.ui.client.Style.Scroll;
import com.extjs.gxt.ui.client.util.Margins;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.extjs.gxt.ui.client.widget.layout.RowData;
import com.extjs.gxt.ui.client.widget.layout.RowLayout;

public class SessionPage extends ContentPanel {
	
    //private final WindowExplorer  we       = new WindowExplorer();
    private final SessionInfoPanel sp = new SessionInfoPanel();
    private final WindowsInfoPanel  wp = new WindowsInfoPanel();
    
	public SessionPage() {
		initLayout();
		initListeners();
	}

	private void initLayout() {
		setLayout(new RowLayout(Orientation.VERTICAL));

		setScrollMode(Scroll.AUTO);
		setBorders(false);
		setHeaderVisible(false);

		sp.setWindowsInfoPanel(wp);
        add(sp);

        add(wp);
        
	}
	
	private void initListeners() {
		
	}
}
