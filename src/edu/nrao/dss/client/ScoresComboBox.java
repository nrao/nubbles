package edu.nrao.dss.client;

import java.util.Date;
import java.util.HashMap;
import java.util.Set;

import com.extjs.gxt.ui.client.event.BaseEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.widget.form.SimpleComboBox;
import com.google.gwt.core.client.GWT;
import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONObject;

public class ScoresComboBox extends SimpleComboBox implements ScoresControl {
	
	private ScoresDisplay display;
	private ScoresAccess access;
    private Schedule schedulePanel;
	private final HashMap<String, Integer> sessionsMap = new HashMap<String, Integer>();
    
    // TODO, WTF: used for seeing if this is a new request, to avoid bug where
    // we are getting event twice.
    private Date lastStart;
    private int lastNumDays;
    private String lastTimeZone;
    private int lastSessionId;
    
	public ScoresComboBox(Schedule schedulePanel) {
		super();
		this.schedulePanel = schedulePanel;
		initLayout();
		lastStart = new Date();
		lastNumDays = 0;
		lastTimeZone = "";
		lastSessionId = 0;
	}
	
	private void initLayout() {
		
	    // get the options
		setForceSelection(true);
		JSONRequest.get("/sessions/options"
			      , new HashMap<String, Object>() {{
			    	  put("mode", "session_handles");
			        }}
			      , new JSONCallbackAdapter() {
			public void onSuccess(JSONObject json) {
				JSONArray results = json.get("session handles").isArray();
				JSONArray ids = json.get("ids").isArray();
				for (int i = 0; i< ids.size(); i += 1) {
					String key = results.get(i).toString().replace('"', ' ').trim();
					sessionsMap.put(key, (int)(ids.get(i).isNumber().doubleValue()));
					add(key);
				}
			}
    	});		
		addListener(Events.Valid, new Listener<BaseEvent>() {
		    public void handleEvent(BaseEvent be) {
		        String session = (String) getSimpleValue();
		    	getSessionScores(session);
			}
		});
		
	}

	public void getSessionScores(String session) {
	    int id = sessionsMap.get(session);
	    getScores(id, session);
	}
	
	private void getScores(int id, String name) {
		Date start = schedulePanel.getStartCalendarDay();
		int numDays = schedulePanel.getNumCalendarDays();
		String tz = schedulePanel.getTimeZone();
		// TODO, WTF: for some reason we get the Valid event twice, so store the request parameters,
		// and only actually send them if they have changed since the last
		if ((start != lastStart) | (numDays != lastNumDays) | (tz != lastTimeZone) | (id != lastSessionId)) {
			// save off these latest values
			lastStart = start;
			lastNumDays = numDays;
			lastTimeZone = tz;
			lastSessionId = id;
			// go get the scores
		    access.request(display, id, name, start, (numDays * 24 * 60), tz);
		}
	}
	
	@Override
	public void setAccess(ScoresAccess access) {
        this.access = access;		
	}

	@Override
	public void setDisplay(ScoresDisplay display) {
        this.display = display;		
	}

}
