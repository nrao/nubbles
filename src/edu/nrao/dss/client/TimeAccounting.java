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


import com.extjs.gxt.ui.client.Style.Orientation;
import com.extjs.gxt.ui.client.Style.Scroll;

import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.layout.RowLayout;
import com.google.gwt.json.client.JSONObject;

import edu.nrao.dss.client.data.OptionsFilter;
import edu.nrao.dss.client.util.ObserverContentPanel;
import edu.nrao.dss.client.util.Subject;
import edu.nrao.dss.client.widget.PeriodSummaryPanel;
import edu.nrao.dss.client.widget.ProjectTimePanel;
import edu.nrao.dss.client.widget.SessionTimePanel;

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
		periodSummary.setNewPeriods(projectTimePanel.getSelectedProject(), sessionName);
	}
	
	public void updatePCodeOptions() { 
		projectTimePanel.getPCodeOptions(OptionsFilter.getDefaultState("project_codes"));
	}
	
	// given the JSON which has all the time accounting info in it, update the current
	// project and session (note: periods are handled separately)
	public void setTimeAccountingFromJSON(JSONObject json) {
       // make sure the project & session panels get updated;
       // periods get updated by default
       projectTimePanel.populateProjTimeAccounting(json);
       String name = sessionTimePanel.getSelectedSession();
       if (name != null & name != "") {
           sessionTimePanel.populateSessTimeAccounting(json, name);
       }        
	}

}
