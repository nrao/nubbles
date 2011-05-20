package edu.nrao.dss.client.util.dssgwtcal;

import java.util.ArrayList;
import java.util.List;

/**
 * Responsible for arranging all Appointments, visually, on a screen in a manner
 * similar to the Microsoft Outlook / Windows Vista calendar. 
 * See: <img src='http://www.microsoft.com/library/media/1033/athome/images/moredone/calendar.gif'/>
 * <p>
 * Note how overlapping appointments are displayed in the provided image
 * 
 * @author Brad Rydzewski
 * @version 1.0 6/07/09
 * @since 1.0
 */
public class DayViewLayoutStrategy {

	private static final int MINUTES_PER_HOUR = 60;
	private static final int HOURS_PER_DAY = 24;

	private HasSettings settings = null;
	public DayViewLayoutStrategy(HasSettings settings) {
		this.settings = settings;
	}

	// given the appointments for day i of n days, calculate how they are laid out on the calendar
	public ArrayList<AppointmentAdapter> doLayout(List<AppointmentInterface> appointments, int dayIndex, int dayCount) {
	    ArrayList<AppointmentAdapter> appointmentCells = prepareLayout(appointments);
	    return calculateLayout(appointmentCells, dayIndex, dayCount); 
	}	

	// Figures out how the appointments are laid out in terms of 'cells': that is, time blocks, and
	// columns (multiple columns are needed when appointments overlap).
	// The returned AppointmentAdapters wrap each appointment and their 'cell' info.
	public ArrayList<AppointmentAdapter> prepareLayout(List<AppointmentInterface> appointments) {

		/*
		 * Note: it is important that all appointments are sorted by Start date
		 * (asc) and Duration (desc) for this algorithm to work. If that is not
		 * the case, it won't work, at all!! Maybe this is a problem that needs
		 * to be addressed
		 */

		int intervalsPerHour = settings.getSettings().getIntervalsPerHour(); 
		float intervalSize = settings.getSettings().getPixelsPerInterval(); //pixels per interval
		
		int minutesPerInterval = MINUTES_PER_HOUR / intervalsPerHour;
		
		// get number of cells (time blocks)
		int numberOfTimeBlocks = MINUTES_PER_HOUR / minutesPerInterval
				* HOURS_PER_DAY;
		TimeBlock[] timeBlocks = new TimeBlock[numberOfTimeBlocks];

		// initialize all the time blocks
		for (int i = 0; i < numberOfTimeBlocks; i++) {
			TimeBlock t = new TimeBlock();
			t.setStart(i * minutesPerInterval);
			t.setEnd(t.getStart() + minutesPerInterval);
			t.setOrder(i);
			t.setTop((float) i * intervalSize);
			t.setBottom(t.getTop() + intervalSize);
			timeBlocks[i] = t;
		}

		// each appointment will get "wrapped" in an appointment cell object,
		// so that we can assign it a location in the grid, row and
		// column span, etc.
		ArrayList<AppointmentAdapter> appointmentCells = new ArrayList<AppointmentAdapter>();
		
		// A group is a set of overlapping appointments;
		// track total columns here: this will reset when a group completes
		int groupMaxColumn = 0; 
		int groupStartIndex = -1;
		int groupEndIndex = -2;

		// Loop through each appointment, finding the intersecting time blocks and
		// keeping track of overlaps (groups)
		for (AppointmentInterface appointment : appointments) {

			// init time block range
			TimeBlock startBlock = null;
			TimeBlock endBlock = null;

			// the appointment adapter is used for displaying the appointment
			AppointmentAdapter apptCell = new AppointmentAdapter(appointment);
			appointmentCells.add(apptCell);

			// get the first time block in which the appointment should appear
			for (TimeBlock block : timeBlocks) {
				// does the appointment intersect w/ the block???
				if (block.intersectsWith(apptCell)) {
					// we found one! set as start block and exit loop
					startBlock = block;
					// are we starting a new group? (does this appointment appear after
					// the last group ends?); 
					if (groupEndIndex < startBlock.getOrder()) {
						// before we an start a new group, use the last group's info
						// to set the group's column info.
						for (int i = groupStartIndex; i <= groupEndIndex; i++) {
							TimeBlock tb = timeBlocks[i];
							tb.setTotalColumns(groupMaxColumn + 1);
						}
						// init new group
						groupStartIndex = startBlock.getOrder();
						groupMaxColumn = 0;
					}
					break;
				}	
			}

			// cross-reference the appointment and starting block
			startBlock.getAppointments().add(apptCell);
			apptCell.getIntersectingBlocks().add(startBlock);

			// use the timeblocks knowledge about what column to use (based off any
			// overlaps we may have) to set the appointment's column.
			int column = startBlock.getFirstAvailableColumn();
			apptCell.setColumnStart(column);

			// add column to block's list of occupied columns, so that the
			// column cannot be given to another appointment
			startBlock.registerColumn(column);

			// sets the start cell of the appt to the current block
			// we can do this since the blocks are ordered ascending
			apptCell.setCellStart(startBlock.getOrder());

			// go through all subsequent blocks to find the intersecting blocks
			for (int i = startBlock.getOrder() + 1; i < timeBlocks.length; i++) {

				// get the nextTimeBlock
				TimeBlock nextBlock = timeBlocks[i];

				if (nextBlock.intersectsWith(apptCell)) {

					// yes! cross-reference appointment and time block and register column
					apptCell.getIntersectingBlocks().add(nextBlock);
					nextBlock.getAppointments().add(apptCell);
					nextBlock.registerColumn(column);
					
					// keep track of this in case it's the last time block for 
					// this appointment
					endBlock = nextBlock;
				}
			}

			// if end block was never set, use the start block
			endBlock = (endBlock == null) ? startBlock : endBlock;
			
			// what's the duration of our appointment in time blocks?
			apptCell.setCellSpan(endBlock.getOrder() - startBlock.getOrder() + 1);

			// expand the group (of overlapping appointments), if need be 
			if (column > groupMaxColumn) {
				groupMaxColumn = column;
			}

			if (groupEndIndex < endBlock.getOrder()) {
				groupEndIndex = endBlock.getOrder();
			}
		}
		
		// now that we're done looping through the appointments, use the last group's
		// info to set the time block column info.
		for (int i = groupStartIndex; i <= groupEndIndex; i++) {
			TimeBlock tb = timeBlocks[i];
			tb.setTotalColumns(groupMaxColumn + 1);
		}

    	return appointmentCells;
	
	}

	// Given a list of AppointmentAdapters, which wrap each appointment together with it's 'cell' info (where the appointment is in terms
	// of time blocks and columns), converts this 'cell' info into actual pixel positions of the appointments in the calendar.
	// This position info is stored in the returned AppointmentAdapters so that they are ready to be displayed.
	private ArrayList<AppointmentAdapter> calculateLayout(ArrayList<AppointmentAdapter> appointmentCells, int dayIndex, int dayCount)	    {

    	float intervalSize = settings.getSettings().getPixelsPerInterval(); //pixels per interval
		
		//last step is to calculate the adjustment reuired for 'multi-day' / multi-column
		//float leftAdj = dayIndex / dayCount; //  0/3  or 2/3
		float widthAdj = 1f / dayCount;
		
		float paddingLeft =.5f;
		float paddingRight=.5f;
		float paddingBottom = 2;
		
		// now that everything has been assigned a cell, column and spans we can calculate layout
		for (AppointmentAdapter apptCell : appointmentCells) {

			float width = 1f / (float) apptCell.getIntersectingBlocks().get(0).getTotalColumns() * 100;
			float left = (float) apptCell.getColumnStart() / (float) apptCell.getIntersectingBlocks().get(0).getTotalColumns() * 100;
			
			AppointmentInterface appt = apptCell.getAppointment();
			appt.setTop((float) apptCell.getCellStart() * intervalSize); 
			appt.setLeft((widthAdj*100*dayIndex) + (left * widthAdj) + paddingLeft  ); 
			appt.setWidth(width * widthAdj - paddingLeft - paddingRight); 
			appt.setHeight((float) apptCell.getIntersectingBlocks().size()
					* ((float) intervalSize) - paddingBottom); 

			float apptStart = apptCell.getAppointmentStart();
			float apptEnd = apptCell.getAppointmentEnd();
			float blockStart = apptCell.getStartMinutes();
			float blockEnd = apptCell.getEndMinutes();
			float blockDuration = blockEnd - blockStart;
			float apptDuration = apptEnd - apptStart;
			float timeFillHeight = apptDuration / blockDuration * 100f;
			float timeFillStart = (apptStart - blockStart) / blockDuration * 100f;
			apptCell.setCellPercentFill(timeFillHeight);
			apptCell.setCellPercentStart(  timeFillStart);
			appt.formatTimeline(apptCell.getCellPercentStart(), apptCell.getCellPercentFill());
		}

		return appointmentCells;
	}
}
