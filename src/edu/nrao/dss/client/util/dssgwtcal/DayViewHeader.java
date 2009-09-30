package edu.nrao.dss.client.util.dssgwtcal;

import edu.nrao.dss.client.util.dssgwtcal.util.WindowUtils;
import java.util.Date;

import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.Label;

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

	public DayViewHeader(HasSettings settings) {
		this.settings = settings;
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
		// header.getCellFormatter().setStyleName(1, 0,SPLITTER_STYLE);

		header.getFlexCellFormatter().setColSpan(1, 0, 3);
		header.setCellPadding(0);
		header.setBorderWidth(0);
		header.setCellSpacing(0);

		splitter.setStylePrimaryName(SPLITTER_STYLE);
		header.setWidget(1, 0, splitter);
	}

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

			Label dayLabel = new Label();
			dayLabel.setStylePrimaryName("day-cell");
			dayLabel.setWidth(dayWidth + "%");
			dayLabel.setText(headerTitle);
			DOM.setStyleAttribute(dayLabel.getElement(), "left", dayLeft
					+ "%");

			// set the style of the header to show that it is today
			if (new Date().getYear() == date.getYear()
					&& new Date().getMonth() == date.getMonth()
					&& new Date().getDate() == date.getDate()) {
				dayLabel.setStyleName("day-cell-today");
			}

			dayPanel.add(dayLabel);
		}
	}

	public void setYear(Date date) {
		// System.out.println("setting year: " + date);
		setYear(1900 + date.getYear());
	}

	public void setYear(int year) {
		header.setText(0, 0, String.valueOf(year));
	}

}


