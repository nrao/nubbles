package edu.nrao.dss.client;



import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import com.extjs.gxt.ui.client.Style.LayoutRegion;
import com.extjs.gxt.ui.client.Style.Scroll;
import com.extjs.gxt.ui.client.data.BaseModelData;
import com.extjs.gxt.ui.client.data.BasePagingLoadResult;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.util.Margins;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.button.ToolButton;
import com.extjs.gxt.ui.client.widget.form.FormPanel;
import com.extjs.gxt.ui.client.widget.form.Time;
import com.extjs.gxt.ui.client.widget.layout.BorderLayout;
import com.extjs.gxt.ui.client.widget.layout.BorderLayoutData;
import com.extjs.gxt.ui.client.widget.layout.HBoxLayout;
import com.extjs.gxt.ui.client.widget.layout.HBoxLayout.HBoxLayoutAlign;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONObject;

import edu.nrao.dss.client.util.dssgwtcal.Appointment;
import edu.nrao.dss.client.util.dssgwtcal.CalendarSettings;
import edu.nrao.dss.client.util.dssgwtcal.DayView;
import edu.nrao.dss.client.util.dssgwtcal.Event;

// This class is the new version of the Beta Test's Scheduling Page.

public class Schedule extends ContentPanel {
	
	public ScheduleCalendar west;
	public VacancyControl northNominee;
	ScheduleControl northSchedule;
	private NomineePanel east;
	private ContentPanel center;

	private DayView dayView;
	
	Date startCalendarDay = new Date();
	Integer numCalendarDays = 3;
	String timezone = "UTC";
	String baseUrl = "/periods/" + timezone;
	
	// scoring sessions
	Scores scores;
	private ScoresComboBox scoresComboBox;
	private ScoresForCalendar scoresDisplay;
	private float[] calendarScores; 

	
	Integer numVacancyMinutes = 2;
	Date startVacancyDate = new Date();
	Time startVacancyTime = new Time();
	public Date startVacancyDateTime = new Date();
	
	private ArrayList<String> sess_handles = new ArrayList<String>();
	
	public Schedule() {
			super();
			initLayout();
	}	
	
	public String getTimeZone() {
		return timezone;
	}
	
	protected void initLayout() {
		setHeaderVisible(true);
		setLayout(new BorderLayout());
		
		// ======================== Controls ===================================
		setCollapsible(false);
		setBodyBorder(false);
		setFrame(false);
		setHeaderVisible(false);
		setBodyStyle("backgroundColor: white;");
		getHeader().addTool(new ToolButton("x-tool-gear"));
		getHeader().addTool(new ToolButton("x-tool-close"));

		// now for the child panels:
		// At the top, control widgets

		final LayoutContainer north = new LayoutContainer();
		HBoxLayout northLayout = new HBoxLayout();
		northLayout.setHBoxLayoutAlign(HBoxLayoutAlign.STRETCH);
		north.setLayout(northLayout);

		// on the left, calendar controls:
		final FormPanel northCalendar = new CalendarControl(this);
		north.add(northCalendar);
		
		// in the middle, schedule controls
        northSchedule = new ScheduleControl(this);
        north.add(northSchedule);
		
//<<<<<<< local
		// on the right, nominee controls:
        northNominee = new VacancyControl(this);
        north.add(northNominee);
//=======
//		// Timezone - controls the reference for all the date/times in the tab
//		final SimpleComboBox<String> tz;
//		tz = new SimpleComboBox<String>();
//		tz.setForceSelection(true);
//		tz.add("UTC");
//		tz.add("ET");
//		tz.setToolTip("Set the timezone for all dates/times");
//
//		tz.setFieldLabel("TZ");
//		tz.setEditable(false);
//		tz.setSimpleValue(timezone);
//	    tz.addListener(Events.Valid, new Listener<BaseEvent>() {
//	    	public void handleEvent(BaseEvent be) {
//	    		timezone = tz.getSimpleValue();
//	    		baseUrl = "/periods/" + timezone;
//	        	west.pe.setRootURL(baseUrl);
//	            updateCalendar();
//	    	}
//	    });
//		northCalendar.add(tz);
//		
//		// 1 schedule controls
//		final FormPanel northSchedule = new FormPanel();
//		northSchedule.setHeading("Schedule Control");
//		northSchedule.setBorders(true);
//		northSchedule.setWidth("25%");
//		north.add(northSchedule);
//		
//		// Auto schedules the current calendar
//		Button scheduleButton = new Button("Schedule");
//		scheduleButton.setToolTip("Generate a schedule for free periods over the specified calendar range");
//		scheduleButton.addSelectionListener(new SelectionListener<ButtonEvent>() {
//			@Override
//			public void componentSelected(ButtonEvent be) {
//	    		HashMap<String, Object> keys = new HashMap<String, Object>();
//	    		String startStr = DateTimeFormat.getFormat("yyyy-MM-dd").format(startCalendarDay) + " 00:00:00";
//	    		Integer numScheduleDays = numCalendarDays < 2 ? 1 : (numCalendarDays -1); 
//	    		keys.put("start", startStr);
//	    		keys.put("duration", numCalendarDays);
//	    		keys.put("tz", timezone);
//				String msg = "Scheduling from " + startStr + " (" + timezone + ")" + " until " + numScheduleDays.toString() + " days later at 8:00 (ET).";
//				final MessageBox box = MessageBox.wait("Calling Scheduling Algorithm", msg, "Be Patient ...");
//				JSONRequest.post("/runscheduler", keys,
//						new JSONCallbackAdapter() {
//							public void onSuccess(JSONObject json) {
//								updateCalendar();
//								box.close();
//							}
//						});
//			}
//		});
//		northSchedule.add(scheduleButton);
//		
//		Button emailButton = new Button("Email");
//		emailButton.setToolTip("Emails a schedule to staff and observers starting now and covering the next two days");
//		emailButton.addSelectionListener(new SelectionListener<ButtonEvent>() {
//			@Override
//			public void componentSelected(ButtonEvent be) {
//	    		HashMap<String, Object> keys = new HashMap<String, Object>();
//				String msg = "Generating scheduling email for observations over the next two days";
//				final MessageBox box = MessageBox.wait("Getting Email Text", msg, "Be Patient ...");
//				
//				// Must set keys here somehow to transmit proper time range.  What is the time range?
//	    		String startStr = DateTimeFormat.getFormat("yyyy-MM-dd").format(startCalendarDay) + " 00:00:00";
//	    		keys.put("start", startStr);
//	    		keys.put("duration", numCalendarDays);
//	    		keys.put("tz", timezone);
//	    		
//				JSONRequest.get("/schedule/email", keys,
//						new JSONCallbackAdapter() {
//							public void onSuccess(JSONObject json) {
//								String addr[] = new String[3];
//								String subject[] = new String[3];
//								String body[] = new String[3];
//								String address_key[] = {"observer_address", "deleted_address", "staff_address"};
//								String subject_key[] = {"observer_subject", "deleted_subject", "staff_subject"};
//								String body_key[] = {"observer_body", "deleted_body", "staff_body"};
//	
//								try
//								{
//									for (int j = 0; j < 3; ++j)
//									{
//										JSONArray emails = json.get(address_key[j]).isArray();
//										//String addr = "";
//										addr[j] = "";
//
//										for (int i = 0; i < emails.size(); ++i)
//										{
//											addr[j] += emails.get(i).isString().stringValue() + ", ";
//										}
//
//										if (addr[j].length() > 2)
//										{
//											addr[j] = addr[j].substring(0, addr[j].length() - 2); // Get rid of last comma.
//										}
//
//										subject[j] = json.get(subject_key[j]).isString().stringValue();
//										body[j] = json.get(body_key[j]).isString().stringValue();
//									}
//
//									EmailDialogBox dlg = new EmailDialogBox(addr, subject, body);
//									dlg.show();
//									box.close();
//								}
//								catch (Exception e)
//								{
//									GWT.log("JSON Email request: " + e);
//								}
//							}
//						});
//			}
//		});
//		northSchedule.add(emailButton);
//		
//		// publishes all periods currently displayed (state moved from pending to scheduled)
//		Button publishButton = new Button("Publish");
//		publishButton.setToolTip("Publishes all the currently visible Periods: state is moved from Pending (P) to Scheduled (S) and become visible to Observer.");
//		publishButton.addSelectionListener(new SelectionListener<ButtonEvent>() {
//			@Override
//			public void componentSelected(ButtonEvent be) {
//				// make the JSON request for the periods so we can make appointments
//				// we need the same url in a different format
//	    		HashMap<String, Object> keys = new HashMap<String, Object>();
//	    		String startStr = DateTimeFormat.getFormat("yyyy-MM-dd").format(startCalendarDay) + " 00:00:00";
//	    		keys.put("start", startStr);
//	    		keys.put("duration", numCalendarDays);
//	    		keys.put("tz", timezone);	    		
//				//final MessageBox box = MessageBox.confirm("Publish Pending Periods", "r u sure?", l);
//				JSONRequest.post("/periods/publish", keys,
//						new JSONCallbackAdapter() {
//							public void onSuccess(JSONObject json) {
//								updateCalendar();
//							}
//						});
//			}
//		});
//		northSchedule.add(publishButton);
//		
//		// deletes all pending periods currently displayed (state moved from pending to deleted)
//		Button deletePendingBtn = new Button("Delete Pending");
//		deletePendingBtn.setToolTip("Deletes all the currently visible Periods in the Pending (P) state.");
//		deletePendingBtn.addSelectionListener(new SelectionListener<ButtonEvent>() {
//			@Override
//			public void componentSelected(ButtonEvent be) {
//				// make the JSON request for the periods so we can make appointments
//				// we need the same url in a different format
//	    		HashMap<String, Object> keys = new HashMap<String, Object>();
//	    		String startStr = DateTimeFormat.getFormat("yyyy-MM-dd").format(startCalendarDay) + " 00:00:00";
//	    		keys.put("start", startStr);
//	    		keys.put("duration", numCalendarDays);
//	    		keys.put("tz", timezone);	    		
//				//final MessageBox box = MessageBox.confirm("Publish Pending Periods", "r u sure?", l);
//				JSONRequest.post("/periods/delete_pending", keys,
//						new JSONCallbackAdapter() {
//							public void onSuccess(JSONObject json) {
//								updateCalendar();
//							}
//						});
//			}
//		});
//		northSchedule.add(deletePendingBtn);		
//		
//		// Factors
//		Button factorsButton = new Button("Factors");
//		factorsButton.setToolTip("Provides access to individual score factors for selected session and time range");
//		factorsDlg = new FactorsDlg();
//		factorsDlg.hide();
//		factorsButton.addSelectionListener(new SelectionListener<ButtonEvent>() {
//			@Override
//			public void componentSelected(ButtonEvent be) {
//				factorsDlg.show();
//			}
//		});
//		northSchedule.add(factorsButton);
//		
//		// Scores
//		scoresComboBox = new ScoresComboBox(this);
//		scoresComboBox.setFieldLabel("Scores");
//        northSchedule.add(scoresComboBox);
//        scores = new Scores(scoresComboBox, new ScoresForCalendar(this));
//		
//		// 4 nominee controls:
//		final FormPanel northNominee = new FormPanel();
//		northNominee.setHeading("Vacancy Control");
//		northNominee.setBorders(true);
//	    northNominee.setWidth("40%");
//		north.add(northNominee);
//			
//		// Nominee date
//	    vacancyDate.setValue(startVacancyDate);
//	    vacancyDate.setFieldLabel("Start Date");
//		vacancyDate.setToolTip("Set the start day for the vacancy to be filled");
//	    vacancyDate.addListener(Events.Valid, new Listener<BaseEvent>() {
//	    	public void handleEvent(BaseEvent be) {
//	            startVacancyDate = vacancyDate.getValue();
//	    	}
//	    });
//	    northNominee.add(vacancyDate);
//	    
//	    // Nominee time
//	    final TimeField vacancyTime = new TimeField();
//	    vacancyTime.setFormat(DateTimeFormat.getFormat("HH:mm"));
//	    vacancyTime.setValue(startVacancyTime);
//	    vacancyTime.setFieldLabel("Start Time");
//		vacancyTime.setToolTip("Set the start time for the vacancy to be filled");
//	    vacancyTime.addListener(Events.Change, new Listener<BaseEvent>() {
//	    	public void handleEvent(BaseEvent be) {
//	            startVacancyTime = vacancyTime.getValue();
//	    	}
//	    });
//	    northNominee.add(vacancyTime);
//
//		// Nominee maximum duration
//		final SimpleComboBox<String> hours = new SimpleComboBox<String>();
//		final HashMap<String, Integer> durChoices = new HashMap<String, Integer>();
//		String noChoice = new String("none");
//		durChoices.put(noChoice, 0);
//		hours.add(noChoice);
//		hours.setForceSelection(true);
//		for (int m = 15; m < 12*60+15; m += 15) {
//			String key = TimeUtils.min2sex(m);
//			durChoices.put(key, m);
//			hours.add(key);
//		}
//		hours.setToolTip("Set the maximum vacancy duration");
//		hours.setFieldLabel("Duration");
//		hours.setEditable(false);
//	    hours.addListener(Events.Select, new Listener<BaseEvent>() {
//	    	public void handleEvent(BaseEvent be) {
//	    		numVacancyMinutes = durChoices.get(hours.getSimpleValue()); 
//	    	}
//	    });
//		northNominee.add(hours);
//		
//		// Nominee options		
//		northNominee.add(new LabelField());
//		//final CheckBoxGroup nomineeOptions = new CheckBoxGroup();
//		nomineeOptions.setSpacing(15);
//		nomineeOptions.setFieldLabel("Selection Options");
//		// timeBetween
//		CheckBox timeBetween = new CheckBox();
//		timeBetween.setBoxLabel("ignore timeBetween?");
//		timeBetween.setTitle("Ignore sessions' timeBetween limits?");
//		timeBetween.setValue(false);
//		nomineeOptions.add(timeBetween);
//		// minimum
//		CheckBox minimum = new CheckBox();
//		minimum.setBoxLabel("ignore minimum?");
//		minimum.setTitle("Ignore sessions' minimum duration limits?");
//		minimum.setValue(false);
//		nomineeOptions.add(minimum);
//		// blackout
//		CheckBox blackout = new CheckBox();
//		blackout.setBoxLabel("ignore blackout?");
//		blackout.setTitle("Ignore observers' blackout periods?");
//		blackout.setValue(false);
//		nomineeOptions.add(blackout);
//		// backup
//		CheckBox backup = new CheckBox();
//		backup.setBoxLabel("only backups?");
//		backup.setTitle("Use only sessions marked as backups?");
//		backup.setValue(false);
//		nomineeOptions.add(backup);
//		// completed
//		CheckBox completed = new CheckBox();
//		completed.setBoxLabel("use completed?");
//		completed.setTitle("Include completed sessions?");
//		completed.setValue(false);
//		nomineeOptions.add(completed);
//		// rfi
//		CheckBox rfi = new CheckBox();
//		rfi.setBoxLabel("ignore RFIexclusion?");
//		rfi.setTitle("Ignore sessions' day time RFI exclusion?");
//		rfi.setValue(false);
//		nomineeOptions.add(rfi);
//		
//		northNominee.add(nomineeOptions);
//		
//	    // Fetch nominees
//		final Button nomineesButton = new Button("Nominees");
//		nomineesButton.setToolTip("Request possible periods for the selected time");
//	    nomineesButton.addListener(Events.OnClick, new Listener<BaseEvent>() {
//	    	public void handleEvent(BaseEvent be) {
//	            updateNominees(east);
//	    	}
//	    });
//		northNominee.add(nomineesButton);
//		
//>>>>>>> other
//		
		BorderLayoutData northData = new BorderLayoutData(LayoutRegion.NORTH, 200);
		northData.setMargins(new Margins(5,5,0,5));

		// ======================== Displays ===================================
		// to the left, the period explorer
		west = new ScheduleCalendar(startCalendarDay, numCalendarDays);
		west.addButtonsListener(this);
		west.setDefaultDate(startCalendarDay);
		BorderLayoutData westData = new BorderLayoutData(LayoutRegion.WEST, 780);
		westData.setMargins(new Margins(5));
		westData.setSplit(true);
		westData.setCollapsible(true);

		// in the middle, the calendar
		center = new ContentPanel(); // TODO extend to bottom of panel
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
  		center.setHeading("Calendar");
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
		// when a period is clicked, a user can insert a different session
		// but we need all those session names
		getSessionOptions();
		dayView.addValueChangeHandler(new ValueChangeHandler<Appointment>(){
	        public void onValueChange(ValueChangeEvent<Appointment> event) {
	        	// seed the PeriodDialog w/ details from the period that just got clicked
	            String periodUrl = "/periods/UTC/" + event.getValue().getTitle();
	    	    JSONRequest.get(periodUrl, new JSONCallbackAdapter() {
		            @Override
		            public void onSuccess(JSONObject json) {
		            	// JSON period -> JAVA period
	                 	Period period = Period.parseJSON(json.get("period").isObject());
                        // display info about this period, and give options to change it
	                 	PeriodSummaryDlg dlg = new PeriodSummaryDlg(period, sess_handles, (Schedule) north.getParent());
		            }
		    });	            
	            
	        }               
	    });	
		//dayView.addSelectionHandler(handler); // TODO handle nominee selection in calendar?
		center.add(dayView);
		
		BorderLayoutData centerData = new BorderLayoutData(LayoutRegion.CENTER); //, 500);
		centerData.setMargins(new Margins(5, 0, 5, 0));
		//centerData.setSplit(true);
		//centerData.setCollapsible(true);
		
		// to the right, nominee periods
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
	
	public FactorsDlg getFactorsDlg() {
		return northSchedule.factorsDlg;
	}
	
	public ScoresComboBox getScoresComboBox() {
	    return scoresComboBox;	
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
		keys.put("timeBetween", (Boolean) northNominee.nomineeOptions.get(0).getValue()); // ignore timeBetween limit?
		keys.put("minimum", (Boolean) northNominee.nomineeOptions.get(1).getValue());     // ignore minimum duration limit?
		keys.put("blackout", (Boolean) northNominee.nomineeOptions.get(2).getValue());    // ignore observer blackout times?
		keys.put("backup", (Boolean) northNominee.nomineeOptions.get(3).getValue());      // use only backup sessions?
		keys.put("completed", (Boolean) northNominee.nomineeOptions.get(4).getValue());   // include completed sessions?
		keys.put("rfi", (Boolean) northNominee.nomineeOptions.get(5).getValue());         // ignore RFI exclusion flag?
		east.updateKeys(keys);
		east.loadData();
		
		east.setHeading("Nominee Periods for " + startStr + " " + timezone);
	}
	
    public void updateCalendar() {	
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
	                		// TODO: really we should be using period state to keep these periods out
	                		if (period.getDuration() > 0) {
                        		periods.add(period);
	                        }
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
                // TODO: format title & description better			
			    String title = Integer.toString(p.getId());
			    String windowInfo = "";
			    String type = "not windowed!"; // TODO: need better way to indicate period attributes
			    if (p.isWindowed()) {
			    	windowInfo = " +" + Integer.toString(p.getWindowDaysAhead()) + "/-" + Integer.toString(p.getWindowDaysAfter());
			    	type = p.isDefaultPeriod() ? "default period" : "choosen period";
			    }
			    String desc = p.getHandle() + windowInfo;
			    Event event = new Event(title, desc, p.getStart(), p.getEnd(), type);
		        dayView.addAppointments(event.getAppointments());
		        
		}
		
		//dayView.add
		dayView.resumeLayout();
		
		// clear the header if no scores being displayed
        if (dayView.getScores() == null) {
        	setCalendarHeader("Calendar");
        }
		
		// clear out the scores so that next time the calendar is updated,
		// unless new scores have been provided, the present display of scores
		// is erased
		dayView.clearScores();
    }
    
    // gets all the session handles (sess name (proj name)) and holds on to them
    // for use in lists (e.g. PeriodDialog)
    private void getSessionOptions() {
        JSONRequest.get("/sessions/options"
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
	
	public void setCalendarHeader(String header) {
		center.setHeading(header);
	}
	
	@SuppressWarnings("unchecked")
	public void showSessionScores(String session) {
		scoresComboBox.setSimpleValue(session);
		scoresComboBox.getSessionScores(session);
	}

}	
	
