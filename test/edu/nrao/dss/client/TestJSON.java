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
