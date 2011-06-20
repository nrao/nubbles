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



import java.util.Date;
import java.util.HashMap;

import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.layout.FitData;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;

import edu.nrao.dss.client.widget.explorers.PeriodExplorer;


public class ScheduleCalendar extends ContentPanel { 

	public PeriodExplorer  pe;  
	
	public ScheduleCalendar() {
		super();
		initLayout();
	}	
	
	protected void initLayout() {
		
		setHeading("Period Explorer");
		setBorders(true);

		// put the period explorer inside
		FitLayout fl = new FitLayout();
		setLayout(fl);
	    pe = new PeriodExplorer();
	    add(pe, new FitData(10));
	    
	    // NOTE: the period explorer's loadData function is called when the
	    // calendar is updated, using the calendar control's widgets as input
	}
	
	public void addButtonsListener(final Schedule schedule) {
		pe.addButtonsListener(schedule);
	}
	
	public void addRecord(HashMap<String, Object> fields) {
		pe.addRecordInterface(fields);
	}
	
	public void setDefaultDate(Date date) {
		pe.setDefaultDate(date);
	}
	
	public void loadData() {
		pe.loadData();
	}
}	
	
