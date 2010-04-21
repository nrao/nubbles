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


public class ShiftPeriodBndDlg extends Dialog {

	// TODO: need to refactor this into more methods
	//public ShiftPeriodDlg(final Period period, ArrayList<String> sess_handles, final Schedule sc) {
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
		GWT.log("ShiftPeriodDialogBox", null);

		// now set up the form w/ all it's fields
		fp.setHeaderVisible(false);
		
 
	
		// the start date
	    //final DateField changeDate = new DateField();
	    //changeDate.setValue(period.getStartDay());
	    changeDate.setFieldLabel("Boundary Date");
		changeDate.setToolTip("Set the new boundary date");
	    fp.add(changeDate);
	    
	    // start time
	    //final TimeField changeTime = new TimeField();
	    changeTime.setFormat(DateTimeFormat.getFormat("HH:mm"));
	    //Time t = new Time(period.getStartHour(), period.getStartMinute(), period.getStartTime());
	    //changeTime.setValue(t);
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
        //final SimpleComboBox<String> reasons = new SimpleComboBox<String>();
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
		//final TextArea desc = new TextArea();
		desc.setFieldLabel("Description");
		desc.setToolTip("Describe why this change is being made. (max. 512 chars.)");
		fp.add(desc, new FormData(350, 350));
		add(fp);
		
		// done adding fields to form!
		
		// TODO: how to size this right?
		setWidth(500);
		setHeight(500);
		
	}
	
	private void initListeners() {
		
		top.addListener(Events.OnClick, new Listener<BaseEvent>() {
			@Override
			public void handleEvent(BaseEvent be) {
				GWT.log("got top click!", null);
				if (top.getValue()) {
					setValuesToTop();
				} else {
					setValuesToBottom();
				}
			}
			
		});

		bottom.addListener(Events.OnClick, new Listener<BaseEvent>() {
			@Override
			public void handleEvent(BaseEvent be) {
				GWT.log("got bottom click!", null);
				if (!bottom.getValue()) {
					setValuesToTop();
				} else {
					setValuesToBottom();
				}
			}
			
		});
		
//		boundary.addListener(Events.OnClick, new Listener<BaseEvent>() {
//
//			@Override
//			public void handleEvent(BaseEvent be) {
//				GWT.log("boundary on blur", null);
//				// TODO Auto-generated method stub
//				RadioGroup rg = (RadioGroup) be.getSource();
//				if ("top" == rg.getValue().getName()) {
//					GWT.log("set to top!", null);
//					setValuesToTop();
//				} else {
//					setValuesToBottom();
//				}
//				
//			}
//			
//		});
		
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
	            
				JSONRequest.post("/schedule/shift_period_boundaries", keys,
						new JSONCallbackAdapter() {
							public void onSuccess(JSONObject json) {
								// if the change worked, update the calendar.
								GWT.log("shift_period_boundaries onSuccess", null);
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
		//bottom.setValue(false);
		
		setValuesToTop();
		
	}
	
	private void setValuesToTop() {
		changeDate.setValue(period.getStartDay());
		GWT.log("start time: "+period.getStartTime(), null);
		Time t = new Time(period.getStartHour(), period.getStartMinute(), period.getStartTime());
		changeTime.setValue(t);
	}
	
	private void setValuesToBottom() {
	    changeDate.setValue(period.getEnd());
		GWT.log("end time: "+period.getEndTime(), null);
	    Time t = new Time(period.getEndHour(), period.getEndMinute(), period.getEndTime());
	    changeTime.setValue(t);
	   
	}
	
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
}


