package edu.nrao.dss.client;

import com.extjs.gxt.ui.client.widget.Window;
import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.user.client.ui.Grid;

public class FactorsWindow extends Window {
	
	private String headers[];
	private String factors[][];
	private int rows, cols;
	private Grid grid;

	public FactorsWindow() {
		initLayout();
		hide();
	}
	
	private void initLayout() {
		setHeading("Factors");
		setModal(false);
		setSize(800, 400);
		setMaximizable(true);
		setToolTip("Individual factors whose product determines a sessions score at a specific time.");
		grid = new Grid(1,1);
		add(grid);
	}
	
	private void parseJSON(JSONObject json) {
		JSONArray fs = json.get("factors").isArray();
		JSONArray fs0 = fs.get(0).isArray();
		int length = fs0.size();
		// Grid size including times, but not headers
		rows = fs.size();
		cols = length + 1;
		// Extract column header names
		headers = new String[cols];
		headers[0] = "Time";
		for (int i = 0; i < length; ++i) {
			String str = fs0.get(i).isArray().get(0).toString();
			headers[i+1] = str.substring(1, str.indexOf('"', 1));
		}
		// Extract factor values
		factors = new String[rows][cols];
		for (int t = 0; t < rows; ++t) {
			JSONArray row = fs.get(t).isArray();
			factors[t][0] = "time string";
			for (int f = 0; f < length; ++f) {
				JSONObject obj = row.get(f).isArray().get(1).isObject();
				String value;
				if (obj.containsKey("Nothing")) {
					value = "";
				} else {
					String temp = obj.get("Just").isNumber().toString();
					int max = 5;
					if (temp.length() > max) {
						value = temp.substring(0, max);
					} else {
					    value = temp;
					}
				}
				// TODO use key to set position!
				factors[t][f+1] = value;
			}
		}
	}
	
	public void update(JSONObject json) {
		parseJSON(json);
		remove(grid);
		// Grid size includes times and headers
		grid = new Grid(rows+1, cols);
		grid.setBorderWidth(2);
		grid.setCellPadding(5);
		grid.setCellSpacing(1);
        for (int col = 0; col < cols; col++) {
            grid.setText(0, col, headers[col]);
        }
	    for (int row = 0; row < rows; row++) {
	        for (int col = 0; col < cols; col++) {
	            grid.setText(row+1, col, factors[row][col]);
	        }
	    }
	    add(grid);
        show();
	}
}
