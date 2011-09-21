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
import com.extjs.gxt.ui.client.widget.Component;
import com.extjs.gxt.ui.client.widget.Dialog;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.DateField;
import com.extjs.gxt.ui.client.widget.form.FormPanel;
import com.extjs.gxt.ui.client.widget.form.NumberField;
import com.extjs.gxt.ui.client.widget.form.SimpleComboBox;
import com.extjs.gxt.ui.client.widget.form.Time;
import com.extjs.gxt.ui.client.widget.form.TimeField;
import com.extjs.gxt.ui.client.widget.form.ComboBox.TriggerAction;
import com.google.gwt.core.client.GWT;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONObject;

import edu.nrao.dss.client.FactorsAccess;
import edu.nrao.dss.client.FactorsControl;
import edu.nrao.dss.client.FactorsDisplay;
import edu.nrao.dss.client.Schedule;
import edu.nrao.dss.client.util.JSONCallbackAdapter;
import edu.nrao.dss.client.util.JSONRequestCache;

public class FactorsDlg extends Dialog implements FactorsControl {

	private Schedule schedule;
	private String label;
	private Integer sessionId;
	public Date start = new Date();
	private Integer duration = 4; // hours
	private FactorsDisplay display;
	private FactorsAccess access;
	
	// FormPanel fields for the Factors Dlg
	private final FormPanel fp = new FormPanel();
	private final SimpleComboBox<String> sessions = new SimpleComboBox<String>();
	private final DateField startDateField = new DateField();
    private final TimeField timeField = new TimeField();
    private final NumberField hours = new NumberField();

	@SuppressWarnings("serial")
	public FactorsDlg(Schedule sched) {
		super();
		schedule = sched;
		
		// Basic Dlg settings
		setHeading("Factor Session");
		addText("Display all the score factors for a session over a range");
		setButtons(Dialog.OKCANCEL);
		
		// now set up the form w/ all it's fields
		fp.setHeaderVisible(false);
		
		// session
		final HashMap<String, Integer> sessionsMap = 
			getOptions(new HashMap<String, Object> () {{
			    put("enabled", "true");
			    put("notcomplete", "true");
			    put("semesters", "[11A, 11B]");
			
		}});
		
		sessions.setToolTip("Select a session to factor.");
		sessions.setFieldLabel("Sessions");
		sessions.setEditable(false);
		sessions.setTriggerAction(TriggerAction.ALL);
		fp.add(sessions);
		
		// start date
		long iTimeStamp = schedule.getStartCalendarDay().getTime() + (3600000 * 24);
		Date tomorrow = new Date();
		tomorrow.setTime(iTimeStamp);
		startDateField.setValue(tomorrow);
	    startDateField.setFieldLabel("Start Date");
		startDateField.setToolTip("Set the start day for the vacancy to be filled");
	    fp.add(startDateField);
	    
	    // start time
	    timeField.setTriggerAction(TriggerAction.ALL);
	    DateTimeFormat fmt = DateTimeFormat.getFormat("HH:mm");
	    timeField.setFormat(fmt);
	    timeField.setDateValue(fmt.parse("13:00"));
	    timeField.setFieldLabel("Start Time");
		timeField.setToolTip("Set the start time for the vacancy to be filled");
	    fp.add(timeField);
		
		// start duration
	    hours.setPropertyEditorType(Integer.class);
	    hours.setValue(24);
		hours.setToolTip("Set the time range.");
		hours.setFieldLabel("Range (Hrs)");
		fp.add(hours);
		
		add(fp);
		
		setWidth(500);
		setHeight(230);
		
		// Cancel Button: somebody decided to back out
		Button cancel = getButtonById(Dialog.CANCEL);
		cancel.addListener(Events.OnClick, new Listener<BaseEvent>() {
			public void handleEvent(BaseEvent be) {
				hide();
			}
		});
		
		Button ok = getButtonById(Dialog.OK);
		ok.addListener(Events.OnClick,new Listener<BaseEvent>() {
			public void handleEvent(BaseEvent be) {
				// session
	    		label = sessions.getSimpleValue();
	    		sessionId = sessionsMap.get(label);
				// start
				start = startDateField.getValue();
				Time startTime = timeField.getValue();
				start.setHours(startTime.getHour());
				start.setMinutes(startTime.getMinutes());
				start.setSeconds(0);
				GWT.log("GMT " + start.toGMTString(), null);
				GWT.log("Locale " + start.toLocaleString(), null);
				// duration
	    		Number n = hours.getValue();
	    		double value = n.doubleValue();
	    		Double db = Double.valueOf(value);
	    		int i = db.intValue();
	    		duration = Integer.valueOf(i);
	    		
                hide();
                
                access.request(display, sessionId, label, start, getDuration(), schedule.getTimeZone());
			}
		});
	}
	
	public HashMap<String, Integer> getOptions() {
		return getOptions(new HashMap<String, Object>());
	}
	
	public HashMap<String, Integer> getOptions(HashMap<String, Object> state) {
		final HashMap<String, Integer> sessionsMap = new HashMap<String, Integer>();
		sessions.setForceSelection(true);
		sessions.removeAll();
		state.put("mode", "session_handles");
		JSONRequestCache.get("/scheduler/sessions/options"
				, state
				, new JSONCallbackAdapter() {
			public void onSuccess(JSONObject json) {
				JSONArray results = json.get("session handles").isArray();
				JSONArray ids = json.get("ids").isArray();
				for (int i = 0; i< ids.size(); i += 1) {
					String key = results.get(i).toString().replace('"', ' ').trim();
					sessionsMap.put(key, (int)(ids.get(i).isNumber().doubleValue()));
					sessions.add(key);
				}
			}
    	});
		return sessionsMap;
	}
	
	private Integer getDuration() {
		return 60*duration;
	}

	@Override
	public void setDisplay(FactorsDisplay d) {
		display = d;
	}
	
	@Override
	public void setAccess(FactorsAccess a) {
		access = a;
	}
	
	public void initValues(HashMap<String, Object> values){
		String value = values.get("handle").toString();
		int cutOff = value.lastIndexOf(" ");
		value = value.substring(0, cutOff);
		sessions.setSimpleValue(value);
		DateTimeFormat fmt = DateTimeFormat.getFormat("yyyy-MM-dd");
		startDateField.setValue(fmt.parse(values.get("date").toString()));
		fmt = DateTimeFormat.getFormat("HH:mm");
		timeField.setDateValue(fmt.parse(values.get("time").toString()));
		hours.setValue(new Float(values.get("duration").toString()));
	}
	
	public void clearFormFields() {
		if (sessions.isDirty()) {
			sessions.clear();
		}
		if (startDateField.isDirty()) {
			long iTimeStamp = schedule.getStartCalendarDay().getTime() + (3600000 * 24);
			Date tomorrow = new Date();
			tomorrow.setTime(iTimeStamp);
			startDateField.setValue(tomorrow);
		}
		if (timeField.isDirty()) {
			DateTimeFormat fmt = DateTimeFormat.getFormat("HH:mm");
		    timeField.setDateValue(fmt.parse("13:00"));
		    
		}
		if (hours.isDirty()) {
			hours.setValue(24);
		}
	}

}
