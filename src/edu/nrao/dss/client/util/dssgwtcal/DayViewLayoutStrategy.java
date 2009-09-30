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
	
	
	public ArrayList<AppointmentAdapter> doLayout(List<AppointmentInterface> appointments, int dayIndex, int dayCount) {

		
		int intervalsPerHour = settings.getSettings().getIntervalsPerHour(); //30 minute intervals
		float intervalSize = settings.getSettings().getPixelsPerInterval(); //25 pixels per interval
		
		/*
		 * Note: it is important that all appointments are sorted by Start date
		 * (asc) and Duration (desc) for this algorithm to work. If that is not
		 * the case, it won't work, at all!! Maybe this is a problem that needs
		 * to be addressed
		 */

		// set to 30 minutes. this means there will be 48 cells. 60min / 30min
		// interval * 24
		//int minutesPerInterval = 30;
		// interval size, set to 100px
		//float sizeOfInterval = 25f;

		// a calendar can view multiple days at a time. sets number of visible
		// days
		// TODO: use this later, not currently implemented
		// float numberOfDays = dates.size();

		int minutesPerInterval = MINUTES_PER_HOUR / intervalsPerHour;
		
		// get number of cells (time blocks)
		int numberOfTimeBlocks = MINUTES_PER_HOUR / minutesPerInterval
				* HOURS_PER_DAY;
		TimeBlock[] timeBlocks = new TimeBlock[numberOfTimeBlocks];

		for (int i = 0; i < numberOfTimeBlocks; i++) {
			TimeBlock t = new TimeBlock();
			t.setStart(i * minutesPerInterval);
			t.setEnd(t.getStart() + minutesPerInterval);
			t.setOrder(i);
			t.setTop((float) i * intervalSize);
			t.setBottom(t.getTop() + intervalSize);
			timeBlocks[i] = t;
		}

		// each appointment will get "wrapped" in an appoinetment cell object,
		// so that we can assign it a location in the grid, row and
		// column span, etc.
		ArrayList<AppointmentAdapter> appointmentCells = new ArrayList<AppointmentAdapter>();
		// Map<TimeBlock,TimeBlock> blockGroup = new
		// HashMap<TimeBlock,TimeBlock>();
		int groupMaxColumn = 0; // track total columns here! this will reset
								// when a group completes
		int groupStartIndex = -1;
		int groupEndIndex = -2;

		// Question: how to distinguish start / finish of a new group?
		// Answer: when endCell of previous appointments < startCell of new
		// appointment

		// for each appointments, we need to see if it intersects with each time
		// block
		for (AppointmentInterface appointment : appointments) {

			TimeBlock startBlock = null;
			TimeBlock endBlock = null;

			// if(blockGroupEndCell)

			// wrap appointment with AppointmentInterface Cell and add to list
			AppointmentAdapter apptCell = new AppointmentAdapter(appointment);
			appointmentCells.add(apptCell);

			// get the first time block in which the appointment should appear
			// TODO: since appointments are sorted, we should never need to
			// re-evaluate a time block that had zero matches...
			// store the index of the currently evaluated time block, if no
			// match, increment
			// that will prevent the same block from ever being re-evaluated
			// after no match found
			for (TimeBlock block : timeBlocks) {
				// does the appointment intersect w/ the block???
				if (block.intersectsWith(apptCell)) {

					// we found one! set as start block and exit loop
					startBlock = block;
					// blockGroup.put(block, block);

					if (groupEndIndex < startBlock.getOrder()) {

						//System.out.println("   prior group max cols: "
						//		+ groupMaxColumn);

						for (int i = groupStartIndex; i <= groupEndIndex; i++) {

							TimeBlock tb = timeBlocks[i];
							tb.setTotalColumns(groupMaxColumn + 1);
							//System.out.println("     total col set for block: "
							//		+ i);
						}
						groupStartIndex = startBlock.getOrder();
						//System.out.println("new group at: " + groupStartIndex);
						groupMaxColumn = 0;
					}

					break;
				} else {
					// here is where I would increment, as per above to-do
				}
			}

			// add the appointment to the start block
			startBlock.getAppointments().add(apptCell);
			// add block to appointment
			apptCell.getIntersectingBlocks().add(startBlock);

			// set the appointments column, if it has not already been set
			// if it has been set, we need to get it for reference later on in
			// this method
			int column = startBlock.getFirstAvailableColumn();
			apptCell.setColumnStart(column);
			apptCell.setColumnSpan(1); // hard-code to 1, for now

			// we track the max column for a time block
			// if a column get's added make sure we increment
			// if (startBlock.getTotalColumns() <= column) {
			// startBlock.setTotalColumns(column+1);
			// }

			// add column to block's list of occupied columns, so that the
			// column cannot be given to another appointment
			startBlock.getOccupiedColumns().put(column, column);

			// sets the start cell of the appt to the current block
			// we can do this since the blocks are ordered ascending
			apptCell.setCellStart(startBlock.getOrder());

			// go through all subsequent blocks...
			// find intersections
			for (int i = startBlock.getOrder() + 1; i < timeBlocks.length; i++) {

				// get the nextTimeBlock
				TimeBlock nextBlock = timeBlocks[i];

				// exit look if end date > block start, since no subsequent
				// blocks will ever intersect
				// if (apptCell.getAppointmentEnd() > nextBlock.getStart()) {
				// break; //does appt intersect with this block?
				// }
				if (nextBlock.intersectsWith(apptCell)) {

					// yes! add appointment to the block
					// register start column
					nextBlock.getAppointments().add(apptCell);
					nextBlock.getOccupiedColumns().put(column, column);
					endBlock = nextBlock; // this may change if intersects with
											// next block

					// add block to appointments list of intersecting blocks
					apptCell.getIntersectingBlocks().add(nextBlock);

					// we track the max column for a time block
					// if a column get's added make sure we increment
					// if (nextBlock.getTotalColumns() <= column) {
					// nextBlock.setTotalColumns(column+1);
					// }

					// blockGroup.put(nextBlock, nextBlock);
				}
			}

			// if end block was never set, use the start block
			endBlock = (endBlock == null) ? startBlock : endBlock;
			// maybe here is the "end" of a group, where we then evaluate max
			// column

			if (column > groupMaxColumn) {
				groupMaxColumn = column;
				// System.out.println("  max col: " + groupMaxColumn);
			}

			if (groupEndIndex < endBlock.getOrder()) {
				groupEndIndex = endBlock.getOrder();
				//System.out.println("  end index (re)set: " + groupEndIndex);
			}
			// for(int i = groupStartIndex; i<=groupEndIndex; i++) {
			// timeBlocks[i].setTotalColumns(groupMaxColumn);
			// }
			// groupMaxColumn=1;
			// }

			// for(TimeBlock timeBlock : blockGroup.values()) {
			//    
			// }

			// blockGroup = new HashMap<TimeBlock,TimeBlock>();

			// set the appointments cell span (top to bottom)
			apptCell.setCellSpan(endBlock.getOrder() - startBlock.getOrder()
					+ 1);

		}
		for (int i = groupStartIndex; i <= groupEndIndex; i++) {

			TimeBlock tb = timeBlocks[i];
			tb.setTotalColumns(groupMaxColumn + 1);
			//System.out.println("     total col set for block: " + i);
		}
		// we need to know the MAX number of cells for each time block.
		// so unfortunately we have to go back through the list to find this out
		/*
		 * for(AppointmentCell apptCell : appointmentCells) {
		 * 
		 * for (TimeBlock block : apptCell.getIntersectingBlocks()) {
		 * 
		 * int maxCol = 0;
		 * 
		 * //find the max cell for (AppointmentCell apptCell :
		 * block.getAppointments()) { int col = apptCell.getColumnStart(); if
		 * (col > maxCol) { maxCol = col; } }
		 * 
		 * block.setTotalColumns(maxCol+1); } }
		 */

		
		//last step is to calculate the adjustment reuired for 'multi-day' / multi-column
		float leftAdj = dayIndex / dayCount; //  0/3  or 2/3
		float widthAdj = 1f / dayCount;
		
		float paddingLeft =.5f;
		float paddingRight=.5f;
		float paddingBottom = 2;
		
		// now that everything has been assigned a cell, column and spans
		// we can calculate layout
		// Note: this can only be done after every single appointment has
		// been assigned a position in the grid
		for (AppointmentAdapter apptCell : appointmentCells) {

			float width = 1f / (float) apptCell.getIntersectingBlocks().get(0).getTotalColumns() * 100;
			float left = (float) apptCell.getColumnStart() / (float) apptCell.getIntersectingBlocks().get(0).getTotalColumns() * 100;
			
			AppointmentInterface appt = apptCell.getAppointment();
			appt.setTop((float) apptCell.getCellStart() * intervalSize); // ok!
			appt.setLeft((widthAdj*100*dayIndex) + (left * widthAdj) + paddingLeft  ); // ok
			appt.setWidth(width * widthAdj - paddingLeft - paddingRight); // ok!
			appt.setHeight((float) apptCell.getIntersectingBlocks().size()
					* ((float) intervalSize) - paddingBottom); // ok!

			float apptStart = apptCell.getAppointmentStart();
			float apptEnd = apptCell.getAppointmentEnd();
			float blockStart = timeBlocks[apptCell.getCellStart()].getStart();
			float blockEnd = timeBlocks[apptCell.getCellStart()+apptCell.getCellSpan()-1].getEnd();
			float blockDuration = blockEnd - blockStart;
			float apptDuration = apptEnd - apptStart;
			float timeFillHeight = apptDuration / blockDuration * 100f;
			float timeFillStart = (apptStart - blockStart) / blockDuration * 100f;
//			System.out.println("apptStart: "+apptStart);
//			System.out.println("apptEnd: "+apptEnd);
//			System.out.println("blockStart: "+blockStart);
//			System.out.println("blockEnd: "+blockEnd);
//			System.out.println("timeFillHeight: "+timeFillHeight);
			//System.out.println("timeFillStart: "+timeFillStart);
			//System.out.println("------------");
			apptCell.setCellPercentFill(timeFillHeight);
			apptCell.setCellPercentStart(  timeFillStart);
			appt.formatTimeline(apptCell.getCellPercentStart(), apptCell.getCellPercentFill());
		}

		return appointmentCells;
	}
}
