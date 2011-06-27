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
import java.util.Date;
import java.util.List;

// This class is responsible for converting figuring out how overlapping Appointments
// are to appear in the calendar.

public class AppointmentAdapter {

    private AppointmentInterface appointment;
    private int cellStart;
    private int cellSpan;
    private int columnStart = -1;
    private int appointmentStart;
    private int appointmentEnd;
    private float cellPercentFill;
    private float cellPercentStart;
    private List<TimeBlock> intersectingBlocks;

    public AppointmentAdapter(AppointmentInterface appointment) {
        this.appointment = appointment;
        this.appointmentStart = calculateDateInMinutes(appointment.getStart());
        this.appointmentEnd = calculateDateInMinutes(appointment.getEnd());
        this.intersectingBlocks = new ArrayList<TimeBlock>();
    }

    public int getStartMinutes() {
    	return intersectingBlocks.get(0).getStart();
    }
    
    public int getEndMinutes() {
    	return intersectingBlocks.get(intersectingBlocks.size()-1).getEnd();
    }
    
    public int getCellStart() {
        return cellStart;
    }

    public void setCellStart(int cellStart) {
        this.cellStart = cellStart;
    }

    public int getCellSpan() {
        return cellSpan;
    }

    public void setCellSpan(int cellSpan) {
        this.cellSpan = cellSpan;
    }

    public int getColumnStart() {
        return columnStart;
    }

    public void setColumnStart(int columnStart) {
        this.columnStart = columnStart;
    }

    public int getAppointmentStart() {
        return appointmentStart;
    }

    public void setAppointmentStart(int appointmentStart) {
        this.appointmentStart = appointmentStart;
    }

    public int getAppointmentEnd() {
        return appointmentEnd;
    }

    public void setAppointmentEnd(int appointmentEnd) {
        this.appointmentEnd = appointmentEnd;
    }

    public List<TimeBlock> getIntersectingBlocks() {
        return intersectingBlocks;
    }

    public void setIntersectingBlocks(List<TimeBlock> intersectingBlocks) {
        this.intersectingBlocks = intersectingBlocks;
    }

    public AppointmentInterface getAppointment() {
        return appointment;
    }

    protected int calculateDateInMinutes(Date date) {
        return date.getHours() * 60 + date.getMinutes();
    }

    public float getCellPercentFill() {
        return cellPercentFill;
    }

    public void setCellPercentFill(float cellPercentFill) {
        this.cellPercentFill = cellPercentFill;
    }

    public float getCellPercentStart() {
        return cellPercentStart;
    }

    public void setCellPercentStart(float cellPercentStart) {
        this.cellPercentStart = cellPercentStart;
    }
}
