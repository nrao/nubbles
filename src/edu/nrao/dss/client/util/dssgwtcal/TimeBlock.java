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

package edu.nrao.dss.client.util.dssgwtcal;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Represents a block of time that contains one or many Appointments. An
 * example of a timeblock could be 10am - 10:30 am, where a block of time
 * is 30 minutes.<p>
 * 
 * A block of time can contain or be interested by one or many appointments.
 * Using the above example, a time block from 10am - 10:30 am could contain
 * the following appointments:
 * <li>10:00 - 10:30, where start / end are same as time block</li>
 * <li>9:00 - 10:30, where appointment start before and ends at same time as tiem block</li>
 * <li>9:00 - 10:20, where appointment start before and ends after start of time block</li>
 * <li>9:00 - 11:00, where appointment starts before and ends after the time block</li>
 * <li>10:05 - 10:25, where appointment starts after but ends before the time block</li><p>
 * 
 * Above are example use cases of a time block and how it relates to an
 * appointment. Note that an appointment can intersect with one ore many
 * time blocks. This class holds a number of parameters allowing a time block
 * to manage its appointments and determing where the appointments should
 * be arranged within itself.
 * 
 * @author Brad Rydzewski
 * @version 1.0 6/07/09
 * @since 1.0
 */
public class TimeBlock {

    private List<AppointmentAdapter> appointments = new ArrayList<AppointmentAdapter>();
    private Map occupiedColumns = new HashMap();
    private int totalColumns = 1;
    private int order;
    private String name;
    private int start;
    private int end;
    private float top;
    private float bottom;

    public List<AppointmentAdapter> getAppointments() {
        return appointments;
    }

    public Map getOccupiedColumns() {
        return occupiedColumns;
    }
    
    public int getFirstAvailableColumn() {
        
        int col = 0;
        while(true) {
            if(occupiedColumns.containsKey(col))
                col++;
            else return col;
        }
    }

    public int getTotalColumns() {
        return totalColumns;
    }

    public void setTotalColumns(int totalColumns) {
        this.totalColumns = totalColumns;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getOrder() {
        return order;
    }

    public void setOrder(int order) {
        this.order = order;
    }

    public int getEnd() {
        return end;
    }

    public void setEnd(int end) {
        this.end = end;
    }

    public int getStart() {
        return start;
    }

    public void setStart(int start) {
        this.start = start;
    }

    public float getBottom() {
        return bottom;
    }

    public void setBottom(float bottom) {
        this.bottom = bottom;
    }

    public float getTop() {
        return top;
    }

    public void setTop(float top) {
        this.top = top;
    }
    
    // add this column to the list of occupied columns
    public void registerColumn(int column) {
    	getOccupiedColumns().put(column, column);
    }
   
    public boolean intersectsWith(int apptStart, int apptEnd) {
        
        //scenario 1: start date of appt between start and end of block
        if(apptStart >= this.getStart() && 
                apptStart < this.getEnd())
            return true;
        
        //scenario 2: end date of appt > block start date & 
        //  start date of appt before block start date
        if(apptEnd > this.getStart() && 
                apptStart < this.getStart())
            return true;
        
        return false;
    }
    
    public boolean intersectsWith(AppointmentAdapter appt) {
        
        return intersectsWith(appt.getAppointmentStart(), appt.getAppointmentEnd());
    }
}
