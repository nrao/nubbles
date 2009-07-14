package edu.nrao.dss.client;


import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import com.extjs.gxt.ui.client.Style.HorizontalAlignment;
import com.extjs.gxt.ui.client.data.BaseModelData;
import com.extjs.gxt.ui.client.data.BasePagingLoadResult;
import com.extjs.gxt.ui.client.event.BaseEvent;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.SelectionListener;

import com.extjs.gxt.ui.client.widget.Component;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.LabelField;
import com.extjs.gxt.ui.client.widget.layout.FitData;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.extjs.gxt.ui.client.widget.layout.TableData;
import com.extjs.gxt.ui.client.widget.layout.TableLayout;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Widget;


public class ScheduleCalendar extends ContentPanel { 

	private Date startCalendarDay = new Date();
	private int numCalendarDays = 1;
	public PeriodExplorer  pe;  
    private List<Period> periods;

	
	public ScheduleCalendar(Date start, int days) {
			super();
			// TBF: use this date range to filter the explorer
			startCalendarDay = start;
			numCalendarDays = days;
			initLayout(start, days);
	}	
	
	@SuppressWarnings("unchecked") 
	protected void initLayout(Date start, int days) {
		
		setHeading("West: Calendar");
		setBorders(true);

		// put the period explorer inside
		FitLayout fl = new FitLayout();
		setLayout(fl);
	    pe = new PeriodExplorer();
	    add(pe, new FitData(10));
   
		
	}
	
//	public DynamicHttpProxy<BasePagingLoadResult<BaseModelData>>getProxy() {
//		return pe.getProxy();
//	}
//	
}	
	