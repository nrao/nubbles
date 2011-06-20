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

import java.util.ArrayList;
import java.util.List;

import com.extjs.gxt.ui.client.data.BaseModelData;
import com.google.gwt.junit.client.GWTTestCase;

public class TestScheduleControl extends GWTTestCase {
	@Override
	public String getModuleName() {
		return "edu.nrao.dss.Nubbles";		
	}

	public void testAnalyzeSchedule() {
		
		// setup - a single period
		BaseModelData datum = new BaseModelData();
		datum.set("date", "2011-10-10");
		datum.set("time", "00:00");
		datum.set("duration", 2.0);
		datum.set("cscore", 0.0);
		ArrayList<BaseModelData> data = new ArrayList<BaseModelData>();
		data.add(datum);
		
		// see how a single period is analyzed 
		Schedule sch = new Schedule();
		ScheduleControl sc = new ScheduleControl(sch);
		double[] results = sc.analyzeSchedule(data);
		assertEquals(0.0, results[0]);
		assertEquals(0.0, results[1]);
		
		// now add another period
		BaseModelData datum2 = new BaseModelData();
		datum2.set("date", "2011-10-10"); // one hour gap
		datum2.set("time", "03:00");
		datum2.set("duration", 2.0);
		datum2.set("cscore", 2.0); // change the average score
		data.add(datum2);

		// see these periods are analyzed 
		results = sc.analyzeSchedule(data);
		assertEquals(1.0, results[0]);
		assertEquals(60.0, results[1]); // one hour gap
		
	}
}
