package edu.nrao.dss.client.widget;

import java.util.ArrayList;
import java.util.HashMap;

import com.extjs.gxt.ui.client.event.BaseEvent;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.ComponentEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.MessageBoxEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.event.WindowEvent;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.Dialog;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.extjs.gxt.ui.client.widget.layout.FlowLayout;
import com.google.gwt.core.client.GWT;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.user.client.Window;

import edu.nrao.dss.client.Period;
import edu.nrao.dss.client.Schedule;
import edu.nrao.dss.client.util.JSONCallbackAdapter;
import edu.nrao.dss.client.util.JSONRequest;

public class PeriodSummaryDlg extends Dialog {
	
	private Period period;
	private Dialog removeDialog;
	protected Button removeApproval;
	private Schedule sc_handle;
	
	private void publishPeriod() {
		String url = "/scheduler/periods/publish/" + Integer.toString(period.getId());
		GWT.log("publish period: " + url, null);
		HashMap<String, Object> keys = new HashMap<String, Object>();
		JSONRequest.post(url, keys,
				new JSONCallbackAdapter() {
					public void onSuccess(JSONObject json) {
						//updateCalendar();
						if (sc_handle != null) {
						    sc_handle.updateCalendar();
						}
						// TODO: really we should refresh this dialog, but no time
						// for that refactoring right now.
						close();
					}
				});		
	}
	
	// A Period can be deleted if it's not in the scheduled state, or it's
	// time accounting has been reconciled so that there is no Time Billed.
	private final boolean periodCanBeDeleted(Period period) {
		if (!period.isScheduled()) {
			return true; // it's pending, who cares?
		} else {
			if (period.getBilled() == 0.0) {
				// somebody must have taken care of the time accounting
				return true;
			} else {
				// sorry, go back and fix the time accounting first
				return false;
			}
		}
	}

	// Retrieves the lastest value of the Period from the server, checks if it
	// can be deleted, then asks for confirmation before zapping it.
	private void tryDeletePeriod() {
	    // get this period again - it's time accounting might have changed
        String periodUrl = "/scheduler/periods/UTC/" + Integer.toString(this.period.getId());
	    JSONRequest.get(periodUrl, new JSONCallbackAdapter() {
            @Override
            public void onSuccess(JSONObject json) {
            	// JSON period -> JAVA period
             	//Period period = Period.parseJSON(json.get("period").isObject());
             	period = Period.parseJSON(json.get("period").isObject());
             	
	    		if (!periodCanBeDeleted(period)) {
	    			// can't delete it yet!
    	    		Window.alert("This Scheduled Period must first have it's Time Accounting adjusted so that it's Time Billed is 0.0");
          		} else {
          			// confirm deletion
          		    removeDialog.show();	
    		    }
             	
            }
	    });    
	}
	
	// makes call to server for deletion, then updates calendar
	private final void deletePeriod() {
		String rootURL = "/scheduler/periods/UTC";
		JSONRequest.delete(rootURL + "/" + this.period.getId(), //id.intValue(),
				new JSONCallbackAdapter() {
					public void onSuccess(JSONObject json) {
						close();
						if (sc_handle != null) {
						    sc_handle.updateCalendar();
						}    
					}
				});		
	}
	
	public PeriodSummaryDlg(final Period period, final ArrayList<String> sess_handles, final Schedule sc) {
		
		super();
		setLayout(new FlowLayout());
	
		sc_handle = sc;
		this.period = period;
		
		// Basic Dlg settings
		String txt = "Summary for Period " + period.getHandle();
		setHeading(txt);
		setButtons(Dialog.CANCEL);
		
		// Delete the Period?
		Button delete = new Button();
		delete.setToolTip("Click this button to delete this Period.");
		delete.setText("Delete Period");
	    delete.addListener(Events.OnClick, new Listener<BaseEvent>() {
	    	@SuppressWarnings("deprecation")
			public void handleEvent(BaseEvent be) {
	    		tryDeletePeriod();
	    	}
	    });	
	    add(delete);
	    
		// Publish the Period?
		Button publish = new Button();
		publish.setToolTip("Click this button to publish this Period.");
		publish.setText("Publish Period");
	    publish.addListener(Events.OnClick, new Listener<BaseEvent>() {
	    	@SuppressWarnings("deprecation")
			public void handleEvent(BaseEvent be) {
	    		publishPeriod();
	    	}
	    });	
	    add(publish);	
	    
		// Insert a Period?
		Button change = new Button();
		change.setToolTip("Click this button to insert a period into the schedule, with the correct time accounting.");
		change.setText("Insert Period");
	    change.addListener(Events.OnClick, new Listener<BaseEvent>() {
	    	@SuppressWarnings("deprecation")
			public void handleEvent(BaseEvent be) {
	    		GWT.log("Change Click", null);
	    		ChangeScheduleDlg dlg = new ChangeScheduleDlg(period, sess_handles, sc);
	    		close();
	    	}
	    });	
	    add(change);
	    
	    // shfit period boundary?
	    Button shift = new Button();
		shift.setToolTip("Click this button to shift one of the boundaries of the period (start or end), with the correct time accounting.");
		shift.setText("Shift Period Boundary");
	    shift.addListener(Events.OnClick, new Listener<BaseEvent>() {
	    	@SuppressWarnings("deprecation")
			public void handleEvent(BaseEvent be) {
	    		GWT.log("shift Click", null);
	    		ShiftPeriodBndDlg dlg = new ShiftPeriodBndDlg(period, sc); //period, sess_handles, sc);
	    		close();
	    	}
	    });	
	    add(shift);
	    
		// display summary info
		final PeriodSummaryPanel p = new PeriodSummaryPanel(period);
		p.hidePeriodPicker();
		add(p);
		
		// TODO: size correctly
		//setAutoWidth(true);
		setWidth(700);
		//setHeight(400);
		setAutoHeight(true);
		show();
		
		// listener for close w/ out saving changes confirmation
		final Listener<MessageBoxEvent> cancelListener = new Listener<MessageBoxEvent>() {
            public void handleEvent(MessageBoxEvent ce) {
            	Button b = (Button) ce.getButtonClicked();
            	if (b.getItemId().compareTo("yes") == 0) {
            		// they really want to exit w/ out saving changes
            		close();
            	}
            }
        };
		
		Button cancel = getButtonById(Dialog.CANCEL);
		cancel.setText("Close");
		cancel.setToolTip("Close this Dialog");
		cancel.addListener(Events.OnClick, new Listener<BaseEvent>() {
			public void handleEvent(BaseEvent be) {
				// if they have unsaved changes, double check that they really want to exit
				if (p.hasChanged()) {
					MessageBox.confirm("Period Summary", "You have unsaved changes.  Exit anyways?", cancelListener);
				} else {
					close();
				}
			}
		});	

		// for confirming deletion of period
		removeDialog = new Dialog();
		removeDialog.setHeading("Confirmation");
		removeDialog.addText("Are you sure you want to delete this Period?");
		removeDialog.setButtons(Dialog.YESNO);
		removeDialog.setHideOnButtonClick(true);
		removeApproval = removeDialog.getButtonById(Dialog.YES);
		removeApproval.addSelectionListener(new SelectionListener<ButtonEvent>() {
			@Override
			public void componentSelected(ButtonEvent ce) {
				deletePeriod();
			}
		});	
		removeDialog.hide();		
	}	
	
	public void setPeriod(Period period) {
		this.period = period;
	}

}
