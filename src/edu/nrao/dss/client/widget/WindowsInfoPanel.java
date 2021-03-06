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

package edu.nrao.dss.client.widget;


import java.util.HashMap;
import java.util.List;

import com.extjs.gxt.ui.client.Style.Orientation;
import com.extjs.gxt.ui.client.event.BaseEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.FormPanel;
import com.extjs.gxt.ui.client.widget.form.SimpleComboBox;
import com.extjs.gxt.ui.client.widget.form.TextField;
import com.extjs.gxt.ui.client.widget.form.ComboBox.TriggerAction;
import com.extjs.gxt.ui.client.widget.layout.AccordionLayout;
import com.extjs.gxt.ui.client.widget.layout.RowLayout;
import com.extjs.gxt.ui.client.widget.toolbar.ToolBar;
import com.google.gwt.core.client.GWT;
import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONObject;


// This class is responsible for displaying all the info that the the window explorer USED to display
// for windows, before they got multiple periods and date ranges.  But for all windows belonging to a single session.

public class WindowsInfoPanel extends PeriodGroupsInfoPanel {
	
	public WindowsInfoPanel(String url, String type) {
		super(url, type);
	}

    public void getWindows() {
    	this.getPeriodGroups();
    }
    
    public void getWindows(int id, String handle) {
    	this.getPeriodGroups(id, handle);
    }	
	
	protected void displayPeriodGroups(JSONObject json) {
		// get ready to populate the windows
		ids.clear();
	    removeAll();
	    
		JSONArray winsJson = json.get("windows").isArray();
		setHeading("Windows (" + Integer.toString(winsJson.size()) + ")");
		
		for (int i = 0; i < winsJson.size(); ++i){
			
			// turn each window JSON into a window panel
			JSONObject winJson =  winsJson.get(i).isObject();
			int id = (int) winJson.get("id").isNumber().doubleValue();
		    ids.put(Integer.toString(id), id);
			WindowInfoPanel w = new WindowInfoPanel(winJson, url, type);

			add(w);
			
			// display the window as it comes in
			// Q: should we do this just once, instead of once per panel?
			layout();
			
			// you have to do the collapse after the layout, or shit don't work
			w.collapse();
		}		
		
		//layout();
	}

}

