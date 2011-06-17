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

package edu.nrao.dss.client.util;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Set;

import com.google.gwt.json.client.JSONObject;

public class JSONRequestCache {

	private static HashMap<String, JSONObject> json = new HashMap<String, JSONObject> ();
	private static HashMap<String, ArrayList<JSONCallback>> callbacks = new HashMap<String, ArrayList<JSONCallback>>();
	private static Date timestamp = new Date();
	
	@SuppressWarnings("serial")
	public static void get(String uri, HashMap<String, Object> data, final JSONCallback cb) {
		Set <String> keys           = data.keySet();
    	ArrayList<String> strKeys   = new ArrayList<String>();
    	ArrayList<String> strValues = new ArrayList<String>();
    	for(Object k : keys) {
    		strKeys.add(k.toString());
    		strValues.add(data.get(k).toString());
    	}
		final String key = uri + "?" + JSONRequest.kv2url(strKeys.toArray(new String[]{})
				                                  , strValues.toArray(new String[]{}));
		JSONObject j = json.get(key);
		if (j == null | JSONRequestCache.expired()) {
			ArrayList<JSONCallback> cbs = callbacks.get(key);
			if (isEmpty(cbs)){
				JSONRequest.get(uri
					          , data
					          , new JSONCallbackAdapter() {
					        	  public void onSuccess(JSONObject json) {
					    		      JSONRequestCache.setJSON(key, json);
					    		      for(JSONCallback callback : callbacks.get(key)) {
					    			      callback.onSuccess(json);
					    		      }
					    		      callbacks.put(key, new ArrayList<JSONCallback> ());
					    	  }
					      });
				cbs = new ArrayList<JSONCallback> ();
				cbs.add(cb);
				callbacks.put(key, cbs);
			} else {
				cbs.add(cb);
			}
		} else {
			cb.onSuccess(json.get(key));
		}
	}
	
	private static boolean isEmpty(ArrayList<JSONCallback> cbs) {
		if (cbs == null) {
			return true;
		} else if (cbs.size() == 0) {
			return true;
		} else {
			return false;
		}
	}

	private static boolean expired() {
		Date now = new Date();
		long diff =  now.getTime() - timestamp.getTime();
		return diff > 10000 | diff < -10000 ;
	}

	private static void setJSON(String key, JSONObject new_json) {
		timestamp = new Date();
		json.put(key, new_json);
	}
}
