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

import com.google.gwt.json.client.JSONObject;
import com.google.gwt.junit.client.GWTTestCase;

import edu.nrao.dss.client.widget.PeriodSummaryPanel;

public class TestPeriodSummaryPanel extends GWTTestCase {
	@Override
	public String getModuleName() {
		return "edu.nrao.dss.Nubbles";		
	}
    public void testLoadPeriodJson() {
    	// setup 
    	PeriodJSON pjson = PeriodJSON.getTestPeriodJSON_1();
    	JSONObject json = new JSONObject();
    	json.put("period", pjson);
    	PeriodJSON pjson2 = PeriodJSON.getTestPeriodJSON_2();
    	JSONObject json2 = new JSONObject();
    	json2.put("period", pjson2);
    	Period p = Period.parseJSON(pjson);    	
    	PeriodSummaryPanel panel = new PeriodSummaryPanel(p);
    	String[] originalWidgetState = panel.getTestString();

    	// test - loading the same input doesn't change anything
    	panel.loadPeriodJson(json);
    	String[] originalWidgetStateAgain = panel.getTestString();
    	for (int i=0; i<originalWidgetState.length; i++) {
    	    assertEquals(originalWidgetState[i], originalWidgetStateAgain[i]);
    	}
    	
    	// test - loading a new period should change the widgets
    	panel.loadPeriodJson(json2);
    	String[] newWidgetState = panel.getTestString();
    	assertEquals("GBT10B (GBT10B-001) 1", newWidgetState[0]);
    	assertEquals("2009-06-02 14:00:00", newWidgetState[1]);
    }
}
