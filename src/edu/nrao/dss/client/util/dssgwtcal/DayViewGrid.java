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
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.ComplexPanel;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;

// The DayGrid draws the grid that displays days / time intervals in the
// body of the calendar.

public class DayViewGrid extends Composite {
	
    
	  // This class is needed so we can overrid the add/remove methods.
	  // but why?  And why is it internal?
      class Div extends ComplexPanel {
            	
		  public Div() {
	            setElement(DOM.createDiv());
		  }
          @Override
          public boolean remove(Widget w) {
                boolean removed = super.remove(w);
                return removed;
          }
          @Override
          public void add(Widget w) {
               super.add(w, getElement());
          }
    }
    
	private static final int CELL_HEIGHT = 50;
	private static final String INTERVAL_MAJOR_STYLE = "major-time-interval";
	private static final String INTERVAL_MINOR_STYLE = "minor-time-interval";
	private static final String WORKING_HOUR_STYLE = "working-hours";
	protected ComplexPanel grid = new Div();
        
	private HasSettings settings = null;
	
	
	private static final int HOURS_PER_DAY = 24;


	public DayViewGrid(HasSettings settings) { //was DayViewGridImpl
		initWidget(grid);
		this.settings = settings;
	}

	// using the given number of days, and the major & minor hour intervals,
	// creates SimplePanel's for each of these things.  I assume it is the edges
	// of these panels which appears as the lines, and thus make our calendar grid
	// (check CSS).
	public void build(int workingHourStart, int workingHourStop, int days) {

		grid.clear();
                
		int intervalsPerHour = settings.getSettings().getIntervalsPerHour();//2; //30 minute intervals
		float intervalSize = settings.getSettings().getPixelsPerInterval();

		this.setHeight( (intervalsPerHour * (intervalSize) * 24 ) +"px" );
		
		float dayWidth = 100f / days;
		float dayLeft = 0f;

		// create the horizontal lines by creating SimplePanel's for the 
		// major hour intervals (one each hour) and the minor hour intervals
		// (from settings)
		for (int i = 0; i < HOURS_PER_DAY; i++) {
			
			boolean isWorkingHours = (i >= workingHourStart && i <= workingHourStop);
			
			// create major interval - it's height is, for the most part, what makes
			// our hour intervals appear correctly
			SimplePanel sp1 = new SimplePanel();
			sp1.setStyleName("major-time-interval");
			sp1.setHeight(intervalSize+FormattingUtil.getBorderOffset()+"px");
			
			// make working hours stand out
			if (isWorkingHours) {
				sp1.addStyleName("working-hours");
			}
			
			//add to body
			grid.add(sp1);
			
			// there may be serveral minor hour intervals in this hour
			for(int x=0;x<intervalsPerHour-1;x++) {
				// this works much like it did for the major hour interval:
				// a panel per minor interval, whose height is what make it
				// appear correctly in the calendar
				SimplePanel sp2 = new SimplePanel();
				sp2.setStyleName("minor-time-interval");
				
				sp2.setHeight(intervalSize+FormattingUtil.getBorderOffset()+"px");
				if (isWorkingHours) {
					sp2.addStyleName("working-hours");
				}
				grid.add(sp2);
			}
			
			
		}

		// we're done with the horizontal line representing the hours;
		// now we need to add the vertical lines to show the different days
		for (int day = 0; day < days; day++) {

			// unlike using the height to get the position of the hour lines correctly;
			// here we compute the left hand position of the new panel, and set it to that.
			dayLeft = dayWidth * day;

			SimplePanel dayPanel = new SimplePanel();
			dayPanel.setStyleName("day-separator");
			grid.add(dayPanel);
			// Q, Note: why must we do this after we've added the SimplePanle,
			// using the DOM?
			DOM.setStyleAttribute(dayPanel.getElement(), "left", dayLeft
					+ "%");
		}
	}


}

