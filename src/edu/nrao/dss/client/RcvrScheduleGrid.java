package edu.nrao.dss.client;


import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.FlexTable;
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
	    setCellSpacing(2);
	    setCellPadding(1);	
	    
	}
	
	public void loadSchedule(int rows, int cols, String[] headers, String[][] schedule) {

		// start fresh
		clear();

		String styleBase = "gwt-RcvrSchdGrid-";

		// first the header
		for (int col = 0; col < cols; col++) {
			// TODO: how to set the width of these columns?
			//getColumnFormatter().setWidth(col, "200px");
			setHTML(0, col, headers[col]);
			getCellFormatter().setStyleName(0, col, styleBase + "header");
			
		}
		
		GWT.log("rows, cols: "+Integer.toString(rows) + " " + Integer.toString(cols), null);
		
		
		// now the data
		for (int row = 0; row < rows; row++) {
			for (int col = 0; col < cols; col++) {
				//GWT.log("["+Integer.toString(row) + "][" + Integer.toString(col)+"]: "+schedule[row][col], null);
				String scheduleValue = schedule[row][col];
				boolean on = (scheduleValue.compareTo("T") == 0) ? true : false;
				String styleName = on ? "on" : "off"; 
    			if (col != 0) {
	    			getCellFormatter().setStyleName(row + 1, col, styleBase + styleName);
				} else {
					getCellFormatter().setStyleName(row + 1, col, styleBase + "header");
				}
				String value = (col == 0) ? scheduleValue : "";
			    setHTML(row + 1, col, value);
			}
		}
		
	}
}
