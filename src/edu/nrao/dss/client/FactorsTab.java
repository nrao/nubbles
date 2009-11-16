package edu.nrao.dss.client;

import com.extjs.gxt.ui.client.Style.Scroll;
import com.extjs.gxt.ui.client.widget.TabPanel;
import com.extjs.gxt.ui.client.widget.TabItem;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.google.gwt.core.client.GWT;

public class FactorsTab implements FactorsDisplay {
	
	private TabPanel panel;
	
	public FactorsTab(TabPanel tp) {
		panel = tp;
	}
	
	@Override
	public void show(String title, String[] headers, String[][] factors) {
		GWT.log("FactorsWindows.show", null);
		FactorGrid grid = new FactorGrid(factors.length, headers.length, headers, factors);
        TabItem item = new TabItem(title);
        item.add(grid);
        item.getHeader().setToolTip(title);
        item.setLayout(new FitLayout());
        item.setScrollMode(Scroll.ALWAYS);
        item.setClosable(true);
        panel.add(item);
	}

}
