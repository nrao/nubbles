package edu.nrao.dss.client.widget;


import java.util.ArrayList;
import java.util.Date;

import com.google.gwt.core.client.GWT;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;

import edu.nrao.dss.client.data.RcvrScheduleData;
import edu.nrao.dss.client.data.RcvrScheduleDate;

// This class if responsible for using a FlexTable to display the rx schedule

public class RcvrScheduleGrid extends FlexTable {
	
	private String[] maintenanceDays;
	private boolean showMaintenance = false;
	private String styleBase = "gwt-RcvrSchdGrid-";

    private static final DateTimeFormat DATE_FORMAT = DateTimeFormat.getFormat("MM/dd/yyyy");
    private static final DateTimeFormat DATE_FORMAT_MAINT = DateTimeFormat.getFormat("yyyy-MM-dd"); 	
    
	public RcvrScheduleGrid() {
		initLayout();
	}	
	
	private void initLayout() {

	    addStyleName("cw-FlexTable");
	    setCellSpacing(2);
	    setCellPadding(1);	
	    
	}

	
    public void loadRcvrScheduleData(RcvrScheduleData data) {
    	this.setMaintenanceDays(data.getMaintenanceDayStrs());
    	// headers start w/ the date, up rx, down rx, then the list of the rcvrs
		String [] headers = getHeaders(data.getReceiverNames());
		renderRcvrSchedule(headers, data);
    }

    private String[] getHeaders(String[] rx) {
		int numRcvrs = rx.length;
		int offset = 3;
		String[] headers = new String[numRcvrs + offset];
		headers[0] = "Date";
		headers[1] = "Up";
		headers[2] = "Down";
		for (int i = 0; i < numRcvrs; i++) {
			headers[i+offset] = rx[i];
		} 
		return headers;
    }
    
    private void renderRcvrSchedule(String[] headers, RcvrScheduleData data) {
    	clearAll();
        renderHeaders(headers);
        renderRows(data);
    }
    
    private void renderHeaders(String[] headers) {
    	int tableRow = 0;
		for (int tableCol = 0; tableCol < headers.length; tableCol++) {
			setHTML(0, tableCol, headers[tableCol]);
			getCellFormatter().setHorizontalAlignment(tableRow, tableCol, HasHorizontalAlignment.ALIGN_CENTER);
			getCellFormatter().setStyleName(tableRow, tableCol, styleBase + "header");
			getCellFormatter().setWidth(tableRow, tableCol, "200");
		}    	
    }
    
    private void renderRows(RcvrScheduleData data) {
    	
    	String[] rcvrs = data.getReceiverNames();
    	RcvrScheduleDate[] rowData = data.getDays();
    	int gridRow = 1; // row 0 is the header
    	int colOffset = 3; // first 3 columns are date, up, down
    	for (int i=0; i<rowData.length; i++) {
    		// first column  - the date
    		String dateStr = rowData[i].getDateStr();
			getCellFormatter().setStyleName(gridRow, 0, styleBase + "header");
			setHTML(gridRow, 0, dateStr);
			// next two columns - what's going up & going down
			getCellFormatter().setStyleName(gridRow, 1, styleBase + "header");
			setHTML(gridRow, 1, rowData[i].getUpStr());
			getCellFormatter().setStyleName(gridRow, 2, styleBase + "header");
			setHTML(gridRow, 2, rowData[i].getDownStr());
			// remaining columns: on or off indication for each rcvr
			for (int j=0; j<rcvrs.length; j++) {
				boolean on = rowData[i].isRcvrAvailable(rcvrs[j]);
				String styleName = on ? "on" : "off";
				int gridCol = j + colOffset; 
	    		getCellFormatter().setStyleName(gridRow, gridCol, styleBase + styleName);
			    setHTML(gridRow, gridCol, "");
			}
            // we're done inserting a row's worth of rcvr scheudle
			gridRow++;
			// but do we also have to show maintenance days?
			if (showMaintenance) {
				String[] daysBetween = data.getMaintenanceDayStrsBetween(i);
				// insert the maintenance days
			    for (int mRow = 0; mRow < daysBetween.length; mRow++) {
			    	setHTML(gridRow, 0, daysBetween[mRow]);
			    	gridRow++;
			    }
			}			
    	}
    	
    }
    
	private void clearAll() {
		// WTF: I can't believe that this is the only thing that really works - 
		// what a sucky interface!
		int rowCount = getRowCount();
		for (int i = 0; i < rowCount; i++) {
			removeRow(0);
		}		
	}
	
	public boolean isMaintenanceDay(String day) {
		for (String mday : maintenanceDays) {
			if (mday.compareTo(day) == 0) {
				return true; // found it!
			}
		}
	    return false; // couldn't find it:	
	}
	
	public void setMaintenanceDays(String[] maintenanceDays) {
		this.maintenanceDays = maintenanceDays;
	}	
	
	public void setShowMaintenance(boolean show) {
		showMaintenance = show;
	}
}
