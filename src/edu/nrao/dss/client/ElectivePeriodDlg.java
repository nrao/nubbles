
package edu.nrao.dss.client;

import java.util.HashMap;

import com.extjs.gxt.ui.client.event.BaseEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.MessageBoxEvent;
import com.extjs.gxt.ui.client.widget.Dialog;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.layout.FlowLayout;
import com.google.gwt.core.client.GWT;
import com.google.gwt.json.client.JSONObject;

// TODO: refactor this class to share code with PeriodSummaryDlg

public class ElectivePeriodDlg extends Dialog {
	
	private Period period;
	private ElectiveInfoPanel parent;
	
	private Button publish;
	private Button cancel;
	private PeriodSummaryPanel periodPanel;
	private Listener<MessageBoxEvent> cancelListener;
	
	public ElectivePeriodDlg(Period period, ElectiveInfoPanel parent) {
		this.period = period;
		this.parent = parent;
		initLayout();
		initListeners();
	}
	
	private void initLayout() {
		
		//super();
		setLayout(new FlowLayout());
	
		// Basic Dlg settings
		String txt = "Summary for Period " + period.getHandle();
		setHeading(txt);
		setButtons(Dialog.CANCEL);
		
	   
		// Publish the Period?
		publish = new Button();
		publish.setToolTip("Click this button to publish this Period.");
		publish.setText("Publish Period");
	    add(publish);	
	    
	    
		// display summary info
		periodPanel = new PeriodSummaryPanel(period);
		add(periodPanel);
		
		// TODO: size correctly
		//setAutoWidth(true);
		setWidth(700);
		//setHeight(400);
		setAutoHeight(true);
		show();
		
		// listener for close w/ out saving changes confirmation
		cancelListener = new Listener<MessageBoxEvent>() {
	        public void handleEvent(MessageBoxEvent ce) {
	        	Button b = (Button) ce.getButtonClicked();
	        	if (b.getItemId().compareTo("yes") == 0) {
	        		// they really want to exit w/ out saving changes
	        		close();
	        		// refresh window
	        		parent.getElective();
	        	}
	        }
	    };
		
		cancel = getButtonById(Dialog.CANCEL);
		cancel.setText("Close");
		cancel.setToolTip("Close this Dialog");

	}
	
	private void initListeners() {
	    publish.addListener(Events.OnClick, new Listener<BaseEvent>() {
	    	@SuppressWarnings("deprecation")
			public void handleEvent(BaseEvent be) {
	    		publishPeriod();
	    	}
	    });	
		cancel.addListener(Events.OnClick, new Listener<BaseEvent>() {
			public void handleEvent(BaseEvent be) {
				// if they have unsaved changes, double check that they really want to exit
				if (periodPanel.hasChanged()) {
					MessageBox.confirm("Period Summary", "You have unsaved changes.  Exit anyways?", cancelListener);
				} else {
					close();
					// refresh window
					parent.getElective();
				}
			}
		});		    
	}
	
	private void publishPeriod() {
		String url = "/periods/publish/" + Integer.toString(period.getId());
		GWT.log("publish period: " + url, null);
		HashMap<String, Object> keys = new HashMap<String, Object>();
		JSONRequest.post(url, keys,
			new JSONCallbackAdapter() {
				public void onSuccess(JSONObject json) {
				    GWT.log("Elective.publishPeriods.onSuccess");
						// TODO: really we should refresh this dialog, but no time
					// for that refactoring right now.
					close();
					// refresh window
					parent.getElective();
				}
			});		
	}

}
