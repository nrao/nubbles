package edu.nrao.dss.client;


import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;

public class RcvrScheduleGrid extends Grid {
	public RcvrScheduleGrid(int rows, int cols, String[] headers, String[][] schedule) {
		super(rows, cols);
		initLayout();
		loadSchedule(rows, cols, headers, schedule);
	}	
	
	private void initLayout() {
		setCellSpacing(1);
		setBorderWidth(2);
		setCellPadding(1);
		setCellSpacing(1);
	}
	
	private void loadSchedule(int rows, int cols, String[] headers, String[][] schedule) {
		
	    // rows is the number of rows in the whole grid, which includes the headers
	    // so schedule has (rows-1) number of rows (schedule[rows-1][cols])
		
        for (int col = 0; col < cols; col++) {
        	setText(0, col, headers[col]);
            //colMap.put(headers[col], col);
            getCellFormatter().setHorizontalAlignment(0, col, HasHorizontalAlignment.ALIGN_CENTER);
        }
        int gridRow;
	    for (int day = 0; day < rows - 1; ++day){
	        for (int col = 0; col < cols; col++) {
	        	//int col = colMap.get(headers[fac]);
	        	gridRow = day + 1;
	        	GWT.log("text at [row][col]: ["+Integer.toString(day)+"]["+Integer.toString(col)+"] = "+schedule[day][col], null);
	            setText(gridRow, col, schedule[day][col]);
	            getCellFormatter().setHorizontalAlignment(gridRow, col, HasHorizontalAlignment.ALIGN_CENTER);
	            getCellFormatter().setWordWrap(gridRow, col, false);
	            // TODO: format the rcvr cell appropriatly: black for not available
	            //getCellFormatter().
	        }
	    }
		
	}
	

}
