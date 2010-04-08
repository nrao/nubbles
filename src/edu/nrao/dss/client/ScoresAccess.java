package edu.nrao.dss.client;

import java.util.Date;
import java.util.HashMap;

import com.google.gwt.core.client.GWT;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.i18n.client.NumberFormat;
import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONObject;
import com.extjs.gxt.ui.client.widget.MessageBox;

public class ScoresAccess {
	private String banner;
	private String headers[];
	private String factors[][];
	MessageBox box;

	public void request(final ScoresDisplay display, Integer sessionId,
			final String label, final Date start, Integer minutes,
			final String timezone) {
		HashMap<String, Object> keys = formKeys(sessionId, label, start, minutes, timezone);
		JSONRequest.get("/factors", keys, new JSONCallbackAdapter() {
			public void onSuccess(JSONObject json) {
				float[] scores = extractScores(json);
				display.show(label, scores);
				box.close();
			}
		});
	}

	private HashMap<String, Object> formKeys(Integer index, String label,
			Date start, Integer minutes, String timezone) {
		HashMap<String, Object> keys = new HashMap<String, Object>();
		keys.put("id", index);
		keys.put("tz", timezone);
		String startStr = DateTimeFormat.getFormat("yyyy-MM-dd HH:mm:ss")
				.format(start);
		keys.put("start", startStr);
		keys.put("duration", minutes);
		String msg = "Generating calendar scores for " + label;
		box = MessageBox.wait("Getting scores", msg, "Be Patient ...");
		return keys;
	}
	
	private float[] extractScores(JSONObject json) {
		float scoreValue;
		
		JSONArray fs = json.get("factors").isArray();
		int numScores = fs.size();
		float[] scores = new float[numScores];
		
		for (int i = 0; i < numScores; ++i) {
			JSONArray factors = fs.get(i).isArray();
			int numFactors = factors.size();
			for (int j = 0; j < numFactors; j++) {
				JSONArray factor = factors.get(j).isArray();
				// for some reason, each factor is an array of [name , {"Just" : value}]
				String factorName = factor.get(0).isString().stringValue();
				if (factorName.compareTo("score") == 0) {
					JSONObject scoreObj = factor.get(1).isObject();
					if (scoreObj.containsKey("Just")) {
						scoreValue = (float) scoreObj.get("Just").isNumber().doubleValue();
					} else {
						scoreValue = -1.0f;
					}
		            scores[i] = scoreValue;			
				}
				
			}
			
		}
//		float scores[] = new float[24 * 4 * 3];
//		for (int i = 0; i < scores.length; i++) {
//			scores[i] = (float) i;
//		}	
		return scores;
	}

}
