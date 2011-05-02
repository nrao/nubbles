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
    private int columnSpan;
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

    public int getColumnSpan() {
        return columnSpan;
    }

    public void setColumnSpan(int columnSpan) {
        this.columnSpan = columnSpan;
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
