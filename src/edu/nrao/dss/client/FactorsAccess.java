package edu.nrao.dss.client;

import java.util.Date;
import java.util.HashMap;

import com.google.gwt.core.client.GWT;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.i18n.client.NumberFormat;
import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONObject;
import com.extjs.gxt.ui.client.widget.MessageBox;

public class FactorsAccess {
	private String headers[];
	private String factors[][];
	MessageBox box;
	
	public void request(final FactorsDisplay display, Integer sessionId, final String label, final Date start, Integer minutes, final String timezone) {
		JSONRequest.get("/factors", formKeys(sessionId, label, start, minutes, timezone),
		new JSONCallbackAdapter() {
			public void onSuccess(JSONObject json) {
				populateHeadersFactors(json, start, timezone);
				display.show(label, headers, factors);
				box.close();
			}
		});
	}
	
	private HashMap<String, Object> formKeys(Integer index, String label, Date start, Integer minutes, String timezone) {
		HashMap<String, Object> keys = new HashMap<String, Object>();
		keys.put("id", index);
		keys.put("tz", timezone);
		String startStr = DateTimeFormat.getFormat("yyyy-MM-dd HH:mm:ss").format(start);
		keys.put("start", startStr);
		keys.put("duration", minutes);
		String msg = "Generating scheduling factors for " + label;
		box = MessageBox.wait("Getting factors", msg, "Be Patient ...");
		return keys;
	}
	
	private void populateHeadersFactors(JSONObject json, Date start, String tz) {
		JSONArray fs = json.get("factors").isArray();
		JSONArray fs0 = fs.get(0).isArray();
		int length = fs0.size();
		// Grid size including times, but not headers
		int rows = fs.size();
		int cols = length + 1;
		// Extract column header names
		headers = new String[cols];
		headers[0] = "Time " + tz;
		for (int i = 0; i < length; ++i) {
			String str = fs0.get(i).isArray().get(0).toString();
			headers[i+1] = str.substring(1, str.indexOf('"', 1));
		}
		// Extract factor values
		factors = new String[rows][cols];
		long msecs = start.getTime();
		Date quarter = new Date();
		DateTimeFormat dtf = DateTimeFormat.getFormat("yyyy-MM-dd HH:mm:ss");
		for (int t = 0; t < rows; ++t) {
			JSONArray row = fs.get(t).isArray();
			quarter.setTime(msecs);
			msecs += 15*60*1000;
			factors[t][0] = dtf.format(quarter);
			for (int f = 0; f < length; ++f) {
				JSONObject obj = row.get(f).isArray().get(1).isObject();
				String repr;
				if (obj.containsKey("Nothing")) {
					repr = "?";
				} else {
					double value = obj.get("Just").isNumber().doubleValue();
					repr = NumberFormat.getFormat("#0.00").format(value);
				}
				factors[t][f+1] = repr;
			}
		}
	}

}
