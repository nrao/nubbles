package edu.nrao.dss.client;

import java.util.Date;
import java.util.HashMap;

import com.extjs.gxt.ui.client.Style.Orientation;
import com.extjs.gxt.ui.client.Style.Scroll;
import com.extjs.gxt.ui.client.event.BaseEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.DateField;
import com.extjs.gxt.ui.client.widget.form.FormPanel;
import com.extjs.gxt.ui.client.widget.form.SimpleComboBox;
import com.extjs.gxt.ui.client.widget.form.ComboBox.TriggerAction;
import com.extjs.gxt.ui.client.widget.layout.RowLayout;
import com.google.gwt.core.client.GWT;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONObject;

import edu.nrao.dss.client.util.JSONCallbackAdapter;
import edu.nrao.dss.client.util.JSONRequest;

public class WindowCalendar extends ContentPanel {
	
	private WindowCalTable calendar = new WindowCalTable();
    private DateField start = new DateField();
    private SimpleComboBox<String> numDays = new SimpleComboBox<String>();
    private Button update = new Button();
    
    
	public WindowCalendar() {
		initLayout();
		initListeners();
	}
	
	private void initLayout() {	
	
		setLayout(new RowLayout(Orientation.VERTICAL));
	
		setScrollMode(Scroll.AUTO);
		setBorders(false);
		setHeaderVisible(true);
		setHeading("Window Calendar");
		
		FormPanel fp = new FormPanel();
		fp.setHeaderVisible(false);

		// when to start the schedule
		start.setFieldLabel("Start Date");
		fp.add(start);
		
		// for how long?
		int maxDays = 30;
		numDays.setFieldLabel("# Days");
		numDays.setTriggerAction(TriggerAction.ALL);
		for (int i = 2; i < maxDays; i++) {
			numDays.add(Integer.toString(i));
		}
		fp.add(numDays);
		
		update.setText("Update");
		fp.add(update);
		
		add(fp);
		
	    add(calendar);
    
	}
	
	private void initListeners() {
		
	    update.addListener(Events.OnClick, new Listener<BaseEvent>() {
	    	public void handleEvent(BaseEvent be) {
	    		// update the calendar using the controls
	    		getWindows();
	    		
	    	}
	    });
	   
	}	
	
	public void setupCalendar() {
		
		// set default values for controls
		start.setValue(new Date()); // today
		numDays.setSimpleValue("1");
		
		// update the calendar
		getWindows();
	}
	
	public void getWindows() {
	    // update the calendar using the controls
		// /scheduler/windows?filterStartDate=2010-02-01&filterDuration=30&sortField=null&sortDir=NONE&offset=0&limit=50
		HashMap<String, Object> keys = new HashMap<String, Object>();
		//keys.put("startdate", DATE_FORMAT.format(day.getValue()));
		String startStr = DateTimeFormat.getFormat("yyyy-MM-dd").format(start.getValue()); //+ " 00:00:00";
		keys.put("filterStartDate", startStr);
		keys.put("filterDuration", numDays.getSimpleValue());
		JSONRequest.get("/scheduler/windows", keys  
			      , new JSONCallbackAdapter() {
			public void onSuccess(JSONObject json) {
				GWT.log(json.toString(), null);
				jsonToCalendar(json, start.getValue(), Integer.parseInt(numDays.getSimpleValue()));
			}
		});		
	}
	
	// converts the given windows json into a string matrix that looks like:
	// [[(Window Info (session name, hours, complete?), T or F; info, T or F; info, etc.]]
	// Ex:
	// [["GBT11A-001-01 (5.5/5.5) Not Cmp., T, T; Default (0.0) (D), F, F]]
	private void jsonToCalendar(JSONObject json, Date start, int numDays) {
		int i;
		String text;
		int numExCols = 3; // | Session (total/billed) Complete? | Start | [dates] | End |
		int colOffset = 2; // because first two columns are: | Session (total/billed) Complete? | Start
		int startColIndex = 1;
		int endColIndex = numDays + 2;
		String[] headers = new String[numDays + numExCols];
		String[] dateStrs = new String[numDays];
		Date[] dates = new Date[numDays];
		Date calStartDate, calEndDate;
		

		// construct the column headers for the calendar: 
		// Session (total/billed) Complete? | Start | 2010-01-01 | 2010-01-02 | End |
		headers[0] = "Session (total/billed) Complete?";
		headers[startColIndex] = "Start";
		headers[endColIndex] = "End";
		
		// TODO: believe it or fucking not, DST causes a one hour offset when we do this!!! WTF!!!!
		String day;
		long startSecs = start.getTime();
		for (i = 0; i < numDays; i++) {
			long offset = ((long) i) * 1000 * 60 * 60 * 24;
			long dateSecs = startSecs + offset;
			Date dt = new Date(dateSecs);
			String dayStr = DateTimeFormat.getFormat("yyyy-MM-dd").format(dt);
			headers[i+colOffset] = dayStr;
			// for use later
			dateStrs[i] = dayStr;
			dates[i] = dt;
		}

		// what are the endpoints of our calendar
		calStartDate = start;
		calEndDate   = dates[numDays-1];
		
		// determine the dimensions of our calendar
		JSONArray windows = json.get("windows").isArray();
		int numWindows = windows.size();	
		int calRows = numWindows;
		int calCols = numDays + numExCols;
		String [][] cal = new String[calRows][calCols];
		
		// now populate the calendar, row by row
		for (i = 0; i < numWindows; i++) {
			
		    JSONObject window = windows.get(i).isObject();
		    
		    // the first column is window info
		    String handle = window.get("handle").isString().stringValue();
		    Double total  = window.get("total_time").isNumber().doubleValue();
		    Double billed = window.get("time_billed").isNumber().doubleValue();
		    Boolean cmp   = window.get("complete").isBoolean().booleanValue();
		    String cmpStr = cmp ? "Cmp." : "Not Cmp.";
		    // Ex: GBT08A-001-01 (GBT08A-001) (8.0/8.0) Cmp."
		    cal[i][0] = handle + " (" + total.toString() + "/" + billed.toString() + ") " + cmpStr;
		    
		    // now gather up info about the window ranges and periods:
		    
		    // when does the window start and stop (not taking into account gaps)?
		    String wstartStr = window.get("start").isString().stringValue();
		    String wstopStr  = window.get("end").isString().stringValue();
			Date wstart = DateTimeFormat.getFormat("yyyy-MM-dd").parse(wstartStr);
			Date wstop  = DateTimeFormat.getFormat("yyyy-MM-dd").parse(wstopStr);

			// are there any gaps in the window (is this window non-contigious?)
			Boolean contigious = window.get("contigious").isBoolean().booleanValue();

			// ranges will be needed if the window is non-contigious.
			JSONArray ranges = window.get("ranges").isArray();
			int numRanges = ranges.size();
			String [][] calRanges = new String[numRanges][2];
			Date [][] calRangeDates = new Date[numRanges][2];
			
			for (int j = 0; j < numRanges; j++) {
				// parse the JSON
				JSONObject range = ranges.get(j).isObject();
			    String rStartStr = range.get("start").isString().stringValue();
			    String rEndStr   = range.get("end").isString().stringValue();
				
				// populate our table
				calRanges[j][0] = rStartStr;
				calRanges[j][1] = rEndStr;
				calRangeDates[j][0] = DateTimeFormat.getFormat("yyyy-MM-dd").parse(rStartStr);
				calRangeDates[j][1]  = DateTimeFormat.getFormat("yyyy-MM-dd").parse(rEndStr);
			}

			// where are the periods?
			JSONArray periods = window.get("periods").isArray();
			int numPeriods = periods.size();
			String [][] calPeriods = new String[numPeriods][4];
			Date [] calPeriodDates = new Date[numPeriods];
			
			for (int j = 0; j < numPeriods; j++) {
				
				// parse the JSON
				JSONObject period = periods.get(j).isObject();
			    String pstartStr  = period.get("date").isString().stringValue();
			    String pstate     = period.get("state").isString().stringValue();
			    Boolean pdefault  = period.get("wdefault").isBoolean().booleanValue();
                Double pbilled    = period.get("time_billed").isNumber().doubleValue();
                
                // populate our table
				calPeriods[j][0] = pstartStr;
				calPeriods[j][1] = pstate;
				calPeriods[j][2] = pdefault ? "T" : "F";
				calPeriods[j][3] = pbilled.toString();
				calPeriodDates[j] = DateTimeFormat.getFormat("yyyy-MM-dd").parse(pstartStr);
				
			}

			// Okay, now that we've gathered up info on window ranges & periods, we can
			// actually decide on what to display in each cell of the calendar
			
			// First, the second & last columns, which contains info on what happens to this window
			// outside of the calendar's range.  Second Column:
			String value, sep;
			if (calStartDate.after(wstart)) {
				value = "T;";
				// window ranges?
				for (int k = 0; k < numRanges; k++) {
					sep = value.compareTo("T;") == 0 ? " " : ", ";
					if (calStartDate.after(calRangeDates[k][0])) {
						if (calStartDate.after(calRangeDates[k][1])) {
							value += sep + calRanges[k][0] + " - " + calRanges[k][1];
						} else {
 							value += sep + "Starts: " + calRanges[k][0];
						}
					}
				}
	    		// periods?
	    		for (int k = 0; k < numPeriods; k++) {
	    			if (calPeriodDates[k].before(calStartDate)) {
	    				value += ", " + calPeriodToText(calPeriods[k]) + " on "  + calPeriods[k][0];
	    			}
	    		}
			} else {
				value = "F";
			}
			cal[i][startColIndex] = value;
				
			// Last Column:
			if (calEndDate.before(wstop)) {
				value = "T;";
				// window ranges?
				for (int k = 0; k < numRanges; k++) {
					sep = value.compareTo("T;") == 0 ? " " : ", ";
					if (calEndDate.before(calRangeDates[k][1])) {
						if (calEndDate.before(calRangeDates[k][0])) {
							value += sep + calRanges[k][0] + " - " + calRanges[k][1];
						} else {
 							value += sep + "Ends: " + calRanges[k][1];
						}
					}
				}
	    		// periods?
	    		for (int k = 0; k < numPeriods; k++) {
	    			if (calPeriodDates[k].after(calEndDate)) {
	    				value += ", " + calPeriodToText(calPeriods[k]) + " on "  + calPeriods[k][0];
	    			}
	    		}				
			} else {
				value = "F";
			}
			cal[i][endColIndex] = value;			
			
		    // the rest are the days - each showing whether it's part of the window 
		    for (int j = 0; j < numDays; j++) {
	    		text = "";
		    	boolean partOfWindow = isDateInWindow(dates[j], wstart, wstop);
		    	// but if this window is not contigious, make sure we aren't in a gap
		    	if (contigious == false && partOfWindow == true) {
		    		// we aren't in a gap if we fall into just ONE of the ranges
 		    		partOfWindow = false;
		    		for (int k = 0; k < numRanges; k++) {
		    			if (isDateInWindow(dates[j], calRangeDates[k][0], calRangeDates[k][1]) == true) {
		    				partOfWindow = true;
		    			}
		    		}
		    	}
		    	// for each day, does this window cover it?
		    	//cal[i][j+1] = partOfWindow ? "T" : "F";
		    	// if part of the window, might be more info to add, like:
		    	// * what day does the default & chosen fall on?
		    	if (partOfWindow) {
		    		// any periods on this day?
		    		for (int k = 0; k < numPeriods; k++) {
		    			if (calPeriodDates[k].equals(dates[j])) {
		    				text += calPeriodToText(calPeriods[k]);
		    			}
		    		}
		    		// insert a separator, if needed
		    		if (text.compareTo("") != 0) {
		    			text = "; " + text;
		    		}
		    	}
		    	cal[i][j+colOffset] = partOfWindow ? "T" + text : "F";
		    	
		    }
		}
	
		// now display it!
		calendar.loadCalendar(calRows, calCols, headers, cal);
	}

	private String calPeriodToText(String[] period) {
		String text;
		String state = "(" + period[1] + ")";
		String billed = "(" + period[3] + ")";
		String def = period[2] == "T" ? "Default" : "Chosen";
		// Ex: Default (P) (8.0)
		text = def + " " + state + " " + billed;
		return text;
	}
	
	private boolean isDateInWindow(Date dt, Date start, Date stop) {
		return ((dt.getTime() >= start.getTime()) && (dt.getTime() <= stop.getTime()));
	}
	
	public void loadCalendar(int rows, int cols, String[] headers, String[][] cal) {
		calendar.loadCalendar(rows, cols, headers, cal);
	}
	

}
