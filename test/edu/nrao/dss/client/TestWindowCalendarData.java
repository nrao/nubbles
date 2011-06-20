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



import java.util.Date;

import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.junit.client.GWTTestCase;

import edu.nrao.dss.client.data.WindowCalendarData;

public class TestWindowCalendarData  extends GWTTestCase {
	
	@Override
	public String getModuleName() {
		return "edu.nrao.dss.Nubbles";		
	}

    public void testOne() {
    	DateTimeFormat DATE_FORMAT = DateTimeFormat.getFormat("yyyy-MM-dd");
    	
    	// get the test input
    	WindowJSON json = WindowJSON.getTestWindowJSON_1();
    	
    	// produce a calendar that is right in the middle of this window's first range: 
    	// 2010-01-01 - 2010-01-07
     	Date dt = DATE_FORMAT.parse("2010-01-02");
    	WindowCalendarData wcd = new WindowCalendarData(dt, 3, json);
    	
    	// test
    	Date[] dts = wcd.getDates();
    	assertEquals("2010-01-02", DATE_FORMAT.format(dts[0]));
    	assertEquals("2010-01-03", DATE_FORMAT.format(dts[1]));
    	assertEquals("2010-01-04", DATE_FORMAT.format(dts[2]));
    	// all days are 'on'
    	for (int day=0; day<5; day++) {
    		assertEquals(true, wcd.isDayNumberInWindow(day));
    	}
    	// check the display info
    	assertEquals("Starts: 2010-01-01", wcd.getDayNumberInfo(0));
    	for (int day=1; day<4; day++) {
    		assertEquals("", wcd.getDayNumberInfo(day));
    	}
    	String endStr = " Ends: 2010-01-07, 2010-02-01 - 2010-02-07, 2010-03-01 - 2010-03-07";
    	//assertEquals(endStr.substring(0, 50), wcd.getDayNumberInfo(4).substring(0, 50));
    	assertEquals(endStr, wcd.getDayNumberInfo(4));
    	
    }
    
    public void testTwo() {
    	DateTimeFormat DATE_FORMAT = DateTimeFormat.getFormat("yyyy-MM-dd");
    	
    	// get the test input
    	WindowJSON json = WindowJSON.getTestWindowJSON_1();
    	
    	// produce a calendar that overaps window's first range: 
    	// 2010-01-01 - 2010-01-07
     	Date dt = DATE_FORMAT.parse("2010-01-06");
    	WindowCalendarData wcd = new WindowCalendarData(dt, 5, json);
    	
    	// test
    	Date[] dts = wcd.getDates();
    	assertEquals("2010-01-06", DATE_FORMAT.format(dts[0]));
    	assertEquals("2010-01-07", DATE_FORMAT.format(dts[1]));
    	assertEquals("2010-01-08", DATE_FORMAT.format(dts[2]));
    	assertEquals("2010-01-09", DATE_FORMAT.format(dts[3]));
    	assertEquals("2010-01-10", DATE_FORMAT.format(dts[4]));
    	
    	// not all days are 'on' - here it's [t,t,t,f,f,f,t]
    	boolean ons[] = wcd.getDisplayFlags();
    	for (int day=0; day<3; day++) {
    		assertEquals(true, wcd.isDayNumberInWindow(day));
    	}
    	for (int day=3; day<6; day++) {
    		assertEquals(false, wcd.isDayNumberInWindow(day));
    	}
		assertEquals(true, wcd.isDayNumberInWindow(6));
    	
    	// check the display info
    	assertEquals("Starts: 2010-01-01", wcd.getDayNumberInfo(0));
    	for (int day=1; day<6; day++) {
    		assertEquals("", wcd.getDayNumberInfo(day));
    	}
    	String endStr = " 2010-02-01 - 2010-02-07, 2010-03-01 - 2010-03-07";
    	//assertEquals(endStr.substring(0, 50), wcd.getDayNumberInfo(4).substring(0, 50));
    	assertEquals(endStr, wcd.getDayNumberInfo(6));
    	
    }   
    
    public void testThree() {
    	DateTimeFormat DATE_FORMAT = DateTimeFormat.getFormat("yyyy-MM-dd");
    	
    	// get the test input
    	WindowJSON json = WindowJSON.getTestWindowJSON_1();
    	
    	// produce a calendar that doesn't overlap any of these ranges: 
    	// 2010-01-01 - 2010-01-07, 2010-02-01 - 2010-02-07, 2010-03-01 - 2010-03-07,
     	Date dt = DATE_FORMAT.parse("2010-01-20");
    	WindowCalendarData wcd = new WindowCalendarData(dt, 2, json);
    	
    	// test
    	Date[] dts = wcd.getDates();
    	assertEquals("2010-01-20", DATE_FORMAT.format(dts[0]));
    	assertEquals("2010-01-21", DATE_FORMAT.format(dts[1]));
    	// not all days are 'on' - here it's [t,f,f,t]
    	//boolean ons[] = wcd.getDisplayFlags();
		assertEquals(true,  wcd.isDayNumberInWindow(0));
		assertEquals(false, wcd.isDayNumberInWindow(1));
		assertEquals(false, wcd.isDayNumberInWindow(2));
		assertEquals(true,  wcd.isDayNumberInWindow(3));
    	// check the display info
    	assertEquals("2010-01-01 - 2010-01-07", wcd.getDayNumberInfo(0));
		assertEquals("", wcd.getDayNumberInfo(1));
		assertEquals("", wcd.getDayNumberInfo(2));
    	assertEquals(" 2010-02-01 - 2010-02-07, 2010-03-01 - 2010-03-07", wcd.getDayNumberInfo(3));
    }   
    
    public void testFour() {
    	DateTimeFormat DATE_FORMAT = DateTimeFormat.getFormat("yyyy-MM-dd");
    	
    	// get the test input
    	WindowJSON json = WindowJSON.getTestWindowJSON_2();
    	
    	// produce a calendar that overlap any of these ranges: 
    	// 2009-06-01 - 2009-06-07
     	Date dt = DATE_FORMAT.parse("2009-06-04");
    	WindowCalendarData wcd = new WindowCalendarData(dt, 6, json);
    	
    	// test
    	Date[] dts = wcd.getDates();
    	assertEquals("2009-06-04", DATE_FORMAT.format(dts[0]));
    	assertEquals("2009-06-05", DATE_FORMAT.format(dts[1]));
    	assertEquals("2009-06-06", DATE_FORMAT.format(dts[2]));
    	assertEquals("2009-06-07", DATE_FORMAT.format(dts[3]));
    	assertEquals("2009-06-08", DATE_FORMAT.format(dts[4]));
    	assertEquals("2009-06-09", DATE_FORMAT.format(dts[5]));
    	
    	// not all days are 'on' - here it's [t,t,t,t,t,f,f,f]
    	boolean ons[] = {true, true, true, true, true, false, false, false}; //wcd.getDisplayFlags();
    	//assertEquals(ons, wcd.getDisplayFlags());
		assertEquals(true, wcd.isDayNumberInWindow(0));
		assertEquals(true, wcd.isDayNumberInWindow(1));
		assertEquals(true, wcd.isDayNumberInWindow(2));
		assertEquals(true, wcd.isDayNumberInWindow(3));
		assertEquals(true, wcd.isDayNumberInWindow(4));
		assertEquals(false, wcd.isDayNumberInWindow(5));
		assertEquals(false, wcd.isDayNumberInWindow(6));
		assertEquals(false, wcd.isDayNumberInWindow(7));
		
    	// check the display info
		String startStr = "Starts: 2009-06-01, Default (P) (0.0) on 2009-06-01";
    	assertEquals(startStr, wcd.getDayNumberInfo(0));
    	for (int i=1; i<8; i++) {
		    assertEquals("", wcd.getDayNumberInfo(i));
    	}
    	
		String handle = "Low Frequency With No RFI (GBT09A-001) 0";		
		String expLabel = handle + " (0.0/0.0) Not Cmp.";
		assertEquals(expLabel, wcd.getLabel());
    }  
    
    public void testFive() {
    	DateTimeFormat DATE_FORMAT = DateTimeFormat.getFormat("yyyy-MM-dd");
    	
    	// get the test input
    	WindowJSON json = WindowJSON.getTestWindowJSON_2();
    	
    	// produce a calendar that ends on the last day 
    	// Window: 2009-06-01 - 2009-06-07
    	// Header: Window | Start  | 6/4 | 6/5 | 6/6 | End |
    	// Row:    label  | "St.." |  on | on  | on  | on  |
     	Date dt = DATE_FORMAT.parse("2009-06-04");
    	WindowCalendarData wcd = new WindowCalendarData(dt, 3, json);
    	
    	// test
    	Date[] dts = wcd.getDates();
    	assertEquals("2009-06-04", DATE_FORMAT.format(dts[0]));
    	assertEquals("2009-06-05", DATE_FORMAT.format(dts[1]));
    	assertEquals("2009-06-06", DATE_FORMAT.format(dts[2]));
    	
    	// not all days are 'on' - here it's [t,t,t,t,t,f,f,f]
    	boolean ons[] = {true, true, true, true, true}; //wcd.getDisplayFlags();
    	//assertEquals(ons, wcd.getDisplayFlags());
    	for (int i=0; i<ons.length-1; i++) {
		    assertEquals(ons[i], wcd.isDayNumberInWindow(i));
    	}
		
    	// check the display info
		String startStr = "Starts: 2009-06-01, Default (P) (0.0) on 2009-06-01";
    	assertEquals(startStr, wcd.getDayNumberInfo(0));
    	for (int i=1; i<5; i++) {
		    assertEquals("", wcd.getDayNumberInfo(i));
    	}
    	
		String handle = "Low Frequency With No RFI (GBT09A-001) 0";		
		String expLabel = handle + " (0.0/0.0) Not Cmp.";
		assertEquals(expLabel, wcd.getLabel());
    }     
}
