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

import com.google.gwt.junit.client.GWTTestCase;

import edu.nrao.dss.client.util.dssgwtcal.util.TimeUtils;

public class TestDssGwtCalTimeUtils extends GWTTestCase {
	@Override
	public String getModuleName() {
		return "edu.nrao.dss.Nubbles";		
	}
    public void testIsDSTBoundary() {
    	Date dt;
    	TimeUtils tu = new TimeUtils();

    	dt = TimeUtils.DATETIME_FORMAT.parse("2009-10-31 00:00");
    	assertEquals(false, tu.isDSTBoundary(dt));
    	dt = TimeUtils.DATETIME_FORMAT.parse("2009-11-01 00:00");
    	assertEquals(true, tu.isDSTBoundary(dt));
    	dt = TimeUtils.DATETIME_FORMAT.parse("2009-11-02 00:00");
    	assertEquals(false, tu.isDSTBoundary(dt));
    	
    	dt = TimeUtils.DATETIME_FORMAT.parse("2014-03-08 00:00");
    	assertEquals(false, tu.isDSTBoundary(dt));
    	dt = TimeUtils.DATETIME_FORMAT.parse("2014-03-09 00:00");
    	assertEquals(true, tu.isDSTBoundary(dt));
    	dt = TimeUtils.DATETIME_FORMAT.parse("2014-03-10 00:00");
    	assertEquals(false, tu.isDSTBoundary(dt));    	
    }

    public void testIsInDSTFiveHourOffset() {
    	Date dt;
    	TimeUtils tu = new TimeUtils();

    	dt = TimeUtils.DATETIME_FORMAT.parse("2009-10-31 00:00");
    	assertEquals(false, tu.isInDSTFiveHourOffset(dt));
    	dt = TimeUtils.DATETIME_FORMAT.parse("2009-11-01 00:00");
    	assertEquals(true, tu.isInDSTFiveHourOffset(dt));
    	dt = TimeUtils.DATETIME_FORMAT.parse("2009-11-02 00:00");
    	assertEquals(true, tu.isInDSTFiveHourOffset(dt));
    	
    	dt = TimeUtils.DATETIME_FORMAT.parse("2014-03-08 00:00");
    	assertEquals(true, tu.isInDSTFiveHourOffset(dt));
    	dt = TimeUtils.DATETIME_FORMAT.parse("2014-03-09 00:00");
    	assertEquals(false, tu.isInDSTFiveHourOffset(dt));
    	dt = TimeUtils.DATETIME_FORMAT.parse("2014-03-10 00:00");
    	assertEquals(false, tu.isInDSTFiveHourOffset(dt));        	
    }
    
    public void testGetDayOffset() {
    	Date dt;
    	TimeUtils tu = new TimeUtils();

    	dt = TimeUtils.DATETIME_FORMAT.parse("2009-10-31 00:00");
    	assertEquals(0, tu.getDayOffset(dt));
    	dt = TimeUtils.DATETIME_FORMAT.parse("2009-11-01 00:00");
    	assertEquals(82800000, tu.getDayOffset(dt));
    	dt = TimeUtils.DATETIME_FORMAT.parse("2009-11-02 00:00");
    	assertEquals(0, tu.getDayOffset(dt));
    	
    	dt = TimeUtils.DATETIME_FORMAT.parse("2014-03-08 00:00");
    	assertEquals(0, tu.getDayOffset(dt));
    	dt = TimeUtils.DATETIME_FORMAT.parse("2014-03-09 00:00");
    	assertEquals(3600000, tu.getDayOffset(dt));
    	dt = TimeUtils.DATETIME_FORMAT.parse("2014-03-10 00:00");
    	assertEquals(0, tu.getDayOffset(dt));        	
    }
    
    public void testGetDay() {
    	Date dt;
    	TimeUtils tu = new TimeUtils();
    	
    	dt = TimeUtils.DATETIME_FORMAT.parse("2009-10-30 00:00");
    	assertEquals(14547, tu.getDay(dt));
    	dt = TimeUtils.DATETIME_FORMAT.parse("2009-10-31 00:00");
    	assertEquals(14548, tu.getDay(dt));
    	dt = TimeUtils.DATETIME_FORMAT.parse("2009-11-01 00:00");
    	assertEquals(14548, tu.getDay(dt));
    	dt = TimeUtils.DATETIME_FORMAT.parse("2009-11-02 00:00");
    	assertEquals(14550, tu.getDay(dt));
    	
    	dt = TimeUtils.DATETIME_FORMAT.parse("2014-03-07 00:00");
    	assertEquals(16136, tu.getDay(dt));
    	dt = TimeUtils.DATETIME_FORMAT.parse("2014-03-08 00:00");
    	assertEquals(16137, tu.getDay(dt));
    	dt = TimeUtils.DATETIME_FORMAT.parse("2014-03-09 00:00");
    	assertEquals(16138, tu.getDay(dt));
    	dt = TimeUtils.DATETIME_FORMAT.parse("2014-03-10 00:00");
    	assertEquals(16139, tu.getDay(dt));        	
    } 
    
    public void testIsToday() {
    	Date dt;
    	TimeUtils tu = new TimeUtils();
    	
    	dt = TimeUtils.DATETIME_FORMAT.parse("2009-10-30 00:00");
    	assertEquals(false, tu.isToday(dt));
    	assertEquals(true,  tu.isToday(new Date()));
    }
}
