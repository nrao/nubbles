// Copyright (C) 2011 Associated Universities, Inc. Washington DC, USA.
// 
// This program is free software; you can redistribute it and/or modify
// it under the terms of the GNU General Public License as published by
// the Free Software Foundation; either version 2 of the License, or
// (at your option) any later version.
// 
// This program is distributed in the hope that it will be useful, but
// WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
// General Public License for more details.
// 
// You should have received a copy of the GNU General Public License
// along with this program; if not, write to the Free Software
// Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.
// 
// Correspondence concerning GBT software should be addressed as follows:
//       GBT Operations
//       National Radio Astronomy Observatory
//       P. O. Box 2
//       Green Bank, WV 24944-0002 USA

package edu.nrao.dss.client;



import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import com.extjs.gxt.ui.client.Style.Orientation;
import com.extjs.gxt.ui.client.Style.LayoutRegion;
import com.extjs.gxt.ui.client.Style.Scroll;
import com.extjs.gxt.ui.client.data.BaseModelData;
import com.extjs.gxt.ui.client.data.BasePagingLoadResult;
import com.extjs.gxt.ui.client.data.ListLoader;
import com.extjs.gxt.ui.client.data.LoadEvent;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.util.Margins;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.button.ToolButton;
import com.extjs.gxt.ui.client.widget.form.FormPanel;
import com.extjs.gxt.ui.client.widget.form.LabelField;
import com.extjs.gxt.ui.client.widget.form.Time;
import com.extjs.gxt.ui.client.widget.layout.BorderLayout;
import com.extjs.gxt.ui.client.widget.layout.BorderLayoutData;
import com.extjs.gxt.ui.client.widget.layout.RowData;
import com.extjs.gxt.ui.client.widget.layout.RowLayout;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONObject;

import edu.nrao.dss.client.data.PeriodEventAdapter;
import edu.nrao.dss.client.util.DynamicHttpProxy;
import edu.nrao.dss.client.util.JSONCallbackAdapter;
import edu.nrao.dss.client.util.JSONRequest;
import edu.nrao.dss.client.util.JSONRequestCache;
import edu.nrao.dss.client.util.dssgwtcal.Appointment;
import edu.nrao.dss.client.util.dssgwtcal.CalendarSettings;
import edu.nrao.dss.client.util.dssgwtcal.DayView;
import edu.nrao.dss.client.util.dssgwtcal.Event;
import edu.nrao.dss.client.util.dssgwtcal.util.TimeUtils;
import edu.nrao.dss.client.widget.FactorsDlg;
import edu.nrao.dss.client.widget.NomineePanel;
import edu.nrao.dss.client.widget.PeriodSummaryDlg;

// This class is the new version of the Beta Test's Scheduling Page.

public class Schedule extends ContentPanel implements Refresher {
	
	public ScheduleCalendar scheduleExplorer;
	public VacancyControl vacancyControl;
	public CalendarControl calendarControl;
	public ScheduleControl scheduleControl;
	private NomineePanel nomineePanel;
	private Reservations reservations;
	private ContentPanel calendar;

	private DayView dayView;
	
	Date startCalendarDay;
	Integer numCalendarDays = 3;
	String timezone = "UTC";
	String baseUrl = "/scheduler/periods/" + timezone;
	
	Scores scores;
	
	Integer numVacancyMinutes = 2;
	Date startVacancyDate = new Date();
	Time startVacancyTime = new Time();
	public Date startVacancyDateTime = new Date();
	
	private ArrayList<String> sess_handles = new ArrayList<String>();
	
	@SuppressWarnings("deprecation")
	public Schedule() {
			super();
			Date d = new Date();
			startCalendarDay = new Date(d.getYear(), d.getMonth(), d.getDate());
			
			initLayout();
			initListeners();
	}	
	
	public String getTimeZone() {
		return timezone;
	}
	
	// it's important too know whether one of the dates being shown on the calendar is
	// a DST boundary, because that means there's client side Date calculations that can't
	// be trusted - like the graphical calendar for instance
	public boolean hasDSTBoundary() {
		Date start = startCalendarDay;
		Date dt;
		TimeUtils tu = new TimeUtils();
		for (int day=0; day < numCalendarDays; day++) {
			dt = new Date(start.getTime() + day*1000*60*60*24);
			if (tu.isDSTBoundary(dt)) {
				return true;
			}
		}
		return false;
	}
	
	protected void initLayout() {
		
		
		setHeaderVisible(true);
		setLayout(new BorderLayout());
		
		setCollapsible(false);
		setBodyBorder(false);
		setFrame(false);
		setHeaderVisible(false);
		setBodyStyle("backgroundColor: white;");
		setHeight(920);
		//setAutoHeight(true);
		getHeader().addTool(new ToolButton("x-tool-gear"));
		getHeader().addTool(new ToolButton("x-tool-close"));

        // basic layout: controls to the west, calendar in the center		
		BorderLayoutData westData = new BorderLayoutData(LayoutRegion.WEST, 760);
		westData.setMinSize(50);
		westData.setMaxSize(1000);
		westData.setMargins(new Margins(5));
		westData.setSplit(true);
		westData.setCollapsible(true);

		BorderLayoutData centerData = new BorderLayoutData(LayoutRegion.CENTER); ;
		centerData.setMargins(new Margins(5, 0, 5, 0));
		
		// now for the child panels:
		// to the side, control widgets
		// ======================== Controls ===================================
		final ContentPanel controlsContainer = new ContentPanel();
		controlsContainer.setFrame(true);
		controlsContainer.setBorders(true);
		controlsContainer.setHeading("Controls");
		controlsContainer.setScrollMode(Scroll.AUTO);

		calendarControl = new CalendarControl(this);
		calendarControl.setCollapsible(true);
		controlsContainer.add(calendarControl);
		
		scheduleControl = new ScheduleControl(this);
        scheduleControl.setCollapsible(true);
        controlsContainer.add(scheduleControl);
		
        scheduleExplorer = new ScheduleCalendar();
		scheduleExplorer.addButtonsListener(this);
		scheduleExplorer.setDefaultDate(startCalendarDay);
		scheduleExplorer.setCollapsible(true);
		scheduleExplorer.setAutoHeight(true);
		controlsContainer.add(scheduleExplorer);
		
		vacancyControl = new VacancyControl(this);
        vacancyControl.setCollapsible(true);
        vacancyControl.collapse();
        controlsContainer.add(vacancyControl);

        nomineePanel = new NomineePanel(this);
		nomineePanel.setCollapsible(true);
		nomineePanel.collapse();
		controlsContainer.add(nomineePanel);
		
		reservations = new Reservations(startCalendarDay, numCalendarDays);
        reservations.setCollapsible(true);
        reservations.collapse();
        controlsContainer.add(reservations);
        
        // in the middle, the calendar
		calendar = new ContentPanel();
        setCalendarHeader(null);
		calendar.setScrollMode(Scroll.NONE);
		
		// calendar
		dayView = new DayView();
		dayView.setDate(startCalendarDay); //calendar date, not required
		dayView.setDays((int) numCalendarDays); //number of days displayed at a time, not required
		dayView.setWidth("100%");
		dayView.setHeight("96%");
		dayView.setTitle("Schedule Calendar");
		CalendarSettings settings = new CalendarSettings();
		// this fixes offset issue with time labels
		settings.setOffsetHourLabels(false);
		// 15-min. boundaries!
		settings.setIntervalsPerHour(4);
		settings.setEnableDragDrop(true);
		settings.setPixelsPerInterval(12); // shrink the calendar!
		dayView.setSettings(settings);
		// when a period is clicked, a user can insert a different session
		// but we need all those session names
		getSessionOptions();
		dayView.addValueChangeHandler(new ValueChangeHandler<Appointment>(){
	        public void onValueChange(ValueChangeEvent<Appointment> event) {
	        	// seed the PeriodDialog w/ details from the period that just got clicked
	            String periodUrl = "/scheduler/periods/UTC/" + event.getValue().getEventId();
	    	    JSONRequest.get(periodUrl, new JSONCallbackAdapter() {
		            @Override
		            public void onSuccess(JSONObject json) {
		            	// JSON period -> JAVA period
	                 	Period period = Period.parseJSON(json.get("period").isObject());
                        // display info about this period, and give options to change it
	                 	PeriodSummaryDlg dlg = new PeriodSummaryDlg(period, sess_handles, (Schedule) controlsContainer.getParent());
	                 	dlg.show();
		            }
		    });	            
	            
	        }               
	    });
		// Wouldn't it be nice if the user could click on a gap in the calendar and find
		// Nominees, rather then just use Vacancy Control?
		// Story: https://www.pivotaltracker.com/story/show/14364047
		//dayView.addSelectionHandler(handler); 
		calendar.add(dayView);
		
		// add all the components to this parent panel
		add(controlsContainer, westData);
		add(calendar, centerData);

		//updateCalendar();
	}
	
    public void setCalendarHeader(String label) {
		String heading = "Calendar ";
		if (label != null) {
			heading += label;
		}
		heading += " (";
		heading += "<font color=#F2A640>Pending</font>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;";
		heading += "<font color=#D96666>Fixed</font>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;";
		heading += "<font color=#668CD9>Open</font>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;";
		heading += "<font color=#4CB052>Default Windowed</font>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;";
		heading += "<font color=#BFBF4D>Non-Default Windowed</font>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;";
		heading += "<font color=#8C66D9>Elective</font>";
		heading += ")";
  		calendar.setHeading(heading);
		calendar.setScrollMode(Scroll.AUTOX);
		calendar.setStyleAttribute("bgcolor", "black");
    }
	
	@SuppressWarnings("deprecation")
	public void initListeners() {
		nomineePanel.getLoader().addListener(ListLoader.Load, new Listener<LoadEvent>() {

			@Override
			public void handleEvent(LoadEvent be) {
				startVacancyDateTime = startVacancyDate;
				startVacancyDateTime.setHours(startVacancyTime.getHour());
				startVacancyDateTime.setMinutes(startVacancyTime.getMinutes());
				startVacancyDateTime.setSeconds(0);
				String startStr = DateTimeFormat.getFormat("yyyy-MM-dd HH:mm:ss").format(startVacancyDateTime);
				int num_nominees = nomineePanel.getNumNominees();
				nomineePanel.setHeading("Nominee Periods for " + startStr + " " + timezone + ".  " + num_nominees + " nominees found.");
				if (num_nominees == 0){
					MessageBox.alert("Attention", "No nominees returned!", null);
				}
			}
			
		});
	}
	
	public FactorsDlg getFactorsDlg() {
		return scheduleControl.factorsDlg;
	}
	
	public void updateNominees() {
		startVacancyDateTime = startVacancyDate;
		startVacancyDateTime.setHours(startVacancyTime.getHour());
		startVacancyDateTime.setMinutes(startVacancyTime.getMinutes());
		startVacancyDateTime.setSeconds(0);
		String startStr = DateTimeFormat.getFormat("yyyy-MM-dd HH:mm:ss").format(startVacancyDateTime);
		
		HashMap<String, Object> keys = new HashMap<String, Object>();
		keys.put("start", startStr);
		keys.put("duration", numVacancyMinutes);
		keys.put("timeBetween", (Boolean) vacancyControl.nomineeOptions.get(0).getValue()); // ignore timeBetween limit?
		keys.put("minimum", (Boolean) vacancyControl.nomineeOptions.get(1).getValue());     // ignore minimum duration limit?
		keys.put("blackout", (Boolean) vacancyControl.nomineeOptions.get(2).getValue());    // ignore observer blackout times?
		keys.put("backup", (Boolean) vacancyControl.nomineeOptions.get(3).getValue());      // use only backup sessions?
		keys.put("completed", (Boolean) vacancyControl.nomineeOptions.get(4).getValue());   // include completed sessions?
		keys.put("rfi", (Boolean) vacancyControl.nomineeOptions.get(5).getValue());         // ignore RFI exclusion flag?
		nomineePanel.updateKeys(keys);
		nomineePanel.loadData();
		nomineePanel.expand();
	}
	
	public void refresh() {
		updateCalendar();
	}
	
    public void updateCalendar() {
    	// construct the url that gets us our periods for the explorer
		String startStr = DateTimeFormat.getFormat("yyyy-MM-dd").format(startCalendarDay);
		String url = baseUrl + "?startPeriods=" + startStr + "&daysPeriods=" + Integer.toString(numCalendarDays);
		
		// get the period explorer to load these
		RequestBuilder builder = new RequestBuilder(RequestBuilder.GET, url);
		DynamicHttpProxy<BasePagingLoadResult<BaseModelData>> proxy = scheduleExplorer.pe.getProxy();
		proxy.setBuilder(builder);
		scheduleExplorer.setDefaultDate(startCalendarDay);
		scheduleExplorer.pe.loadData();
		
		// now get the calendar to load these
		dayView.setDate(startCalendarDay); //calendar date, not required
		dayView.setDays((int) numCalendarDays);		

		// make the JSON request for the periods so we can make appointments
		// we need the same url in a different format
		HashMap<String, Object> keys = new HashMap<String, Object>();
		keys.put("startPeriods", startStr);
		keys.put("daysPeriods", Integer.toString(numCalendarDays));
	    JSONRequest.get(baseUrl, keys, new JSONCallbackAdapter() {
	            @Override
	            public void onSuccess(JSONObject json) {
	                // update the gwt-cal widget
	                loadAppointments(jsonToPeriods(json));
	            }
	    });
	    reservations.update(DateTimeFormat.getFormat("MM/dd/yyyy").format(startCalendarDay)
	    		          , Integer.toString(numCalendarDays));
	}		
	    
    public List<Period> jsonToPeriods(JSONObject json) {
    	// JSON periods -> JAVA periods
        List<Period> periods = new ArrayList<Period>();
        JSONArray ps = json.get("periods").isArray();
        for (int i = 0; i < ps.size(); ++i) {
        	Period period = Period.parseJSON(ps.get(i).isObject());
        	if (period != null){
        		if (!period.isDeleted()) {
            		periods.add(period);
                }
        	}
        }
		return periods;
    }
    
    // updates the gwt-cal widget w/ given periods
    private void loadAppointments(List<Period> periods) {	
		dayView.suspendLayout();
		dayView.clearAppointments();
		for(Period p : periods) {
			dayView.addAppointments(PeriodEventAdapter.fromPeriod(p).getAppointments());
		}
		
		dayView.resumeLayout();
		
		// clear the header if no scores being displayed
        if (dayView.getScores() == null) {
        	setCalendarHeader(null);
        }
		
		// clear out the scores so that next time the calendar is updated,
		// unless new scores have been provided, the present display of scores
		// is erased
		dayView.clearScores();
    }
   
    
    // gets all the session handles (sess name (proj name)) and holds on to them
    // for use in lists (e.g. PeriodDialog)
    private void getSessionOptions() {
    	JSONRequestCache.get("/scheduler/sessions/options"
				, new HashMap<String, Object>() {{
			    	  put("mode", "session_handles");
			        }}
				, new JSONCallbackAdapter() {
        			   public void onSuccess(JSONObject json) {
    					JSONArray sessions = json.get("session handles").isArray();
    					for (int i = 0; i < sessions.size(); ++i){
    						sess_handles.add(sessions.get(i).toString().replace('"', ' ').trim());
    					}       
   					
        			   }
        		   }
       );

    }   
    
	public void setCalendarScores(float[] scores) {
	    dayView.setScores(scores);
	}
	
	public Date getStartCalendarDay() {
	    return startCalendarDay;	
	}
	
	public int getNumCalendarDays() {
		return numCalendarDays;
	}
	
	public void setTimezone(String tz) {
		dayView.setTimezone(tz);
		this.timezone = tz;
	}

}	
	
