package edu.nrao.dss.client.util.dssgwtcal;

public class CalendarSettings {

    public static CalendarSettings DEFAULT_SETTINGS = new CalendarSettings();
    private int pixelsPerInterval = 30; //IE6 cannot be less than 20!!!!! 
    private int intervalsPerHour = 2;
    private int workingHourStart = 8;
    private int workingHourEnd = 17;
    private int scrollToHour = 8; //default hour that gets scrolled to
    private boolean enableDragDrop = true;
    private boolean offsetHourLabels = false;

    public CalendarSettings() {
    }

    public int getPixelsPerInterval() {
        return pixelsPerInterval;
    }

    public void setPixelsPerInterval(int px) {
        pixelsPerInterval = px;
    }

    public int getIntervalsPerHour() {
        return intervalsPerHour;
    }

    public void setIntervalsPerHour(int intervals) {
        intervalsPerHour = intervals;
    }

    public int getWorkingHourStart() {
        return workingHourStart;
    }

    public void setWorkingHourStart(int start) {
        workingHourStart = start;
    }

    public int getWorkingHourEnd() {
        return workingHourEnd;
    }

    public void setWorkingHourEnd(int end) {
        workingHourEnd = end;
    }

    public int getScrollToHour() {
        return scrollToHour;
    }

    public void setScrollToHour(int hour) {
        scrollToHour = hour;
    }

    public boolean isEnableDragDrop() {
        return enableDragDrop;
    }

    public void setEnableDragDrop(boolean enableDragDrop) {
        this.enableDragDrop = enableDragDrop;
    }

    public boolean isOffsetHourLabels() {
        return offsetHourLabels;
    }

    public void setOffsetHourLabels(boolean offsetHourLabels) {
        this.offsetHourLabels = offsetHourLabels;
    }
}
