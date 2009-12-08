package edu.nrao.dss.client;

import java.util.ArrayList;

import com.extjs.gxt.ui.client.event.BaseEvent;
import com.extjs.gxt.ui.client.event.ComponentEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.MessageBoxEvent;
import com.extjs.gxt.ui.client.event.WindowEvent;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.Dialog;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.extjs.gxt.ui.client.widget.layout.FlowLayout;
import com.google.gwt.core.client.GWT;

public class PeriodSummaryDlg extends Dialog {
	
	public PeriodSummaryDlg(final Period period, final ArrayList<String> sess_handles, final Schedule sc) {
		
		super();
		setLayout(new FlowLayout());
		
		// Basic Dlg settings
		String txt = "Summary for Period " + period.getHandle();
		setHeading(txt);
		setButtons(Dialog.CANCEL);
		

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
	}	

}
