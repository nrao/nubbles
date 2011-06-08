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
