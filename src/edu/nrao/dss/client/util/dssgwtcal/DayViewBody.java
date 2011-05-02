package edu.nrao.dss.client.util.dssgwtcal;

import java.util.Date;

import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.HasVerticalAlignment.VerticalAlignmentConstant;
import com.google.gwt.user.client.ui.Widget;

// This class acts as a wrapper around the DayViewGrid and DayViewTimeline Classes.
// A scroll panel is the parent (for scrolling - duh!), followed by a FlexTable, which
// actually positions the timeline and grid properly.

public class DayViewBody extends Composite {
	private FlexTable layout = new FlexTable();
	private ScrollPanel scrollPanel = new ScrollPanel();
	private DayViewTimeline timeline = null;
	private DayViewGrid grid = null;
	private HasSettings settings = null;

	public void add(Widget w) {
            scrollPanel.add(w);
        }
	
	public ScrollPanel getScrollPanel() {
		return scrollPanel;
	}

	public DayViewGrid getGrid() {
		return grid;
	}
	
	public DayViewTimeline getTimeline() {
		return timeline;
	}

	public DayViewGrid getDayViewGrid() {
		return grid;
	}

	public DayViewTimeline getDayViewTimeline() {
		return timeline;
	}

	public DayViewBody(HasSettings settings) {
		initWidget(scrollPanel);
		this.settings = settings;
		this.timeline = new DayViewTimeline(settings);
		this.grid = new DayViewGrid(settings);
		scrollPanel.setStylePrimaryName("scroll-area");
		DOM.setStyleAttribute(scrollPanel.getElement(), "overflowX",
				"hidden");
		DOM.setStyleAttribute(scrollPanel.getElement(), "overflowY",
				"scroll");

		// create the calendar body layout table
		layout.setCellPadding(0);
		layout.setBorderWidth(0);
		layout.setCellSpacing(0);
		layout.getColumnFormatter().setWidth(1, "99%");
		
		// set vertical alignment
		VerticalAlignmentConstant valign = HasVerticalAlignment.ALIGN_TOP;
		layout.getCellFormatter().setVerticalAlignment(0, 0, valign);
		layout.getCellFormatter().setVerticalAlignment(0, 1, valign);

		grid.setStyleName("gwt-appointment-panel");
                
        //TODO: use CSS to set table layout
        layout.getCellFormatter().setWidth(0, 0, "50px");
		DOM.setStyleAttribute(layout.getElement(), "tableLayout", "fixed");                
        
		// here we actually add the timeline and grid to the correct positions
		layout.setWidget(0, 0, timeline);
		layout.setWidget(0, 1, grid);
		
		// finally, add everything to the scroll panel
		scrollPanel.add(layout);
	}

	public void setDays(Date date, int days) {
		grid.build(settings.getSettings().getWorkingHourStart(), settings.getSettings().getWorkingHourEnd(), days);
	}
}
