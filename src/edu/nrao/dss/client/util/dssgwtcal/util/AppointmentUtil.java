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
