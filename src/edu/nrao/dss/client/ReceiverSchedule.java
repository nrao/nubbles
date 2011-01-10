package edu.nrao.dss.client;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Set;
import java.util.TreeSet;

import com.extjs.gxt.ui.client.Style.Orientation;
import com.extjs.gxt.ui.client.Style.Scroll;
import com.extjs.gxt.ui.client.event.BaseEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.util.Margins;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.layout.RowData;
import com.extjs.gxt.ui.client.widget.layout.RowLayout;
import com.google.gwt.core.client.GWT;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.FlexTable.FlexCellFormatter;

public class ReceiverSchedule extends ContentPanel {
	
	//04/11/2009
    private static final DateTimeFormat DATE_FORMAT = DateTimeFormat.getFormat("MM/dd/yyyy");
    private int count = 0;
    
    //private RcvrScheduleGrid grid = new RcvrScheduleGrid();
    private RcvrSchdGridPanel grid = new RcvrSchdGridPanel();
    private RcvrChangePanel change = new RcvrChangePanel();
//    private RcvrSchdEditPanel edit = new RcvrSchdEditPanel();
    
	public ReceiverSchedule() {
		initLayout();
		
		// populate the table w/ the rcvr schedule
		//getRcvrSchedule();
		grid.setupCalendar();

	}
	
	private void initLayout() {

		setLayout(new RowLayout(Orientation.VERTICAL));

		setScrollMode(Scroll.AUTO);
		setBorders(false);
		setHeaderVisible(false);
		
        add(grid);
        add(change, new RowData(1, -1, new Margins(4)));
//        add(edit, new RowData(1, -1, new Margins(4)));
        
        //TODO: better way to bind?
        grid.setParent(this);
//        edit.setParent(this);
        change.setParent(this);
        
	}	

	public void getRcvrSchedule(Date start, int numDays, boolean showMaintenanceDays) {
		
		HashMap<String, Object> keys = new HashMap<String, Object>();
		//keys.put("startdate", DATE_FORMAT.format(day.getValue()));
		String startStr = DateTimeFormat.getFormat("yyyy-MM-dd").format(start) + " 00:00:00";
		keys.put("startdate", startStr);
		keys.put("duration", numDays);
		JSONRequest.get("/receivers/schedule", keys  
			      , new JSONCallbackAdapter() {
			public void onSuccess(JSONObject json) {
				jsonToRcvrSchedule(json);
			}
		});
	}

	public void updateRcvrSchedule() {
	    grid.getRcvrSchedule();	
	}
	
	private void jsonToRcvrSchedule(JSONObject json) {
		
		// construct the header for the rcvr schedule calendar
		JSONArray rcvrs = json.get("receivers").isArray();
		int numRcvrs = rcvrs.size();
		// headers start w/ the date, up rx, down rx, then the list of the rcvrs
		int offset = 3;
		String[] headers = new String[numRcvrs + offset];
		headers[0] = "Date";
		headers[1] = "Up";
		headers[2] = "Down";
		String rcvr;
		String [] rx = new String[numRcvrs];
		for (int i = 0; i < numRcvrs; i++) {
			rcvr = rcvrs.get(i).isString().stringValue();
			headers[i+offset] = rcvr;
			rx[i] = rcvr;
		}
		change.loadRcvrs(rx);
	
		// now, get the rest of the schedule
		JSONObject schedule = json.get("schedule").isObject();
		int numDays = schedule.keySet().size();
		String[][] calendar = getRcvrCalStrs(schedule, rx, numDays);
		
		// use that to create the grid
		//grid.loadSchedule(numDays, headers.length, headers, calendar);
		
		// get the diff schedule
		JSONArray diff = json.get("diff").isArray();
		String[][] diffCalendar = new String[diff.size()][4];
		String day, up, down, available, on;
		for (int i = 0; i < diff.size(); i++) {
			JSONObject diffDay = diff.get(i).isObject();
			day = diffDay.get("day").isString().stringValue();
			// get the list of rcvrs that are going up this day
			JSONArray ups = diffDay.get("up").isArray(); //.isString().stringValue();
			up = "";
			for (int j = 0; j<ups.size(); j++) {
				up += ups.get(j).isString().stringValue() + " ";
			}
			// get the list of rcvrs that are going down this day
			JSONArray downs = diffDay.get("down").isArray(); // .isString().stringValue();
			down = "";
			for (int j = 0; j<downs.size(); j++) {
				down += downs.get(j).isString().stringValue() + " ";
			}
			// for this same day, use the earlier rcvr calendar to determine the 
			// longer list of rcvrs that will be available at the end of this day
			available = "";
//			for (int j=0; j<calendar.length; j++) {
//				String calDay = calendar[j][0];
//				//if (calendar[j][0] == day) {
//				if (calDay.compareTo(day) == 0) {
//					// grab the rcvrs that are on ("T")
//					for (int k=0; k<calendar[j].length; k++) {
//						on = calendar[j][k];
//						if (on.compareTo("T") == 0) {
//							available += headers[k] + " ";
//						}
//						
//					}
//				}
//			}
			diffCalendar[i][0] = day;
			diffCalendar[i][1] = up;
			diffCalendar[i][2] = down;
			diffCalendar[i][3] = available;
		}

		
		// get the maintenance days
		JSONArray maintJson = json.get("maintenance").isArray();		
		String[] maintenanceDays = new String[maintJson.size()];
		String mDay;
		for (int i = 0; i < maintJson.size(); i++) {
			//JSONObject mObj = maintJson.get(i).isObject();
			//mDay = mObj.get("date").isString().stringValue();
			mDay = maintJson.get(i).isString().stringValue();
			maintenanceDays[i] = mDay;
		}
//		edit.setMaintenanceDays(maintenanceDays);
		grid.setMaintenanceDays(maintenanceDays);
		
		change.loadSchedule(calendar);
		
		// use these to create the grid
		grid.loadSchedule(headers, numDays, rx.length + 1, calendar, diffCalendar);
		
		
	}

	// this converts part of the JSON we get back from the server and the list of all rcvrs (plus the date)
	// and converts it to a 2-D string representation of what we're supposed to display in the 
	// Receiver Schedule calendar.
	private String [][] getRcvrCalStrs(JSONObject schedule, String[] rcvrs, int numDays) {
		String rcvr;
		
		int numRcvrs = rcvrs.length;
		// the calendar is as wide as the number of rx, plus the date column
		String[][] calendar = new String[numDays][numRcvrs + 1];
		
		// the entries in this dictionary are date strings: we need to turn them into
		// Date objects so we can sort them, then use them as keys to get the
		// rcvr schedule in the correct order.
		TreeSet<Date> dates = new TreeSet<Date>(); //schedule.keySet());
		for (String dateStr : schedule.keySet()) {
		    Date dt = DATE_FORMAT.parse(dateStr);
		    dates.add(dt);
		}
		
		// now we have the dates in the right order, so we can populate the calendar
		int row = 0;
		for (Date date : dates) { 
			// the first column in each row is the date of the rcvr change
			String dtStr = DATE_FORMAT.format(date);
			calendar[row][0] = dtStr;
			// here we have the rcvr's available on this date
			JSONArray onRcvrs = schedule.get(dtStr).isArray();
			// put them in an array so we can test if a given rcvr is
			// available or not
			ArrayList<String> onRcvrList = new ArrayList<String>();
            for (int i = 0; i < onRcvrs.size(); i++) {
            	onRcvrList.add(onRcvrs.get(i).isString().stringValue());
            }
		    // go through the list of rcvrs
			for (int i = 0; i < numRcvrs; i++) {
				rcvr = rcvrs[i];
				// if this rcvr is in the list of the available rcvrs
				// indicate that somehow
				if (onRcvrList.contains(rcvr)) {
					calendar[row][i+1] = "T"; 
				} else {
					calendar[row][i+1] = "F"; 
				}
			}
			onRcvrList.clear();
			row += 1;
		}    	
		
		return calendar;
		
	}
}
