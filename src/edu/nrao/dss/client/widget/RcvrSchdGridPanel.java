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

import java.util.Date;

import com.extjs.gxt.ui.client.Style.Orientation;
import com.extjs.gxt.ui.client.Style.Scroll;
import com.extjs.gxt.ui.client.Style.VerticalAlignment;
import com.extjs.gxt.ui.client.event.BaseEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.form.CheckBox;
import com.extjs.gxt.ui.client.widget.form.DateField;
import com.extjs.gxt.ui.client.widget.form.FormPanel;
import com.extjs.gxt.ui.client.widget.form.SimpleComboBox;
import com.extjs.gxt.ui.client.widget.form.ComboBox.TriggerAction;
import com.extjs.gxt.ui.client.widget.layout.RowLayout;
import com.extjs.gxt.ui.client.widget.layout.TableData;
import com.extjs.gxt.ui.client.widget.layout.TableLayout;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.event.Listener;

import edu.nrao.dss.client.ReceiverSchedule;
import edu.nrao.dss.client.data.RcvrScheduleData;

// class is responsible for the panel that specifies what part of the rx schedule
// to request, and uses RcvrScheduleGrid for displaying that schedule

public class RcvrSchdGridPanel extends ContentPanel {

	// displays rx schedule
    private RcvrScheduleGrid grid = new RcvrScheduleGrid();
    
    // widgets for sending request for rx schedule
    private DateField start = new DateField();
    private SimpleComboBox<String> numMonths = new SimpleComboBox<String>();
    private CheckBox showMnt = new CheckBox();
    private Button update = new Button();
    
    private ReceiverSchedule parent;
    
	public RcvrSchdGridPanel() {
		initLayout();
		initListeners();
	}
	
	private void initLayout() {	
	
		setLayout(new RowLayout(Orientation.VERTICAL));
	
		//setScrollMode(Scroll.AUTO);
		setBorders(false);
		setHeaderVisible(true);
		setHeading("Receiver Schedule");
		
		FormPanel fp = newFormPanel();
		fp.setLayout(new TableLayout(2));
		
		// button
		FormPanel leftFp = newFormPanel();
		update.setText("Update");
		leftFp.add(update);		
		TableData leftTd = newTableData("100px");
		leftTd.setVerticalAlign(VerticalAlignment.MIDDLE);
		fp.add(leftFp, leftTd);
		
		// dates & such
		FormPanel rightFp = newFormPanel();
		
		// when to start?
		start.setFieldLabel("Start Date");
		rightFp.add(start);
		
		// for how long?
		numMonths.setTriggerAction(TriggerAction.ALL);
		numMonths.setFieldLabel("# Months");
		for (int i = 1; i < 13; i++) {
			numMonths.add(Integer.toString(i));
		}
	
		rightFp.add(numMonths);
		
		// include maintenance days?
		showMnt.setFieldLabel("Maint. Days");
		rightFp.add(showMnt);
		
		fp.add(rightFp, newTableData("500px"));
		add(fp);
		
	    add(grid);
    
	}
	// helper func for setting up panels
	private FormPanel newFormPanel() {
		FormPanel fp = new FormPanel();
		fp.setHeaderVisible(false);
		fp.setBodyBorder(false);
		return fp;
	}
	
	// helper func for setting up panels
	private TableData newTableData(String px) {
		TableData td = new TableData();
		td.setVerticalAlign(VerticalAlignment.TOP);
		// Q: why must I do this, just to get the two forms to share space?
		td.setColspan(1);
		td.setWidth(px);		
		return td;
	}
	
	private void initListeners() {
	    update.addListener(Events.OnClick, new Listener<BaseEvent>() {
	    	public void handleEvent(BaseEvent be) {
	    		// update the calendar using the controls
	    		getRcvrSchedule();
	    		
	    	}
	    });
	    
	    showMnt.addListener(Events.OnClick, new Listener<BaseEvent>() {
	    	public void handleEvent(BaseEvent be) {
	    		grid.setShowMaintenance(showMnt.getValue());
	    		;
	    		
	    	}
	    });
	}	
	
	public void setupCalendar() {
		
		// set default values for controls
		start.setValue(new Date()); // today
		numMonths.setSimpleValue("1");
		showMnt.setValue(false);
		
		// update the calendar
		getRcvrSchedule();
	}
	
	public void getRcvrSchedule() {
	    // update the calendar using the controls
		int numDays = 30 * Integer.parseInt(numMonths.getSimpleValue());
		parent.getRcvrSchedule(start.getValue(), numDays, showMnt.getValue());
	}
	
	public void loadRcvrScheduleData(RcvrScheduleData data) {
		grid.loadRcvrScheduleData(data);
	}
	
	public void setParent(ReceiverSchedule rs) {
		parent = rs;
	}
}
