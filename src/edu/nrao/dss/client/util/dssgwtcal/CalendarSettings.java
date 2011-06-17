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
