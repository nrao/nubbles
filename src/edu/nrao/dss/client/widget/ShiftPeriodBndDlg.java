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
import java.util.HashMap;

import com.extjs.gxt.ui.client.event.BaseEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.widget.Dialog;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.DateField;
import com.extjs.gxt.ui.client.widget.form.FormPanel;
import com.extjs.gxt.ui.client.widget.form.Radio;
import com.extjs.gxt.ui.client.widget.form.RadioGroup;
import com.extjs.gxt.ui.client.widget.form.SimpleComboBox;
import com.extjs.gxt.ui.client.widget.form.TextArea;
import com.extjs.gxt.ui.client.widget.form.Time;
import com.extjs.gxt.ui.client.widget.form.TimeField;
import com.extjs.gxt.ui.client.widget.form.ComboBox.TriggerAction;
import com.extjs.gxt.ui.client.widget.layout.FormData;
import com.google.gwt.core.client.GWT;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.json.client.JSONObject;

import edu.nrao.dss.client.Period;
import edu.nrao.dss.client.Schedule;
import edu.nrao.dss.client.util.JSONCallbackAdapter;
import edu.nrao.dss.client.util.JSONRequest;

// This Dialog provides a way for the user to shift the boundary between two
// existing periods - and having the server take care of all the time accounting
// side affects.  Note: does not work when a boundary is along a schedule gap.

public class ShiftPeriodBndDlg extends Dialog {
	
	private Period period;	
	private Schedule schedule;
	
	final FormPanel fp = new FormPanel();
	private Radio bottom = new Radio();  
	private Radio top = new Radio();  
	private RadioGroup boundary = new RadioGroup();
    private DateField changeDate = new DateField();
    private TimeField changeTime = new TimeField();
    private SimpleComboBox<String> reasons = new SimpleComboBox<String>();
    private TextArea desc = new TextArea();

	public ShiftPeriodBndDlg(final Period period, final Schedule sc) {
		super();
		
		this.period = period;
		schedule = sc;
		
		initLayout();
		initListeners();
		setDefaultValues();
		
		show();
	}	
	
	private void initLayout() {
		
		// Basic Dlg settings
		String heading = "Shift Period Boundary";
		setHeading(heading);
		String txt = "Shift one of the boundaries for Period " + period.getHandle();
		addText(txt);
		setButtons(Dialog.OKCANCEL);

		// now set up the form w/ all it's fields
		fp.setHeaderVisible(false);
	
		// the start date
		changeDate.setFieldLabel("Boundary Date");
		changeDate.setToolTip("Set the new boundary date");
	    fp.add(changeDate);
	    
	    // start time
	    changeTime.setTriggerAction(TriggerAction.ALL);
	    changeTime.setFormat(DateTimeFormat.getFormat("HH:mm"));
	    changeTime.setFieldLabel("Boundary Time");
	    changeTime.setAllowBlank(false);
	    changeTime.setEditable(false);
		changeTime.setToolTip("Set the new boundary time");
	    fp.add(changeTime);		

		// which boundary?  Top or bottom?
	    top.setName("top");  
		top.setBoxLabel("Period Start");  
		bottom.setName("bottom");  
		bottom.setBoxLabel("Period Bottom");  
		boundary.setFieldLabel("Which Boundary?");  
		boundary.add(top);  
		boundary.add(bottom);  
		fp.add(boundary);
		
		// why?
        reasons.add("other_session_weather");
        reasons.add("other_session_rfi");
        reasons.add("other_session_other");
        reasons.setToolTip("Choose why this boundary is shifting.");
        reasons.setFieldLabel("Reason");
        reasons.setEditable(false);
        reasons.setAllowBlank(false);
        reasons.setTriggerAction(TriggerAction.ALL);
        fp.add(reasons);
        
		// notes
		desc.setFieldLabel("Description");
		desc.setToolTip("Describe why this change is being made. (max. 512 chars.)");
		fp.add(desc, new FormData(350, 350));
		add(fp);
		
		// Note: better way to size this?
		setWidth(500);
		setHeight(500);
		
	}
	
	private void initListeners() {
		
		// when the top is chosen, set the changeDate to the beginning of the period
		top.addListener(Events.OnClick, new Listener<BaseEvent>() {
			@Override
			public void handleEvent(BaseEvent be) {
				if (top.getValue()) {
					setValuesToTop();
				} else {
					setValuesToBottom();
				}
			}
			
		});

		// when the bottom is chosen, set the changeDate to the end of the period
		bottom.addListener(Events.OnClick, new Listener<BaseEvent>() {
			@Override
			public void handleEvent(BaseEvent be) {
				if (!bottom.getValue()) {
					setValuesToTop();
				} else {
					setValuesToBottom();
				}
			}
			
		});
		
		// Cancel Button: somebody decided to back out
		Button cancel = getButtonById(Dialog.CANCEL);
		cancel.addListener(Events.OnClick, new Listener<BaseEvent>() {
			public void handleEvent(BaseEvent be) {
				close();
			}
			
		});
		
		// OK button: Make the change to the schedule
		Button ok = getButtonById(Dialog.OK);
		ok.addListener(Events.OnClick,new Listener<BaseEvent>() {
			public void handleEvent(BaseEvent be) {
				
				// validate the input
				if (fp.isValid() == false) {
					String msg = "You have not entered valid information for changing the Schedule.";
					//Window.alert(msg);
					return;
				}
				
				// get the values to send down the wire
	    		HashMap<String, Object> keys = new HashMap<String, Object>();
	    		Date changeDateTime = changeDate.getValue();
	    		changeDateTime.setHours(changeTime.getValue().getHour());
	    		changeDateTime.setMinutes(changeTime.getValue().getMinutes());
	    		changeDateTime.setSeconds(0);
	    		String startStr = DateTimeFormat.getFormat("yyyy-MM-dd HH:mm:ss").format(changeDateTime);
	    		keys.put("time", startStr);
	    		
	    		int boundaryStart = 0; // bottom
	    		if (top.getValue()) {
	    			boundaryStart = 1; // top
	    		}
	            keys.put("start_boundary", boundaryStart);
	            keys.put("period_id", period.getId());
	            keys.put("description", desc.getValue());
	            keys.put("reason", reasons.getSimpleValue());
	            
				JSONRequest.post("/scheduler/schedule/shift_period_boundaries", keys,
						new JSONCallbackAdapter() {
							public void onSuccess(JSONObject json) {
								// if the change worked, update the calendar.
								schedule.updateCalendar();
							}
						});
				close();
			}
		});		
	}
	
	private void setDefaultValues() {
	
		// default to start of period
		top.setValue(true);
		setValuesToTop();
		
	}

	// init widgets to beginning of period
	private void setValuesToTop() {
		changeDate.setValue(period.getStartDay());
		Time t = new Time(period.getStartHour(), period.getStartMinute(), period.getStartTime());
		changeTime.setValue(t);
	}
	
	// init widgets to end of period
	private void setValuesToBottom() {
	    changeDate.setValue(period.getEnd());
	    Time t = new Time(period.getEndHour(), period.getEndMinute(), period.getEndTime());
	    changeTime.setValue(t);
	   
	}
	
}


