package edu.nrao.dss.client.util.dssgwtcal;

import edu.nrao.dss.client.util.dssgwtcal.util.TimeUtils;
import edu.nrao.dss.client.util.dssgwtcal.util.WindowUtils;
import java.util.Date;

import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.Label;

// This class is responsible for the top of the calendar that looks like this:
// | 2011 | Mon, May 2 | Tue, May 3 |
// A FlexTable is resonsible for the overall layout, with the dates (Mon, May 2, ...) being handled
// by an AbsoluteLabel with a Label for each date.

public class DayViewHeader extends Composite {
	private FlexTable header = new FlexTable();
	private AbsolutePanel dayPanel = new AbsolutePanel();
	private AbsolutePanel splitter = new AbsolutePanel();
	private HasSettings settings = null;
	private static final String GWT_CALENDAR_HEADER_STYLE = "gwt-calendar-header";
	private static final String DAY_CELL_CONTAINER_STYLE = "day-cell-container";
	private static final String YEAR_CELL_STYLE = "year-cell";
	private static final String SPLITTER_STYLE = "splitter";
	
	public static final String[] DAY_LIST = new String[] { "Sun", "Mon", "Tue",
		"Wed", "Thu", "Fri", "Sat" };
public static final String[] MONTH_LIST = new String[] { "Jan", "Feb",
		"Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov",
		"Dec" };

    private TimeUtils tu;
    
	public DayViewHeader(HasSettings settings) {
		this.settings = settings;
		tu = new TimeUtils();
		
		initWidget(header);
		header.setStyleName(GWT_CALENDAR_HEADER_STYLE);
		dayPanel.setStyleName(DAY_CELL_CONTAINER_STYLE);

		header.insertRow(0);
		header.insertRow(0);
		header.insertCell(0, 0);
		header.insertCell(0, 0);
		header.insertCell(0, 0);
		header.setWidget(0, 1, dayPanel);
		header.getCellFormatter().setStyleName(0, 0, YEAR_CELL_STYLE);
		header.getCellFormatter().setWidth(0, 2,
				WindowUtils.getScrollBarWidth(true) + "px");

		header.getFlexCellFormatter().setColSpan(1, 0, 3);
		header.setCellPadding(0);
		header.setBorderWidth(0);
		header.setCellSpacing(0);

		splitter.setStylePrimaryName(SPLITTER_STYLE);
		header.setWidget(1, 0, splitter);
	}

	// For each day, creates a label w/ text like "Mon, May 2";
	// the label get it's DOM's left attribute set properly
	// and that label gets added to dayPanel
	public void setDays(Date date, int days) {

		dayPanel.clear();
		float dayWidth = 100f / days;
		float dayLeft = 0f;

		for (int i = 0; i < days; i++) {

			// increment the date by 1
			if (i > 0)
				date.setDate(date.getDate() + 1);

			// set the left position of the day splitter to
			// the width * incremented value
			dayLeft = dayWidth * i;

			String headerTitle = DAY_LIST[date.getDay()] + ", "
					+ MONTH_LIST[date.getMonth()] + " " + date.getDate();
//			if (tu.isDSTBoundary(date)) {
//				// warn users that this Daylight Savings Time starts or ends on this day.
//				headerTitle += " (DST)";
//			}

			Label dayLabel = new Label();
			//dayLabel.setStylePrimaryName("day-cell");
			dayLabel.setWidth(dayWidth + "%");
			dayLabel.setText(headerTitle);
			// Question: why are we using the DOM here?
			DOM.setStyleAttribute(dayLabel.getElement(), "left", dayLeft
					+ "%");

			// how should this day be displayed?
			String styleName = "day-cell"; //default value
			if (tu.isToday(date)) {
				styleName = "day-cell-today";
			}
//			if (tu.isDSTBoundary(date)) {
//				// notice how displaying the fact that the day is DST takes
//				// precedence over the fact that it is today.
//			    styleName = "day-cell-dst";	
//			}
			dayLabel.setStyleName(styleName);
			
			dayPanel.add(dayLabel);
		}
	}

	public void setYear(Date date) {
		setYear(1900 + date.getYear());
	}

	public void setYear(int year) {
		header.setText(0, 0, String.valueOf(year));
	}

}


