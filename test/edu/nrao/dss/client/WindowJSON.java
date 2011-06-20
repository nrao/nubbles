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

import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONBoolean;
import com.google.gwt.json.client.JSONNumber;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONString;

//{"total_time": 0.0
//, "errors": ["Default Period mandatory for non-guaranteed Sessions."]
//, "end": "2010-03-07"
//, "time_billed": 0
//, "time_remaining": 0.0
//, "contigious": false
//, "ranges": [{"duration": 7, "start": "2010-01-01", "end": "2010-01-07", "id": 1}
//	           , {"duration": 7, "start": "2010-02-01", "end": "2010-02-07", "id": 3}
//	           , {"duration": 7, "start": "2010-03-01", "end": "2010-03-07", "id": 2}]
//, "start": "2010-01-01"
//, "num_periods": 0
//, "periods": []
//, "duration": 66
//, "handle": "Low Frequency With No RFI (GBT09A-001) 0"
//, "id": 1
//, "complete": false}

public class WindowJSON extends TestJSON {
	
	public WindowJSON(int id
			        , String handle
			        , Double total_time
			        , Double time_billed
			        , Double time_remaining
			        , boolean complete
			        , boolean contigious
			        , String start
			        , String end
			        , int duration
			        , int num_periods
			        , String[] errors
			        , String[][] ranges
			        , PeriodJSON[] periods) {
	    add("id", id);
	    add("handle", handle);
	    add("total_time", total_time);
	    add("time_billed", time_billed);
	    add("time_remaining", time_remaining);
	    add("complete", complete);
	    add("contigious", contigious);
	    add("start", start);
	    add("end", end);
	    add("duration", duration);
	    add("num_periods", num_periods);
	    add("errors", createErrorsJSON(errors));
	    add("ranges", createRangesJSON(ranges));
	    add("periods", createPeriodsJSON(periods));
	}
	
	private JSONArray createPeriodsJSON(PeriodJSON[] periods) {
		JSONArray array = new JSONArray();
		for (int i=0; i<periods.length; i++) {
			array.set(i, periods[i]);
		}
		return array;
		
	}
	private JSONArray createErrorsJSON(String[] errors) {
		JSONArray array = new JSONArray();
		for (int i=0; i<errors.length; i++) {
			JSONString error = new JSONString(errors[i]);
			array.set(i, error);
		}
		return array;
	}
	
	private JSONArray createRangesJSON(String[][] ranges) {
		JSONArray array = new JSONArray();
		for (int i=0; i<ranges.length; i++) {
			JSONObject range = createRangeJSON(ranges[i][0], ranges[i][1], Integer.parseInt(ranges[i][2]));
			array.set(i, range);
		}
		return array;
	}
	
	private JSONObject createRangeJSON(String start, String end, int dur) {
	    JSONObject range = new JSONObject();
	    range.put("start", new JSONString(start));
	    range.put("end",   new JSONString(end));
	    range.put("duration", new JSONNumber(dur));
	    return range;
	}
	
	// a window w/ no periods yet assigned to it.
	static WindowJSON getTestWindowJSON_1() {
    	WindowJSON wjson;
		String[] errs = new String[] {"Default Period mandatory for non-guaranteed Sessions."};
		String[][] rgs = new String[][] {{"2010-01-01","2010-01-07","7"}
		                                ,{"2010-02-01","2010-02-07","7"}
		                                ,{"2010-03-01","2010-03-07","7"}
		                                };
		String handle = "Low Frequency With No RFI (GBT09A-001) 0";
		PeriodJSON[] ps = new PeriodJSON[] {};
		wjson = new WindowJSON(1, handle, 0.0, 0.0, 0.0, false, false, "2010-01-01", "2010-03-07", 66, 0, errs, rgs, ps);	
    	return wjson;
	}	
	
	// a simple window with one default period
	static WindowJSON getTestWindowJSON_2() {
    	WindowJSON wjson;
		String[] errs = new String[] {"Window is overlapping with window ID(s): 1"};
		String[][] rgs = new String[][] {{"2009-06-01","2009-06-07","7"}
		                                };
		String handle = "Low Frequency With No RFI (GBT09A-001) 0";
		PeriodJSON[] ps = new PeriodJSON[] {PeriodJSON.getTestPeriodJSON_1()};
		wjson = new WindowJSON(2, handle, 0.0, 0.0, 0.0, false, true, "2009-06-01", "2009-06-07", 7, 0, errs, rgs, ps);	
    	return wjson;
		
	}	
}
