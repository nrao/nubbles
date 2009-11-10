package edu.nrao.dss.client.util.dssgwtcal;

import edu.nrao.dss.client.util.dssgwtcal.util.FormattingUtil;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.SimplePanel;

public /**
         * The Timeline Class is a sequential display of the hours in a day. Each
         * hour label should visually line up to a cell in the DayGrid.
         * 
         * @author Brad
         */
        class DayViewTimeline extends Composite {

    //private static final int MINUTES_PER_HOUR = 60;
    private static final int HOURS_PER_DAY = 24;
//	private int intervalsPerHour = settings.getIntervalsPerHour();//2; //30 minute intervals
//	private float intervalSize = settings.getPixelsPerInterval();//25f; //25 pixels per interval
    private AbsolutePanel timelinePanel = new AbsolutePanel();
    private HasSettings settings = null;
    private static final String TIME_LABEL_STYLE = "hour-label";
//    private final String[] HOURS = new String[]{"12 AM", "1 AM", "2 AM", "3 AM",
//        "4 AM", "5 AM", "6 AM", "7 AM", "8 AM", "9 AM", "10 AM",
//        "11 AM", "Noon", "1 PM", "2 PM", "3 PM", "4 PM", "5 PM",
//        "6 PM", "7 PM", "8 PM", "9 PM", "10 PM", "11 PM"
//    };
    private final String[] HOURS = new String[]{"12", "1", "2", "3",
        "4", "5", "6", "7", "8", "9", "10",
        "11", "Noon", "1", "2", "3", "4", "5",
        "6", "7", "8", "9", "10", "11"};
    private final String AM = " AM";
    private final String PM = " PM";
    // set this to change whether timeline uses AM, PM, or 24-hour style.
    private boolean amPmTime = false;
    
    public DayViewTimeline(HasSettings settings) {
        initWidget(timelinePanel);
        timelinePanel.setStylePrimaryName("time-strip");
        this.settings = settings;
        prepare();
    }

    public void prepare() {
        timelinePanel.clear();
        float labelHeight = 
                settings.getSettings().getIntervalsPerHour() * 
                settings.getSettings().getPixelsPerInterval();
        //float timeineHeight = labelHeight * HOURS_PER_DAY;
        //this.setHeight(timeineHeight+"px");



        int i = 0;
        if (settings.getSettings().isOffsetHourLabels() == true) {

            i = 1;
            SimplePanel sp = new SimplePanel();
            sp.setHeight((labelHeight / 2) + "px");
            timelinePanel.add(sp);
        }

        while (i < HOURS.length) {

        	String hour;
        	if (amPmTime) {
                hour = HOURS[i];
        	} else {
            	hour = Integer.toString(i) + ":00";
        	}
            i++;

            //block
            SimplePanel hourWrapper = new SimplePanel();
            hourWrapper.setStylePrimaryName(TIME_LABEL_STYLE);
            hourWrapper.setHeight((labelHeight + FormattingUtil.getBorderOffset()) + "px");

            FlowPanel flowPanel = new FlowPanel();
            flowPanel.setStyleName("hour-layout");
            
            Label hourLabel = new Label(hour);
            hourLabel.setStylePrimaryName("hour-text");
            flowPanel.add(hourLabel);
            
            if (amPmTime) {
	            String amPm = "";
	            if(i<13)
	                amPm = AM;
	            else if(i>13)
	                amPm = PM;
	            
	            Label ampmLabel = new Label(amPm);
	            ampmLabel.setStylePrimaryName("ampm-text");
	            flowPanel.add(ampmLabel);
            }

            hourWrapper.add(flowPanel);
            
            timelinePanel.add(hourWrapper);
        }
    }
}

