package edu.nrao.dss.client;

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
import com.extjs.gxt.ui.client.widget.layout.FormData;
import com.extjs.gxt.ui.client.event.BaseEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.Listener;
import com.google.gwt.core.client.GWT;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.user.client.Window;

import edu.nrao.dss.client.util.TimeUtils;

// TODO: this does more then just change a period - it can replace several periods
// w/ a single period.  But it's also ideal for inserting backups.  so what to call it?

class ChangeScheduleDlg extends Dialog {

	// TODO: need to refactor this into more methods
	public ChangeScheduleDlg(final Period period, ArrayList<String> sess_handles, final Schedule sc) {
		
		super();
		
		// Basic Dlg settings
		String heading = "Change Schedule";
		setHeading(heading);
		String txt = "Change the schedule for and around Period " + period.getHandle();
		addText(txt);
		setButtons(Dialog.OKCANCEL);
		GWT.log("PeriodDialogBox", null);
		
		// now set up the form w/ all it's fields
		final FormPanel fp = new FormPanel();
		fp.setHeaderVisible(false);
		
		// starting with the start date
	    final DateField changeDate = new DateField();
	    changeDate.setValue(period.getStartDay());
	    changeDate.setFieldLabel("Start Date");
		changeDate.setToolTip("Set the start date for the time range to be changed");
	    fp.add(changeDate);
	    
	    // start time
	    final TimeField changeTime = new TimeField();
	    changeTime.setFormat(DateTimeFormat.getFormat("HH:mm"));
	    Time t = new Time(period.getStartHour(), period.getStartMinute(), period.getStartTime());
	    changeTime.setValue(t);//Time(period.getStartTime()));
	    changeTime.setFieldLabel("Start Time");
	    changeTime.setAllowBlank(false);
	    changeTime.setEditable(false);
		changeTime.setToolTip("Set the start time for the time range to be changed");
	    fp.add(changeTime);
		
		// duration
		final SimpleComboBox<String> hours = new SimpleComboBox<String>();
		final HashMap<String, Integer> durChoices = new HashMap<String, Integer>();
		for (int m = 15; m < 24*60; m += 15) {
			String key = TimeUtils.min2sex(m);
			durChoices.put(key, m);
			hours.add(key);
		}
		hours.setToolTip("Set duration (Hrs:Mins) of the time to be changed");
		hours.setFieldLabel("Duration");
		hours.setEditable(false);
		hours.setAllowBlank(false);
		hours.setSimpleValue(period.getDurationString());
		fp.add(hours);
		
		// replace with what other session?
		final SimpleComboBox<String> sessions = new SimpleComboBox<String>();
		for (String handle : sess_handles) {
			sessions.add(handle);
		}
		sessions.setToolTip("Choose the session for the new period to be inserted.");
		sessions.setFieldLabel("Session");
		sessions.setEditable(false);
		fp.add(sessions);
		
		// why?
        final SimpleComboBox<String> reasons = new SimpleComboBox<String>();
        reasons.add("other_session_weather");
        reasons.add("other_session_rfi");
        reasons.add("other_session_other");
        reasons.setToolTip("Choose why this period is being inserted.");
        reasons.setFieldLabel("Reason");
        reasons.setEditable(false);
        reasons.setAllowBlank(false);
        fp.add(reasons);
        
		// notes
		final TextArea desc = new TextArea();
		desc.setFieldLabel("Description");
		desc.setToolTip("Describe why this change is being made. (max. 512 chars.)");
		fp.add(desc, new FormData(350, 350));
		add(fp);
		
		// done adding fields to form!
		
		// TODO: how to size this right?
		setWidth(500);
		setHeight(500);
		
		show();
		
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

				JSONRequest.post("/schedule/change_schedule", keys,
						new JSONCallbackAdapter() {
							public void onSuccess(JSONObject json) {
								// if the change worked, update the calendar.
								GWT.log("schedule_change onSuccess", null);
								sc.updateCalendar();
							}
						});
				close();
			}
		});
	
	}
	
}