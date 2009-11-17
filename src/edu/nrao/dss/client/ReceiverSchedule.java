package edu.nrao.dss.client;

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
//			      , new HashMap<String, Object>() {{
//			    	  put("mode", "project_codes");
//			        }}
			      , new JSONCallbackAdapter() {
			public void onSuccess(JSONObject json) {
				// translate the json to string arrays
				GWT.log(json.toString(), null);
				// construct the header
				JSONArray rcvrs = json.get("receivers").isArray();
				int numRcvrs = rcvrs.size();
				// headers start w/ the date, then the list of the rcvrs
				String[] headers = new String[numRcvrs + 1];
				headers[0] = "Date";
				for (int i = 0; i < numRcvrs; i++) {
					headers[i+1] = rcvrs.get(i).isString().stringValue();
				}
				// get the rest of the schedule
				JSONObject schedule = json.get("schedule").isObject();
				int numDays = schedule.keySet().size();
				String[][] calendar = new String[numDays][numRcvrs + 1];
				
				int row = 0;
				String value = "T";
				for (String date : schedule.keySet()) {
					calendar[row][0] = date;
					JSONArray onRcvrs = schedule.get(date).isArray();

				    // go through the header's list of rcvrs
					for (int i = 0; i < numRcvrs; i++) {
						String rcvr = headers[i+1];
						// TODO: if this receiver is in the current date's list of rcvrs, set it to 'T'
						calendar[row][i+1] = rcvr; //value;
					}
					
					row += 1;
				}
				// use that to create the grid
				initRcvrScheduleGrid(numDays+1, headers.length, headers, calendar);
				}
			//}
		});
	}	
}
