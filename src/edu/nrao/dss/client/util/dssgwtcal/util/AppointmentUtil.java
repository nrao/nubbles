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

package edu.nrao.dss.client.util.dssgwtcal.util;

import edu.nrao.dss.client.util.dssgwtcal.Appointment;
import edu.nrao.dss.client.util.dssgwtcal.AppointmentInterface;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class AppointmentUtil {

    // Filters a list of appointments and returns only appointments with a start
    // date equal to the date provided. 
    public static ArrayList filterListByDate(ArrayList fullList, Date startDate) {

        ArrayList<AppointmentInterface> group = new ArrayList<AppointmentInterface>();
        startDate = new Date(startDate.getYear(), startDate.getMonth(),
                startDate.getDate(), 0, 0, 0);
        Date endDate = new Date(startDate.getYear(), startDate.getMonth(),
                startDate.getDate(), 0, 0, 0);
        endDate.setDate(endDate.getDate() + 1);

        // NOTE: this will *NOT* add an appointment that ends on 'endDate', i.e. midnight
        for (int i = 0; i < fullList.size(); i++) {
            AppointmentInterface tmpAppt = (AppointmentInterface) fullList.get(i);
            if (tmpAppt.getEnd().before(endDate)) {
                if (tmpAppt.getStart().after(startDate) || tmpAppt.getStart().equals(startDate)) {
                    group.add(tmpAppt);
                }
            }
        }

        return group;
    }

    public static Appointment checkAppointmentElementClicked(Element element,List<Appointment> appointments) {
        for (Appointment appt : appointments) {
            
            if ( DOM.isOrHasChild(appt.getElement(), element) ) {
                
                return appt;
            }
        }
        
        return null;
    }
}
