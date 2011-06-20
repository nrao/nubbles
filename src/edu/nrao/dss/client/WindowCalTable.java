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

package edu.nrao.dss.client;

import java.util.Date;

import com.google.gwt.core.client.GWT;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;

import edu.nrao.dss.client.data.WindowCalendarData;

public class WindowCalTable extends FlexTable {
	int numCols;
	private String styleBase = "gwt-RcvrSchdGrid-";
    private DateTimeFormat dtFormat = DateTimeFormat.getFormat("yyyy-MM-dd");
    
	public WindowCalTable() {
		initLayout();
	}	
	
	private void initLayout() {

	    addStyleName("cw-FlexTable");
	    setCellSpacing(2);
	    setCellPadding(1);	
	    
	}

	// headers = ["Label", "Start", "date 1" ... "date n", "End"]
	public String[] getHeaders(Date start, int numDays) {
		numCols = numDays + 3;
        String[] headers = new String[numCols];
        String label = "Session (total/billed) Complete?";
        headers[0] = label;
        headers[1] = "Start";
        headers[numCols-1] = "End";		
		long offset;
		long msPerDay = 1000 * 60 * 60 * 24;
		Date[] dates = new Date[numDays];
        for (int i=0; i<numDays; i++) {		
		    offset = ((long) i) * msPerDay;
		    dates[i] = new Date(start.getTime() + offset);
		    headers[i+2] = dtFormat.format(dates[i]);
        }    		
		return headers;
		
	}
	public void renderCalendar(WindowCalendarData[] data, Date start, int numDays) {
		
		// prepare for rendering
        numCols = numDays + 3;
        String[] headers = getHeaders(start, numDays);
        
		// start fresh
		clearAll();

		// first the headers - the first row
		int row = 0;
		for (int col = 0; col < numCols; col++) {
			setHTML(0, col, headers[col]);
			getCellFormatter().setHorizontalAlignment(0, col, HasHorizontalAlignment.ALIGN_CENTER);
			getCellFormatter().setStyleName(0, col, styleBase + "header");
			getCellFormatter().setWidth(row, col, "200");
		}        
		
		// now the windows
		row = 0;
		int col = 0;
		for (int i=0; i<data.length; i++) {
		    WindowCalendarData wd = data[i];
		    row = i + 1; // first row is the headers
		    // first column is the window label
		    getCellFormatter().setStyleName(row, 0, styleBase + "off");
		    setHTML(row, 0, wd.getLabel());
		    // the rest are the days in the calendar for this window
		    //for (int day=0; day<numDays; day++) {
		    for (int day=0; day<wd.getDisplayFlags().length; day++) {
		    	col = day+1; // first col. is the session label
				String styleName = wd.isDayNumberInWindow(day) ? "on" : "off"; 
				getCellFormatter().setStyleName(row, col, styleBase + styleName);
		    	setHTML(row, col, wd.getDayNumberInfo(day));
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
}
