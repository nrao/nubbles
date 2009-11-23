package edu.nrao.dss.client;

import java.util.ArrayList;
import java.util.Date;
import java.util.Set;
import java.util.TreeSet;

import com.extjs.gxt.ui.client.Style.Orientation;
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
    
    private RcvrScheduleGrid grid = new RcvrScheduleGrid();
    private RcvrChangePanel change = new RcvrChangePanel();
    
	public ReceiverSchedule() {
		initLayout();
		
		// populate the table w/ the rcvr schedule
		getRcvrSchedule();

	}
	
	private void initLayout() {

		setLayout(new RowLayout(Orientation.VERTICAL));

		setBorders(false);
		setHeaderVisible(false);

        add(grid);
        add(change, new RowData(1, -1, new Margins(4)));
	}	

	private void getRcvrSchedule() {
		JSONRequest.get("/receivers/schedule"  
			      , new JSONCallbackAdapter() {
			public void onSuccess(JSONObject json) {
				GWT.log(json.toString(), null);
				jsonToRcvrSchedule(json);
			}
		});
	}

	private void jsonToRcvrSchedule(JSONObject json) {
		// construct the header for the rcvr schedule calendar
		JSONArray rcvrs = json.get("receivers").isArray();
		int numRcvrs = rcvrs.size();
		// headers start w/ the date, then the list of the rcvrs
		String[] headers = new String[numRcvrs + 1];
		headers[0] = "Date";
		String rcvr;
		for (int i = 0; i < numRcvrs; i++) {
			rcvr = rcvrs.get(i).isString().stringValue();
			headers[i+1] = rcvr;
		}
	
		// now, get the rest of the schedule
		JSONObject schedule = json.get("schedule").isObject();
		int numDays = schedule.keySet().size();
		String[][] calendar = getRcvrCalendar(schedule, headers, numDays);
		
		// use that to create the grid
		grid.loadSchedule(numDays, headers.length, headers, calendar);
		
		// get the diff schedule
		JSONArray diff = json.get("diff").isArray();
		String[][] diffCalendar = new String[diff.size()][4];
		String day, up, down, available, on;
		for (int i = 0; i < diff.size(); i++) {
			JSONObject diffDay = diff.get(i).isObject();
			day = diffDay.get("day").isString().stringValue();
			JSONArray ups = diffDay.get("up").isArray(); //.isString().stringValue();
			up = "";
			for (int j = 0; j<ups.size(); j++) {
				up += ups.get(j).isString().stringValue() + " ";
			}
			JSONArray downs = diffDay.get("down").isArray(); // .isString().stringValue();
			down = "";
			for (int j = 0; j<downs.size(); j++) {
				down += downs.get(j).isString().stringValue() + " ";
			}
			available = "";
			for (int j=0; j<calendar.length; j++) {
				if (calendar[j][0] == day) {
					for (int k=0; k<calendar[j].length; k++) {
						on = calendar[j][k];
						if (on.compareTo("T") == 0) {
							available += headers[k];
						}
						
					}
				}
			}
			diffCalendar[i][0] = day;
			diffCalendar[i][1] = up;
			diffCalendar[i][2] = down;
			diffCalendar[i][3] = available;
		}
		change.loadSchedule(diffCalendar);
		
	}

	// this converts part of the JSON we get back from the server and the list of all rcvrs (plus the date)
	// and converts it to a 2-D string representation of what we're supposed to display in the 
	// Receiver Schedule calendar.
	private String [][] getRcvrCalendar(JSONObject schedule, String[] headers, int numDays) {
		String rcvr;
		
		// headers is each rcvr + the Date (first column)
		int numRcvrs = headers.length - 1;
		String[][] calendar = new String[numDays][headers.length];
		
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
		    // go through the header's list of rcvrs
			for (int i = 0; i < numRcvrs; i++) {
				rcvr = headers[i+1];
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
