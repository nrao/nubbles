package edu.nrao.dss.client;

import java.util.ArrayList;
import java.util.Set;
import java.util.TreeSet;

import com.extjs.gxt.ui.client.Style.Orientation;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.layout.RowLayout;
import com.google.gwt.core.client.GWT;
import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONObject;

public class ReceiverSchedule extends ContentPanel {
	public ReceiverSchedule() {
		initLayout();
	}
	
	private void initLayout() {

		setLayout(new RowLayout(Orientation.VERTICAL));
		

		setBorders(false);
		setHeaderVisible(false);
		
		//initRcvrScheduleGrid();
		getRcvrSchedule();
	}	
	
	private void initRcvrScheduleGrid(int rows, int cols, String[] header, String[][] schedule) {
		
		// TODO: get the rcvr schedule
		// for now, fake it
//		int rows = 3;
//		int cols = 3;
//		String[] header = {"Date", "L", "S"};
//		String[][] schedule = {{"2009-10-1","T","F"}
//		                     , {"2009-10-2","F","T"}
//		                     , {"2009-10-3","T","F"}};
                
		
		RcvrScheduleGrid grid = new RcvrScheduleGrid(rows, cols, header, schedule); 
		add(grid);
		show();
		
	}
	
	private void getRcvrSchedule() {
		JSONRequest.get("/receivers/schedule"  //, null

			      , new JSONCallbackAdapter() {
			public void onSuccess(JSONObject json) {
				// translate the json to string arrays
				GWT.log(json.toString(), null);
				// construct the header
				JSONArray rcvrs = json.get("receivers").isArray();
				int numRcvrs = rcvrs.size();
				// headers start w/ the date, then the list of the rcvrs
				String[] headers = new String[numRcvrs + 1];
				//ArrayList<String> rcvrList = new ArrayList<String>();
				headers[0] = "Date";
				String rcvr;
				for (int i = 0; i < numRcvrs; i++) {
					rcvr = rcvrs.get(i).isString().stringValue();
					headers[i+1] = rcvr;
					//rcvrList.add(rcvr);
				}
				// get the rest of the schedule
				JSONObject schedule = json.get("schedule").isObject();
				int numDays = schedule.keySet().size();
				String[][] calendar = new String[numDays][numRcvrs + 1];
				
				int row = 0;
				String value = "T";
				// TODO: this doesn't sort the string list of dates properly!
				TreeSet<String> orderedDays = new TreeSet<String>(schedule.keySet());
				for (String date : orderedDays) { //schedule.keySet()) {
					calendar[row][0] = date;
					JSONArray onRcvrs = schedule.get(date).isArray();
					ArrayList<String> onRcvrList = new ArrayList<String>();
                    for (int i = 0; i < onRcvrs.size(); i++) {
                    	onRcvrList.add(onRcvrs.get(i).isString().stringValue());
                    }
				    // go through the header's list of rcvrs
					for (int i = 0; i < numRcvrs; i++) {
						rcvr = headers[i+1];
						if (onRcvrList.contains(rcvr)) {
							calendar[row][i+1] = "T"; //value;
						} else {
							calendar[row][i+1] = "F"; //value;
						}
					}
					onRcvrList.clear();
					
					row += 1;
				}
				// use that to create the grid
				initRcvrScheduleGrid(numDays+1, headers.length, headers, calendar);
				}
		});
	}	
}
