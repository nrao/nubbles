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

package edu.nrao.dss.client.util.dssgwtcal;

import edu.nrao.dss.client.util.dssgwtcal.util.FormattingUtil;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.SimplePanel;

// This class is a sequential display of the hours in a day. Each
// hour label should visually line up to a cell in the DayGrid.

public class DayViewTimeline extends Composite {

    private static final int HOURS_PER_DAY = 24;
    private AbsolutePanel timelinePanel = new AbsolutePanel();
    private HasSettings settings = null;
    private static final String TIME_LABEL_STYLE = "hour-label";

    // used to easily look up labels when amPmTime flag is true
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

    // This function adds a panel for every hour of the day to the timelinePanel,
    // computing the height of each one based of the setting, and simply 
    // calling timelinePanel.add(new panel)
    public void prepare() {
    	
        timelinePanel.clear();
        
        // the height of each panel showing the hour in the timeline is 
        // computed from the settings
        float labelHeight = 
                settings.getSettings().getIntervalsPerHour() * 
                settings.getSettings().getPixelsPerInterval();
        
        int i = 0;
        if (settings.getSettings().isOffsetHourLabels() == true) {

            i = 1;
            SimplePanel sp = new SimplePanel();
            sp.setHeight((labelHeight / 2) + "px");
            timelinePanel.add(sp);
        }

        // Each hour in the day get's it's own panel (w/ subpanels)
        while (i < HOURS.length) {

        	// What's the text to actually be displayed?
        	// "13:00" vs. "1:00 PM"
        	String hour;
        	if (amPmTime) {
                hour = HOURS[i];
        	} else {
            	hour = Integer.toString(i) + ":00";
        	}
            i++;

            // We need a SimplePanel to hold our FlowPanel (why?)
            SimplePanel hourWrapper = new SimplePanel();
            hourWrapper.setStylePrimaryName(TIME_LABEL_STYLE);
            hourWrapper.setHeight((labelHeight + FormattingUtil.getBorderOffset()) + "px");

            // this new FlowPanel actualy holds the hour label (and optional AM/PM label).
            FlowPanel flowPanel = new FlowPanel();
            flowPanel.setStyleName("hour-layout");
            
            Label hourLabel = new Label(hour);
            hourLabel.setStylePrimaryName("hour-text");
            flowPanel.add(hourLabel);
            
            // Add "AM/PM" as another label only if we need it
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

