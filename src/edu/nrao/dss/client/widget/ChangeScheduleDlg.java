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

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import com.extjs.gxt.ui.client.widget.Dialog;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.DateField;
import com.extjs.gxt.ui.client.widget.form.FormPanel;
import com.extjs.gxt.ui.client.widget.form.SimpleComboBox;
import com.extjs.gxt.ui.client.widget.form.TextArea;
import com.extjs.gxt.ui.client.widget.form.Time;
import com.extjs.gxt.ui.client.widget.form.TimeField;
import com.extjs.gxt.ui.client.widget.form.ComboBox.TriggerAction;
import com.extjs.gxt.ui.client.widget.layout.FormData;
import com.extjs.gxt.ui.client.event.BaseEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.Listener;
import com.google.gwt.core.client.GWT;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.user.client.Window;

import edu.nrao.dss.client.Period;
import edu.nrao.dss.client.Schedule;
import edu.nrao.dss.client.util.JSONCallbackAdapter;
import edu.nrao.dss.client.util.JSONRequest;
import edu.nrao.dss.client.util.TimeUtils;

// This dialog allows the user to specify a new period to insert into the schedule -
// using the nell server to overwrite any overlapping periods, and taking care of all
// the time accounting.

class ChangeScheduleDlg extends Dialog {

    private FormPanel fp = new FormPanel();
    private DateField changeDate = new DateField();
    private TimeField changeTime = new TimeField();
	private SimpleComboBox<String> hours = new SimpleComboBox<String>();
	private HashMap<String, Integer> durChoices = new HashMap<String, Integer>();
    private SimpleComboBox<String> sessions = new SimpleComboBox<String>();
    private SimpleComboBox<String> reasons = new SimpleComboBox<String>();
	private TextArea desc = new TextArea();
    
	private Period period;
	private Schedule parent;
	private ArrayList<String> sess_handles;
	

	public ChangeScheduleDlg(final Period period, ArrayList<String> sess_handles, final Schedule sc) {
		super();
	
		this.period = period;
		this.parent = sc;
		this.sess_handles = sess_handles;
		
		initLayout();
		initListeners();
		
		show();
		
	}
	
	private void initLayout() {
		
		// Basic Dlg settings
		String heading = "Insert Period";
		setHeading(heading);
		String txt = "Insert a new period around Period " + period.getHandle();
		addText(txt);
		setButtons(Dialog.OKCANCEL);
		
		// now set up the form w/ all it's fields
		fp.setHeaderVisible(false);
		
		// starting with the start date
	    changeDate.setValue(period.getStartDay());
	    changeDate.setFieldLabel("Start Date");
		changeDate.setToolTip("Set the start date for the start of new period");
	    fp.add(changeDate);
	    
	    // start time
	    //final TimeField changeTime = new TimeField();
	    changeTime.setTriggerAction(TriggerAction.ALL);
	    changeTime.setFormat(DateTimeFormat.getFormat("HH:mm"));
	    Time t = new Time(period.getStartHour(), period.getStartMinute(), period.getStartTime());
	    changeTime.setValue(t);//Time(period.getStartTime()));
	    changeTime.setFieldLabel("Start Time");
	    changeTime.setAllowBlank(false);
	    changeTime.setEditable(false);
		changeTime.setToolTip("Set the start time for the start of new period");
	    fp.add(changeTime);
		
		// duration
		for (int m = 15; m < 24*60; m += 15) {
			String key = TimeUtils.min2sex(m);
			durChoices.put(key, m);
			hours.add(key);
		}
		
		hours.setTriggerAction(TriggerAction.ALL); 
		hours.setToolTip("Set duration (Hrs:Mins) of the new period");
		hours.setFieldLabel("Duration");
		hours.setEditable(false);
		hours.setAllowBlank(false);
		hours.setSimpleValue(period.getDurationString());
		fp.add(hours);
		
		// replace with what other session?
		for (String handle : sess_handles) {
			sessions.add(handle);
		}

		sessions.setTriggerAction(TriggerAction.ALL);
		sessions.setToolTip("Choose the session for the new period to be inserted.");
		sessions.setFieldLabel("Session");
		sessions.setEditable(false);
		fp.add(sessions);
		
		// why?
        reasons.setTriggerAction(TriggerAction.ALL);
        reasons.add("other_session_weather");
        reasons.add("other_session_rfi");
        reasons.add("other_session_other");
        reasons.setToolTip("Choose why this period is being inserted.");
        reasons.setFieldLabel("Reason");
        reasons.setEditable(false);
        reasons.setAllowBlank(false);
        fp.add(reasons);
        
		// notes
		desc.setFieldLabel("Description");
		desc.setToolTip("Describe why this change is being made. (max. 512 chars.)");
		fp.add(desc, new FormData(350, 350));
		add(fp);
		
		setWidth(500);
		setHeight(500);
	}
	
	
	
	private void initListeners() {
		
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
                sendScheduleChange();
        		close();
			}	
		});
	
	}

	private void sendScheduleChange() {
		
		// validate the input
		if (fp.isValid() == false) {
			String msg = "You have not entered valid information for changing the Schedule.";
			Window.alert(msg);
			return;
		}
		
		// get the values to send down the wire
		HashMap<String, Object> keys = new HashMap<String, Object>();
		Date changeDateTime = changeDate.getValue();
		changeDateTime.setHours(changeTime.getValue().getHour());
		changeDateTime.setMinutes(changeTime.getValue().getMinutes());
		changeDateTime.setSeconds(0);
		String startStr = DateTimeFormat.getFormat("yyyy-MM-dd HH:mm:ss").format(changeDateTime);	    	
		keys.put("start", startStr);
		keys.put("duration", Double.toString(durChoices.get(hours.getSimpleValue())/60.0)); // hex -> minutes -> hours
		keys.put("session", sessions.getSimpleValue());
		keys.put("reason", reasons.getSimpleValue());
		keys.put("description", desc.getValue());

		JSONRequest.post("/scheduler/schedule/change_schedule", keys,
				new JSONCallbackAdapter() {
					public void onSuccess(JSONObject json) {
						// if the change worked, update the calendar.
						parent.updateCalendar();
					}
				});
	}
		
}
