package edu.nrao.dss.client;

import java.util.Date;
import java.util.HashMap;

import com.extjs.gxt.ui.client.event.BaseEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.Listener;
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

public class FactorsDlg extends Dialog implements FactorsControl {

	private Schedule schedule;
	private String label;
	private Integer sessionId;
	public Date start = new Date();
	private Integer duration = 4; // hours
	private FactorsDisplay display;
	private FactorsAccess access;

	@SuppressWarnings("serial")
	public FactorsDlg(Schedule sched) {
		super();
		schedule = sched;
		
		// Basic Dlg settings
		setHeading("Factor Session");
		addText("Display all the score factors for a session over a range");
		setButtons(Dialog.OKCANCEL);
		
		// now set up the form w/ all it's fields
		final FormPanel fp = new FormPanel();
		fp.setHeaderVisible(false);
		
		// session
		final SimpleComboBox<String> sessions = new SimpleComboBox<String>();
		final HashMap<String, Integer> sessionsMap = new HashMap<String, Integer>();
		sessions.setForceSelection(true);
		JSONRequest.get("/sessions/options"
			      , new HashMap<String, Object>() {{
			    	  put("mode", "session_handles");
			        }}
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
		sessions.setToolTip("Select a session to factor.");
		sessions.setFieldLabel("Sessions");
		sessions.setSimpleValue("Maintenance (Maintenance)"); // TODO fails
		sessions.setEditable(false);
		sessions.setTriggerAction(TriggerAction.ALL);
		fp.add(sessions);
		
		// start date
		final DateField startDateField = new DateField();
	    startDateField.setValue(new Date());
	    startDateField.setFieldLabel("Start Date");
		startDateField.setToolTip("Set the start day for the vacancy to be filled");
	    fp.add(startDateField);
	    
	    // start time
	    final TimeField timeField = new TimeField();
	    timeField.setFormat(DateTimeFormat.getFormat("HH:mm"));
	    timeField.setValue(new Time(0, 0)); // TODO fails
	    timeField.setFieldLabel("Start Time");
		timeField.setToolTip("Set the start time for the vacancy to be filled");
	    fp.add(timeField);
		
		// start duration
	    final NumberField hours = new NumberField();
	    hours.setPropertyEditorType(Integer.class);
	    hours.setValue(4);
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
                
                access.request(display, sessionId, label, start, getDuration(), schedule.timezone);
			}
		});
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

}
