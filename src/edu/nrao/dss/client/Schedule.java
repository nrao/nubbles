package edu.nrao.dss.client;



import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import com.bradrydzewski.gwt.calendar.client.Appointment;
import com.bradrydzewski.gwt.calendar.client.AppointmentInterface;
import com.bradrydzewski.gwt.calendar.client.CalendarSettings;
import com.bradrydzewski.gwt.calendar.client.DayView;
import com.extjs.gxt.ui.client.Style.LayoutRegion;
import com.extjs.gxt.ui.client.Style.Scroll;
import com.extjs.gxt.ui.client.data.BaseModelData;
import com.extjs.gxt.ui.client.data.BasePagingLoadResult;
import com.extjs.gxt.ui.client.event.BaseEvent;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.util.Margins;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.button.ToolButton;
import com.extjs.gxt.ui.client.widget.form.DateField;
import com.extjs.gxt.ui.client.widget.form.FormPanel;
import com.extjs.gxt.ui.client.widget.form.CheckBox;
import com.extjs.gxt.ui.client.widget.form.CheckBoxGroup;
import com.extjs.gxt.ui.client.widget.form.LabelField;
import com.extjs.gxt.ui.client.widget.form.SimpleComboBox;
import com.extjs.gxt.ui.client.widget.form.Time;
import com.extjs.gxt.ui.client.widget.form.TimeField;
import com.extjs.gxt.ui.client.widget.layout.BorderLayout;
import com.extjs.gxt.ui.client.widget.layout.BorderLayoutData;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.extjs.gxt.ui.client.widget.layout.HBoxLayout;
import com.extjs.gxt.ui.client.widget.layout.HBoxLayout.HBoxLayoutAlign;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.Window;

import edu.nrao.dss.client.util.TimeUtils;

// This class is the new version of the Beta Test's Scheduling Page.

public class Schedule extends ContentPanel {
	
	public ScheduleCalendar west;
	private NomineePanel east;
	private ContentPanel center;

	private DayView dayView;
	
	private Date startCalendarDay = new Date();
	private Integer numCalendarDays = 3;
	private String timezone = "UTC";
	private String baseUrl = "/periods/" + timezone;

	
	private Integer numVacancyMinutes = 2;
	private Date startVacancyDate = new Date();
	private Time startVacancyTime = new Time();
	public Date startVacancyDateTime = new Date();
	private CheckBoxGroup nomineeOptions = new CheckBoxGroup();
	
	public Schedule() {
			super();
			initLayout();
	}	
	
	protected void initLayout() {
		setHeaderVisible(true);
		setLayout(new BorderLayout());
		
		// bells & whistles for this content panel
		//setHeading("Schedule Stuff"); 
		setCollapsible(false);
		setBodyBorder(false);
		setFrame(false);
		setHeaderVisible(false);
		setBodyStyle("backgroundColor: white;");
		getHeader().addTool(new ToolButton("x-tool-gear"));
		getHeader().addTool(new ToolButton("x-tool-close"));

		// now for the child panels:
		// At the top, control widgets
		LayoutContainer north = new LayoutContainer();
		HBoxLayout northLayout = new HBoxLayout();
		northLayout.setHBoxLayoutAlign(HBoxLayoutAlign.STRETCH);
		north.setLayout(northLayout);

		// 4 calendar controls:
		final FormPanel northCalendar = new FormPanel();
		northCalendar.setHeading("Calendar Controls");
		northCalendar.setBorders(true);
		northCalendar.setWidth("40%");
		north.add(northCalendar);
		
		// fields for form
		// Date - when this changes, change the start of the calendar view
	    final DateField dt = new DateField();
	    dt.setValue(startCalendarDay);
	    dt.setFieldLabel("Start Date");
		dt.setToolTip("Set the schedule and display start day");
	    dt.addListener(Events.Valid, new Listener<BaseEvent>() {
	    	public void handleEvent(BaseEvent be) {
	            startCalendarDay = dt.getValue();
	            updateCalendar();
	    	}
	    });
	    northCalendar.add(dt);

		// Days - when this changes, change the length of the calendar view
		final SimpleComboBox<Integer> days;
		days = new SimpleComboBox<Integer>();
		days.add(1);
		days.add(2);
		days.add(3);
		days.add(4);
		days.add(5);
		days.add(6);
		days.add(7);
		days.setToolTip("Set the schedule and display duration");

		days.setFieldLabel("Days");
		days.setEditable(false);
		days.setSimpleValue(numCalendarDays);
	    days.addListener(Events.Valid, new Listener<BaseEvent>() {
	    	public void handleEvent(BaseEvent be) {
	    		numCalendarDays = days.getSimpleValue(); 
	            updateCalendar();
	    	}
	    });
		northCalendar.add(days);
		
		// Timezone - controls the reference for all the date/times in the tab
		final SimpleComboBox<String> tz;
		tz = new SimpleComboBox<String>();
		tz.add("UTC");
		tz.add("ET");
		tz.setToolTip("Set the timezone for all dates/times");

		tz.setFieldLabel("TZ");
		tz.setEditable(false);
		tz.setSimpleValue(timezone);
	    tz.addListener(Events.Valid, new Listener<BaseEvent>() {
	    	public void handleEvent(BaseEvent be) {
	    		timezone = tz.getSimpleValue();
	    		baseUrl = "/periods/" + timezone;
	        	west.pe.setRootURL(baseUrl);
	            updateCalendar();
	    	}
	    });
		northCalendar.add(tz);

/*		// Update Button
	    final Button updateBtn;
	    updateBtn = new Button("Update");
		updateBtn.setToolTip("Modify the schedule and display range of calendar");
	    updateBtn.addListener(Events.OnClick, new Listener<BaseEvent>() {
	    	public void handleEvent(BaseEvent be) {
	            updateCalendar();
	    	}
	    });
		northCalendar.add(updateBtn);*/
		
		
		// 1 schedule controls
		final FormPanel northSchedule = new FormPanel();
		northSchedule.setHeading("Schedule Control");
		northSchedule.setBorders(true);
		northSchedule.setWidth("15%");
		north.add(northSchedule);
		
		// Auto schedules the current calendar
		Button scheduleButton = new Button("Schedule");
		scheduleButton.setToolTip("Generate a schedule for free periods over the specified calendar range");
		scheduleButton.addSelectionListener(new SelectionListener<ButtonEvent>() {
			@Override
			public void componentSelected(ButtonEvent be) {
	    		HashMap<String, Object> keys = new HashMap<String, Object>();
	    		String startStr = DateTimeFormat.getFormat("yyyy-MM-dd").format(startCalendarDay) + " 00:00:00";
	    		keys.put("start", startStr);
	    		keys.put("duration", numCalendarDays);
				String msg = "Scheduling from " + startStr + " for " + numCalendarDays.toString() + " days.";
				final MessageBox box = MessageBox.wait("Calling Scheduling Algorithm", msg, "Be Patient ...");
				JSONRequest.post("/runscheduler", keys,
						new JSONCallbackAdapter() {
							public void onSuccess(JSONObject json) {
								System.out.println("runscheduler onSuccess");
								updateCalendar();
								box.close();
							}
						});
			}
		});
		northSchedule.add(scheduleButton);
		
		
		// 4 nominee controls:
		final FormPanel northNominee = new FormPanel();
		northNominee.setHeading("Vacancy Control");
		northNominee.setBorders(true);
	    northNominee.setWidth("45%");
		north.add(northNominee);
			
		// Nominee date
	    final DateField vacancyDate = new DateField();
	    vacancyDate.setValue(startVacancyDate);
	    vacancyDate.setFieldLabel("Start Date");
		vacancyDate.setToolTip("Set the start day for the vacancy to be filled");
	    vacancyDate.addListener(Events.Select, new Listener<BaseEvent>() {
	    	public void handleEvent(BaseEvent be) {
	    		System.out.println("TimeField Listener date " + vacancyDate.getValue().toString());
	            startVacancyDate = vacancyDate.getValue();
	    	}
	    });
	    northNominee.add(vacancyDate);
	    
	    // Nominee time
	    final TimeField vacancyTime = new TimeField();
	    vacancyTime.setFormat(DateTimeFormat.getFormat("HH:mm"));
	    vacancyTime.setValue(startVacancyTime);
	    vacancyTime.setFieldLabel("Start Time");
		vacancyTime.setToolTip("Set the start time for the vacancy to be filled");
	    vacancyTime.addListener(Events.Select, new Listener<BaseEvent>() {
	    	public void handleEvent(BaseEvent be) {
	    		System.out.println("TimeField Listener time " + vacancyTime.getValue().toString());
	            startVacancyTime = vacancyTime.getValue();
	    	}
	    });
	    northNominee.add(vacancyTime);

		// Nominee maximum duration
		final SimpleComboBox<String> hours = new SimpleComboBox<String>();
		final HashMap<String, Integer> durChoices = new HashMap<String, Integer>();
		String noChoice = new String("none");
		durChoices.put(noChoice, 0);
		hours.add(noChoice);
		for (int m = 15; m < 8*60; m += 15) {
			String key = TimeUtils.min2sex(m);
			durChoices.put(key, m);
			hours.add(key);
		}
		hours.setToolTip("Set the maximum vacancy duration");
		hours.setFieldLabel("Duration");
		hours.setEditable(false);
	    hours.addListener(Events.Select, new Listener<BaseEvent>() {
	    	public void handleEvent(BaseEvent be) {
	    		numVacancyMinutes = durChoices.get(hours.getSimpleValue()); 
	    	}
	    });
		northNominee.add(hours);
		
		// Nominee options		
		northNominee.add(new LabelField());
		//final CheckBoxGroup nomineeOptions = new CheckBoxGroup();
		nomineeOptions.setSpacing(10);
		nomineeOptions.setFieldLabel("Selection Options");
		// timeBetween
		CheckBox timeBetween = new CheckBox();
		timeBetween.setBoxLabel("ignore timeBetween?");
		timeBetween.setTitle("Ignore sessions' timeBetween limits?");
		timeBetween.setValue(false);
		nomineeOptions.add(timeBetween);
		// minimum
		CheckBox minimum = new CheckBox();
		minimum.setBoxLabel("ignore minimum?");
		minimum.setTitle("Ignore sessions' minimum duration limits?");
		minimum.setValue(false);
		nomineeOptions.add(minimum);
		// blackout
		CheckBox blackout = new CheckBox();
		blackout.setBoxLabel("ignore blackout?");
		blackout.setTitle("Ignore observers' blackout periods?");
		blackout.setValue(false);
		nomineeOptions.add(blackout);
		// backup
		CheckBox backup = new CheckBox();
		backup.setBoxLabel("only backups?");
		backup.setTitle("Use only sessions marked as backups?");
		backup.setValue(false);
		nomineeOptions.add(backup);
		// completed
		CheckBox completed = new CheckBox();
		completed.setBoxLabel("use completed?");
		completed.setTitle("Include completed sessions?");
		completed.setValue(false);
		nomineeOptions.add(completed);
		northNominee.add(nomineeOptions);
		
	    // Fetch nominees
		final Button nomineesButton = new Button("Nominees");
		nomineesButton.setToolTip("Request possible periods for the selected time");
	    nomineesButton.addListener(Events.OnClick, new Listener<BaseEvent>() {
	    	public void handleEvent(BaseEvent be) {
	            updateNominees(east);
	    	}
	    });
		northNominee.add(nomineesButton);
		
		
		BorderLayoutData northData = new BorderLayoutData(LayoutRegion.NORTH, 200);
		northData.setMargins(new Margins(5,5,0,5));
		//northData.

		// to the left, the period explorer
		west = new ScheduleCalendar(startCalendarDay, numCalendarDays);
		west.addButtonsListener(this);
		west.setDefaultDate(startCalendarDay);
		BorderLayoutData westData = new BorderLayoutData(LayoutRegion.WEST, 550);
		westData.setMargins(new Margins(5));
		westData.setSplit(true);
		westData.setCollapsible(true);


		// to the right, the calendar
		center = new ContentPanel();
/*		center = new ContentPanel() {
			protected void onRender(Element target, int index) {
				super.onRender(target, index);
				el().addEventsSunk(Event.ONCLICK);
			}
		};
		center.addListener(Events.OnClick,
				new Listener<BaseEvent>() {
				    public void handleEvent(BaseEvent be) {
					    GWT.log(be.toString(), null);
					    GWT.log(be.getSource().toString(), null);
					    GWT.log(be.getClass().toString(), null);
					    GWT.log(be.getType().toString(), null);
				    }
			    });*/
  		center.setHeading("Center: Calendar");
		center.setScrollMode(Scroll.AUTOX);
		
		// calendar
		dayView = new DayView();
		dayView.setDate(startCalendarDay); //calendar date, not required
		dayView.setDays((int) numCalendarDays); //number of days displayed at a time, not required
		dayView.setWidth("100%");
		dayView.setTitle("Schedule Calendar");
		CalendarSettings settings = new CalendarSettings();
		// this fixes offset issue with time labels
		settings.setOffsetHourLabels(false);
		// 15-min. boundaries!
		settings.setIntervalsPerHour(4);
		settings.setEnableDragDrop(true);
		dayView.setSettings(settings);
		dayView.addValueChangeHandler(new ValueChangeHandler<Appointment>(){
	        public void onValueChange(ValueChangeEvent<Appointment> event) {
	            String msg = "Details of Period for Session: " + event.getValue().getDescription();   
	            Window.alert(msg);
	        }               
	    });	
		//dayView.addSelectionHandler(handler); // TODO handle nominee selecion in calendar?
		center.add(dayView);
		
		BorderLayoutData centerData = new BorderLayoutData(LayoutRegion.CENTER); //, 500);
		centerData.setMargins(new Margins(5, 0, 5, 0));
		//centerData.setSplit(true);
		//centerData.setCollapsible(true);
		
		// in the east, nominee periods
		east = new NomineePanel(this);

		BorderLayoutData eastData = new BorderLayoutData(LayoutRegion.EAST, 300);
		eastData.setMargins(new Margins(5));
		eastData.setSplit(true);
		eastData.setCollapsible(true);
		
		// add all the components to this parent panel
		add(north, northData);
		add(west, westData);
		add(center, centerData);
		add(east, eastData);

		updateCalendar();
	}
	
	private void updateNominees(ContentPanel panel) {
		// TODO get start and duration from the calendar!
		startVacancyDateTime = startVacancyDate;
		startVacancyDateTime.setHours(startVacancyTime.getHour());
		startVacancyDateTime.setMinutes(startVacancyTime.getMinutes());
		startVacancyDateTime.setSeconds(0);
		String startStr = DateTimeFormat.getFormat("yyyy-MM-dd HH:mm:ss").format(startVacancyDateTime);
		
		HashMap<String, Object> keys = new HashMap<String, Object>();
		keys.put("start", startStr);
		keys.put("duration", numVacancyMinutes);
		keys.put("timeBetween", (Boolean) nomineeOptions.get(0).getValue()); // ignore timeBetween limit?
		keys.put("minimum", (Boolean) nomineeOptions.get(1).getValue());     // ignore minimum duration limit?
		keys.put("blackout", (Boolean) nomineeOptions.get(2).getValue());    // ignore observer blackout times?
		keys.put("backup", (Boolean) nomineeOptions.get(3).getValue());      // use only backup sessions?
		keys.put("completed", (Boolean) nomineeOptions.get(4).getValue());   // include completed sessions?
		east.updateKeys(keys);
		east.loadData();
		
		panel.setHeading("East: Nominee Periods for " + startStr);
	}
	
    public void updateCalendar() {	
    	GWT.log("updateCalendar", null);

    	// construct the url that gets us our periods for the explorer
		String startStr = DateTimeFormat.getFormat("yyyy-MM-dd").format(startCalendarDay);
		String url = baseUrl + "?startPeriods=" + startStr + "&daysPeriods=" + Integer.toString(numCalendarDays);
		
		// get the period explorer to load these
		RequestBuilder builder = new RequestBuilder(RequestBuilder.GET, url);
		DynamicHttpProxy<BasePagingLoadResult<BaseModelData>> proxy = west.pe.getProxy();
		proxy.setBuilder(builder);
		west.setDefaultDate(startCalendarDay);
		west.pe.loadData();
		
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
	            	// JSON periods -> JAVA periods
	                List<Period> periods = new ArrayList<Period>();
	                JSONArray ps = json.get("periods").isArray();
	                for (int i = 0; i < ps.size(); ++i) {
	                	Period period = Period.parseJSON(ps.get(i).isObject());
	                	if (period != null){
	                		periods.add(period);
	                	}
	                }
	                // update the gwt-cal widget
	                loadAppointments(periods);
	            }
	    });
	}		
	    
    // updates the gwt-cal widget w/ given periods
    private void loadAppointments(List<Period> periods) {	    
		dayView.suspendLayout();
		dayView.clearAppointments();
		for(Period p : periods) {
		        Appointment appt = new Appointment();
		        appt.setStart(p.getStart());
		        appt.setEnd(p.getEnd());
		        appt.setTitle("Period");
		        appt.setDescription(p.getHandle());
		        appt.addStyleName("gwt-appointment-blue");
		        dayView.addAppointment(appt);
		}
		dayView.resumeLayout();
    }
}	
	
