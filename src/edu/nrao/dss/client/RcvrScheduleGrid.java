package edu.nrao.dss.client;


import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;

public class RcvrScheduleGrid extends FlexTable {
	public RcvrScheduleGrid() {
		initLayout();
	}	
	
	private void initLayout() {
	    FlexCellFormatter cellFormatter = getFlexCellFormatter();
	    cellFormatter.setHorizontalAlignment(0, 1,
		        HasHorizontalAlignment.ALIGN_LEFT);
	    addStyleName("cw-FlexTable");
	    setWidth("32em");
	    setCellSpacing(4);
	    setCellPadding(1);	
	    
	}
	
	public void loadSchedule(int rows, int cols, String[] headers, String[][] schedule) {

		// start fresh
		clear();
		
		// first the header
		for (int col = 0; col < cols; col++) {
			setHTML(0, col, headers[col]);
		}
		
		GWT.log("rows, cols: "+Integer.toString(rows) + " " + Integer.toString(cols), null);
		// now the data
		for (int row = 0; row < rows; row++) {
			for (int col = 0; col < cols; col++) {
				//GWT.log("["+Integer.toString(row) + "][" + Integer.toString(col)+"]: "+schedule[row][col], null);
				setHTML(row + 1, col, schedule[row][col]);
				
			}
		}
		
	}
}
