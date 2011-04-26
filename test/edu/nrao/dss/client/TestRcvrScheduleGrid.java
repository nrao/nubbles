package edu.nrao.dss.client;

import com.google.gwt.junit.client.GWTTestCase;

import edu.nrao.dss.client.data.RcvrScheduleData;
import edu.nrao.dss.client.widget.RcvrScheduleGrid;


public class TestRcvrScheduleGrid extends GWTTestCase {

	@Override
	public String getModuleName() {
		return "edu.nrao.dss.Nubbles";
	}
	
	public void testJsonToRcvrSchedule() {
		
		// create a new grid, and pass it some data to render
		RcvrScheduleGrid rs = new RcvrScheduleGrid();
        RcvrScheduleJSON json = new RcvrScheduleJSON();		
        RcvrScheduleData rsd = RcvrScheduleData.parseJSON(json);
		rs.loadRcvrScheduleData(rsd);
		
		// check the results
		assertEquals(rsd.getDays().length + 1, rs.getRowCount());
		for (int i=0; i<rs.getRowCount(); i++) {
			assertEquals(rsd.getReceiverNames().length + 3, rs.getCellCount(i));
			if (i != 0) {
			    assertEquals(rsd.getDays()[i-1].getDateStr(), rs.getHTML(i, 0));
			    assertEquals(rsd.getDays()[i-1].getUpStr(),   rs.getHTML(i, 1));
			    assertEquals(rsd.getDays()[i-1].getDownStr(), rs.getHTML(i, 2));
			}
		}
	}
	
	public void testJsonToRcvrScheduleWithMaintenance() {
		
		// create a new grid, and pass it some data to render
		RcvrScheduleGrid rs = new RcvrScheduleGrid();
		// show the maintanence day!
		rs.setShowMaintenance(true);
        RcvrScheduleJSON json = new RcvrScheduleJSON();		
        RcvrScheduleData rsd = RcvrScheduleData.parseJSON(json);
		rs.loadRcvrScheduleData(rsd);
		
		// check the results - take into account the maintenance day
		assertEquals(rsd.getDays().length + 2, rs.getRowCount());
		// second row is the first rx change day
		assertEquals(rsd.getReceiverNames().length + 3, rs.getCellCount(1));
	    assertEquals(rsd.getDays()[0].getDateStr(), rs.getHTML(1, 0));
	    assertEquals(rsd.getDays()[0].getUpStr(),   rs.getHTML(1, 1));
	    assertEquals(rsd.getDays()[0].getDownStr(), rs.getHTML(1, 2));
		// next row is just the maintenance day
		assertEquals(1, rs.getCellCount(2));
	    assertEquals("04/07/2009", rs.getHTML(2, 0));	    
	    // remaining rows are the remaining rx change days
		for (int i=3; i<rs.getRowCount(); i++) {
			assertEquals(rsd.getReceiverNames().length + 3, rs.getCellCount(i));
		    assertEquals(rsd.getDays()[i-2].getDateStr(), rs.getHTML(i, 0));
		    assertEquals(rsd.getDays()[i-2].getUpStr(),   rs.getHTML(i, 1));
		    assertEquals(rsd.getDays()[i-2].getDownStr(), rs.getHTML(i, 2));
		}
		assertEquals(true, rs.isMaintenanceDay("04/07/2009"));
		assertEquals(false, rs.isMaintenanceDay("04/08/2009"));
		
	}

}
