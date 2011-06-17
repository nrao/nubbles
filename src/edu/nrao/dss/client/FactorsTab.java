// Copyright (C) 2011 Associated Universities, Inc. Washington DC, USA.
// 
// This program is free software; you can redistribute it and/or modify
// it under the terms of the GNU General Public License as published by
// the Free Software Foundation; either version 2 of the License, or
// (at your option) any later version.
// 
// This program is distributed in the hope that it will be useful, but
// WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
// General Public License for more details.
// 
// You should have received a copy of the GNU General Public License
// along with this program; if not, write to the Free Software
// Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.
// 
// Correspondence concerning GBT software should be addressed as follows:
//       GBT Operations
//       National Radio Astronomy Observatory
//       P. O. Box 2
//       Green Bank, WV 24944-0002 USA

package edu.nrao.dss.client;

import com.extjs.gxt.ui.client.Style;
import com.extjs.gxt.ui.client.Style.Scroll;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.TabPanel;
import com.extjs.gxt.ui.client.widget.TabItem;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.ScrollPanel;

import edu.nrao.dss.client.widget.FactorGrid;

public class FactorsTab implements FactorsDisplay {
	
	private TabPanel panel;
	
	public FactorsTab(TabPanel tp) {
		panel = tp;
	}
	
	@Override
	public void show(String title, String banner, String[] headers, String[][] factors) {
		FactorGrid grid = new FactorGrid(factors.length, headers.length, headers, factors);
        TabItem item = new TabItem(title);
        ContentPanel cp = createContentPanel(banner, grid);
        item.add(cp);
        item.getHeader().setToolTip(title);
        item.setLayout(new FitLayout());
        item.setClosable(true);
        panel.add(item);
	}
	
	public ContentPanel createContentPanel(String banner, FactorGrid grid) {
		ContentPanel panel = new ContentPanel();
		panel.setHeading(banner);
		panel.setLayout(new FitLayout());
		
		ScrollPanel sp = new ScrollPanel(grid);
		sp.setAlwaysShowScrollBars(true);  //  Use this instead, since Google's shit's all broke (Chrome).
		panel.add(sp);
		//  No worky in Chrome :(
		//panel.setScrollMode(Scroll.AUTO);
		
		
		return panel;
	}

}
