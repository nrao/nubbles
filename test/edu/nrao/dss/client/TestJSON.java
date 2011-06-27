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

public class TestJSON extends JSONObject {
	public void add(String key, JSONArray values) {
		this.put(key, values);
	}
	public void add(String key, JSONObject value) {
		this.put(key, value);
	}
	
    public void add(String key, String value) {
    	JSONString v = new JSONString(value);
    	this.put(key, v);
    	
    }
  
    public void add(String key, int value) {
    	JSONNumber v = new JSONNumber(value);
    	this.put(key, v);
    }
    public void add(String key, Double value) {
    	JSONNumber v = new JSONNumber(value);
    	this.put(key, v);
    }    
    public void add(String key, boolean value) {
    	JSONBoolean v = JSONBoolean.getInstance(value);
    	this.put(key, v);
    }
}
