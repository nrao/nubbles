package edu.nrao.dss.client;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.extjs.gxt.ui.client.data.BaseListLoadResult;
import com.extjs.gxt.ui.client.data.BaseListLoader;
import com.extjs.gxt.ui.client.data.BaseModel;
import com.extjs.gxt.ui.client.data.BaseModelData;
import com.extjs.gxt.ui.client.data.JsonReader;
import com.extjs.gxt.ui.client.event.BaseEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.SelectionChangedEvent;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.ListView;
import com.extjs.gxt.ui.client.widget.ListViewSelectionModel;
import com.google.gwt.core.client.GWT;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONString;
import com.google.gwt.json.client.JSONValue;

public class NomineePanel extends ContentPanel {
	
	private Schedule schedule;
	private ListView<NomineeModel> nominees;
	private DynamicHttpProxy<BaseListLoadResult<NomineeType>> proxy;
	private BaseListLoader<BaseListLoadResult<NomineeModel>> loader;
	private ListStore<NomineeModel> store;
	private String rootUrl = "/nominees";
	
	public NomineePanel(Schedule sched) {
		schedule = sched;
		initLayout();
	}
	
	private void initLayout() {
		setHeading("East: Nominee Periods");
		
		RequestBuilder builder = new RequestBuilder(RequestBuilder.POST, rootUrl);

		JsonReader<BaseListLoadResult<NomineeType>> reader = new JsonReader<BaseListLoadResult<NomineeType>>(new NomineeType());
		proxy = new DynamicHttpProxy<BaseListLoadResult<NomineeType>>(builder);
		loader = new BaseListLoader<BaseListLoadResult<NomineeModel>>(proxy, reader);

		store = new ListStore<NomineeModel>(loader);
		
		nominees = new ListView<NomineeModel>();
		nominees.setStore(store);
		// TODO How to change the format of a field, i.e., duration and score?
		//nominees.setSimpleTemplate("<b>{sess_name}</b> ({proj_name}) {duration} minutes {TimeUtil.min2sex(duration)}");
		nominees.setSimpleTemplate("<b>{sess_name}</b> ({proj_name}) {score} for {durationStr}");
		add(nominees);
		nominees.getSelectionModel().addListener(Events.SelectionChange,  
		         new Listener<SelectionChangedEvent<BaseModel>>() {  
		           public void handleEvent(SelectionChangedEvent<BaseModel> be) {  
			         BaseModelData baseModelData = (BaseModelData) (be.getSelectedItem());
		             schedule.west.addRecord(nominee2Period(baseModelData));
		             schedule.updateCalendar();
		           }  
		         }); 
	}
	
	private HashMap<String, Object> nominee2Period(BaseModelData fields) {
		HashMap<String, Object> retval = new HashMap<String, Object>();
		retval.put("handle", fields.get("sess_name") + " (" + fields.get("proj_name") + ")");
		retval.put("date", DateTimeFormat.getFormat("yyyy-MM-dd").format(schedule.startVacancyDateTime));
		retval.put("time", DateTimeFormat.getFormat("HH:mm").format(schedule.startVacancyDateTime));
		retval.put("duration", (Double)fields.get("duration")/60.0);
		retval.put("score", (Double)fields.get("score"));
		retval.put("backup", false);
		return retval;
	}
	
	public void updateKeys(HashMap<String, Object> data) {
		Set <String> keys           = data.keySet();
    	ArrayList<String> strKeys   = new ArrayList<String>();
    	ArrayList<String> strValues = new ArrayList<String>();
    	for(Object k : keys) {
    		strKeys.add(k.toString());
    		strValues.add(data.get(k).toString());
    	}
    	StringBuilder urlData = new StringBuilder();
		urlData.append(rootUrl);
		urlData.append("?");
		urlData.append(JSONRequest.kv2url(strKeys.toArray(new String[]{}), strValues.toArray(new String[]{})));
		RequestBuilder builder = new RequestBuilder(RequestBuilder.GET, urlData.toString());
//		RequestBuilder builder = new RequestBuilder(RequestBuilder.POST, rootUrl);
//      builder.setRequestData(JSONRequest.kv2url(strKeys.toArray(new String[]{}), strValues.toArray(new String[]{})));
		proxy.setBuilder(builder);
	}
	
	public void loadData() {
		System.out.println("loadData");
		loader.load();
	}
}
