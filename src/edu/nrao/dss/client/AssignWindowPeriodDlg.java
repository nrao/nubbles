package edu.nrao.dss.client;

import java.util.HashMap;

import com.extjs.gxt.ui.client.event.BaseEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.widget.Dialog;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.CheckBox;
import com.extjs.gxt.ui.client.widget.form.FormPanel;
import com.extjs.gxt.ui.client.widget.form.SimpleComboBox;
import com.extjs.gxt.ui.client.widget.form.Time;
import com.extjs.gxt.ui.client.widget.form.ComboBox.TriggerAction;
import com.extjs.gxt.ui.client.widget.layout.FormData;
import com.google.gwt.core.client.GWT;
import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONObject;


// This class is responsible for allowing the user to pick an existing period from a given session, and
// assign it to a given window.

public class AssignWindowPeriodDlg extends Dialog {
	
	final private SimpleComboBox<String> periods = new SimpleComboBox<String>();
	final private HashMap<String, Integer> periodsMap = new HashMap<String, Integer>();
	final private CheckBox defaultPeriod = new CheckBox();
	private int windowId;
	
	public AssignWindowPeriodDlg() {
		super();
		
		// Basic Dlg settings
		setHeading("Assign a Period to this Window");
		addText("Choose the pre-existing period to assign to this Window.");
		setButtons(Dialog.OKCANCEL);
		
		// now set up the form w/ all it's fields
		final FormPanel fp = new FormPanel();
		fp.setHeaderVisible(false);
		
		// periods
		periods.setForceSelection(true);
		periods.setToolTip("Select a period to assign to this Window");
		periods.setFieldLabel("Periods");
		//sessions.setSimpleValue("Maintenance (Maintenance)"); // TODO fails
		periods.setEditable(false);
		periods.setTriggerAction(TriggerAction.ALL);
		
		fp.add(periods, new FormData(300, 300));

		// are we assigning the default or chosen period?
		defaultPeriod.setValue(true);
		defaultPeriod.setFieldLabel("Default Period?");
		defaultPeriod.setToolTip("Is the period assigned for this window's default or chosen period?");
		
		fp.add(defaultPeriod);
		
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
                hide();
                assignPeriod();
			}
		});		
	}
	
	public void show(String sessionHandle, int windowId) {
		
		this.windowId = windowId;
		
		// parse this handle to get the session name and project code
		String names[] = sessionHandle.split(" ");
		String session = names[0];
		String pcode = names[1];
		pcode = pcode.substring(1, pcode.length() - 1);
		
		
		// make the call to get the periods for this session
		HashMap<String, Object> keys = new HashMap<String, Object>();
		keys.put("mode", "periods");
		keys.put("session_name", session); 
		keys.put("pcode", pcode);
		JSONRequest.get("/sessions/options"
			      , keys 
			      , new JSONCallbackAdapter() {
			public void onSuccess(JSONObject json) {
				
				// get ready to repopulate
				periodsMap.clear();
				periods.removeAll();
				
				// save off the period ids, and populate the drop down with periods.
				JSONArray ps = json.get("periods").isArray();
				JSONArray ids = json.get("period ids").isArray();
				for (int i = 0; i< ids.size(); i += 1) {
					String key = ps.get(i).toString().replace('"', ' ').trim();
					//periodsMap.put(key, (int)(ids.get(i).isNumber().doubleValue()));
					String sId = ids.get(i).isString().stringValue(); // TODO: why are these strings???
					periodsMap.put(key, Integer.parseInt(sId));
					periods.add(key);
				}
			}
    	});		
		super.show();
	}
	
	public void assignPeriod() {
		
		// what period was chosen?
		String periodName = periods.getSimpleValue();
		if (periodName == null) {
			return; // error
		} 
		if (periodName.compareTo("") == 0) {
			return; // error
		}
		int periodId = periodsMap.get(periodName);
		
		String url = "/window/" + Integer.toString(windowId) + "/assign_period/" + Integer.toString(periodId);
		HashMap<String, Object> keys = new HashMap<String, Object>();
		keys.put("default", defaultPeriod.getValue());
		JSONRequest.post(url
			      , keys
			      , new JSONCallbackAdapter() {
			public void onSuccess(JSONObject json) {
                // done?
			}
		});				
	}
}
	
