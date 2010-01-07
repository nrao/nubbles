package edu.nrao.dss.client;

import com.extjs.gxt.ui.client.event.BaseEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.form.FormPanel;
import com.extjs.gxt.ui.client.widget.form.SimpleComboBox;
import com.extjs.gxt.ui.client.widget.form.TextField;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.extjs.gxt.ui.client.widget.layout.FlowLayout;

public class RcvrChangePanel extends ContentPanel {

	private SimpleComboBox<String> periods = new SimpleComboBox<String>();
	private TextField<String> finalRcvrs = new TextField<String>();
	private TextField<String> goingUpRcvrs = new TextField<String>();
	private TextField<String> goingDownRcvrs = new TextField<String>();
	
	private String[][] diffSchedule;
	
	public RcvrChangePanel() {
		initLayout();
		initListeners();
	}
	
	private void initLayout() {
		setLayout(new FlowLayout());
		setHeading("Receiver Change Panel");
		
		FormPanel fp = new FormPanel();
		fp.setHeaderVisible(false);
		
		periods.setFieldLabel("Change Date");
		fp.add(periods);
		
		//finalRcvrs.setFieldLabel("Rcvrs available at end of day");
		//fp.add(finalRcvrs);
		
		goingUpRcvrs.setFieldLabel("Rcvrs going up");
		fp.add(goingUpRcvrs);
		
		goingDownRcvrs.setFieldLabel("Rcvrs going down");
		fp.add(goingDownRcvrs);
		
		add(fp);
	}
	
	private void initListeners() {
		periods.addListener(Events.Valid, new Listener<BaseEvent>() {
		  	public void handleEvent(BaseEvent be) {
		  		// go git it
		  		setPeriod(periods.getSimpleValue());
		   	}
		});		
	}
	
	public void loadSchedule(String[][] diffSchedule) {
		this.diffSchedule = diffSchedule;
		
		periods.clearSelections();
		periods.removeAll();
		for (int i = 0; i < diffSchedule.length; i++) {
			periods.add(diffSchedule[i][0]);
		}
	}
	
	private void setPeriod(String day) {
		// set the up and down text boxes according to the day
		for (int i = 0; i < diffSchedule.length; i++) {
			if (day.compareTo(diffSchedule[i][0]) == 0) {
				goingUpRcvrs.setValue(diffSchedule[i][1]);
				goingDownRcvrs.setValue(diffSchedule[i][2]);
				finalRcvrs.setValue(diffSchedule[i][3]);
			}
		}
		
	}
 	
}
