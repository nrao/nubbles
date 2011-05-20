package edu.nrao.dss.client;



import java.util.Date;
import java.util.HashMap;

import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.layout.FitData;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;

import edu.nrao.dss.client.widget.explorers.PeriodExplorer;


public class ScheduleCalendar extends ContentPanel { 

	public PeriodExplorer  pe;  
	
	public ScheduleCalendar() {
		super();
		initLayout();
	}	
	
	protected void initLayout() {
		
		setHeading("Period Explorer");
		setBorders(true);

		// put the period explorer inside
		FitLayout fl = new FitLayout();
		setLayout(fl);
	    pe = new PeriodExplorer();
	    add(pe, new FitData(10));
	    
	    // NOTE: the period explorer's loadData function is called when the
	    // calendar is updated, using the calendar control's widgets as input
	}
	
	public void addButtonsListener(final Schedule schedule) {
		pe.addButtonsListener(schedule);
	}
	
	public void addRecord(HashMap<String, Object> fields) {
		pe.addRecordInterface(fields);
	}
	
	public void setDefaultDate(Date date) {
		pe.setDefaultDate(date);
	}
	
	public void loadData() {
		pe.loadData();
	}
}	
	
