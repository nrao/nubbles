package edu.nrao.dss.client;

import java.util.HashMap;

import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;

public class FactorGrid extends Grid {

	private final static HashMap<String, String> abbr = new HashMap<String, String>();
	static
	{
		abbr.put("scienceGrade", "grade");
		abbr.put("thesisProject", "thesis");
		abbr.put("projectCompletion", "compl");
		abbr.put("stringency", "stringcy");
		abbr.put("atmosphericOpacity", "atmOpac");
		abbr.put("surfaceObservingEfficiency", "srfObsEff");
		abbr.put("trackingEfficiency", "trkEff");
		abbr.put("rightAscensionPressure", "RApres");
		abbr.put("frequencyPressure", "freqPres");
		abbr.put("observingEfficiencyLimit", "obsEffLim");
		abbr.put("hourAngleLimit", "HAlim");
		abbr.put("zenithAngleLimit", "ZAlim");
		abbr.put("trackingErrorLimit", "trkEffLim");
		abbr.put("atmosphericStabilityLimit", "atmStaLim");
		abbr.put("receiver", "rcvr");
		abbr.put("observerOnSite", "obsSite");
		abbr.put("needsLowRFI", "lowRFI");
		abbr.put("lstExcepted", "lstExcp");
		abbr.put("enoughTimeBetween", "timeBet");
		abbr.put("observerAvailable", "obsAval");
	}
	
	private void setHeader(int col, String name) {
		String entry;
		if (abbr.containsKey(name)) {
			entry = abbr.get(name);
		} else {
			entry = name;
		}
		setText(0, col, entry);
	}
	 
	public FactorGrid(int rows, int cols, String[] headers, String[][] factors) {
		super(rows+1, cols);
		setBorderWidth(2);
		setCellPadding(1);
		setCellSpacing(1);
		HashMap<String, Integer> colMap = new HashMap<String, Integer>();
        for (int col = 0; col < cols; col++) {
        	setHeader(col, headers[col]);
            colMap.put(headers[col], col);
            getCellFormatter().setHorizontalAlignment(0, col, HasHorizontalAlignment.ALIGN_CENTER);
        }
	    for (int row = 0; row < rows; ++row){
	        for (int fac = 0; fac < cols; fac++) {
	        	int col = colMap.get(headers[fac]);
	            setText(row+1, col, factors[row][col]);
	            getCellFormatter().setHorizontalAlignment(row+1, col, HasHorizontalAlignment.ALIGN_CENTER);
	            getCellFormatter().setWordWrap(row+1, col, false);
	        }
	    }
	}
}
