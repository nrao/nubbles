package edu.nrao.dss.client;

import java.util.ArrayList;

import com.extjs.gxt.ui.client.event.BaseEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.Dialog;
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
		setButtons(Dialog.OK);

		// change the schedule?
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
	    
		// display summary info
		PeriodSummaryPanel p = new PeriodSummaryPanel(period);
		add(p);
		
		// TODO: size correctly
		//setAutoWidth(true);
		setWidth(700);
		//setHeight(400);
		setAutoHeight(true);
		show();
		
		Button ok = getButtonById(Dialog.OK);
		ok.addListener(Events.OnClick, new Listener<BaseEvent>() {
			public void handleEvent(BaseEvent be) {
				close();
			}
		});		
	}	

}
