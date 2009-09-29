package edu.nrao.dss.client.util.dssgwtcal;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HasValue;
import java.util.ArrayList;
import java.util.Date;



/**
 *
 * @author Brad Rydzewski
 */
public class Calendar extends Composite implements HasValue<Appointment> {
//    public void onModuleLoad() {
//        initLayout();
//    }
//    private void initLayout() {
//        GWT.log("initLayout", null);
//    }
    
    private AbsolutePanel rootPanel = new AbsolutePanel();
    private CalendarView currentView = null;
    private CalendarSettings settings = null;
    private ArrayList<ValueChangeHandler> handlers = null;
    public static final int DAY_VIEW = 1;
    public static final int THREE_DAY_VIEW = 2;
    public static final int FIVE_DAY_VIEW = 3;
    public static final int SEVEN_DAY_VIEW = 4;
//    public static final int MONTH_VIEW = 4;
//    public static final int AGENDA_VIEW = 5;

    public Calendar() {
        this(CalendarSettings.DEFAULT_SETTINGS, DAY_VIEW);
    }
    
    public Calendar(CalendarSettings settings, int defaultView) {
        initWidget(rootPanel);
        DOM.setStyleAttribute(rootPanel.getElement(), "overflow", "visible");
        
        this.settings = settings;
        this.handlers = new ArrayList<ValueChangeHandler>();
        setCalendarView(defaultView);
        
    }

    
    public void setCalendarView(int view) {
        
        CalendarView newView = null;
        
        //if day view, calculate how many days to display
//        if(view < 5) {
//            
//            int days = 1;
//            switch(view) {
//                case 2 : days = 3; break;
//                case 3 : days = 5; break;
//                case 4 : days = 7; break;
//            }
//            
//            //if the current view is already a day view, just set days and done!
//            if(currentView!=null && currentView instanceof DayView) {
//                currentView.setDays(days);
//                return;
//                
//            //else create a new day view and set the days, but don't exit yet
//            } else {
//                newView = new DayView();
//                newView.setDays(days);
//            }
//        } else {
//            
//            //calculate other views!!!!!
//            
//        }
        
        switch(view) {
            case 1 : newView = new DayView(settings); newView.setDays(1); break;
            case 2 : newView = new DayView(settings); newView.setDays(3); break;
            case 3 : newView = new DayView(settings); newView.setDays(5); break;
            case 4 : newView = new DayView(settings); newView.setDays(7); break;
        }
        
        setCalendarView(newView);
    }


    
    public void setCalendarView(CalendarView view) {
        
        CalendarView newView = view;
        CalendarView oldView = this.currentView;
        
        //suspend layout
        newView.suspendLayout();
        
        //tmp values to transfer from current to new currentView
        ArrayList appointments = new ArrayList<Appointment>();
        Appointment selectedAppointment = null;
        Date date = new Date();
        String width = null;
        String height = null;
        
        if(oldView!=null) {
            //detatch from parent... does I need to do anything else?
            appointments = oldView.getAppointments();
            date = oldView.getDate();
            selectedAppointment = oldView.getValue();
            height = DOM.getStyleAttribute(oldView.getElement(), "height");
            width = "100%";//DOM.getStyleAttribute(oldView.getElement(), "width");
            oldView.removeFromParent();
            
            this.currentView = null;
        }
        
        //set currentView
        this.currentView = newView;
        
        //add currentView to this panel
        rootPanel.add(newView);
        newView.setDate(date);
        
        //transfer width and height
        if(width !=null && !width.isEmpty()) {
            newView.setWidth(width);
        }
        if(height !=null && !height.isEmpty()) {
            newView.setHeight(height);
        }
        
        //transfer selected appointment
        if(selectedAppointment!=null)
            newView.setValue(selectedAppointment);
                
        //transfer all handlers
        for(ValueChangeHandler<Appointment> handler : handlers) {
            newView.addValueChangeHandler(handler);
        }
        
        //transfer appointments
        newView.addAppointments(appointments);
        
        //resume layout
        newView.resumeLayout();
    }



    public void suspendLayout() {
        currentView.suspendLayout();
    }

    public void resumeLayout() {
        currentView.resumeLayout();
    }

    public CalendarSettings getSettings() {
        return settings;
    }

    public void setSettings(CalendarSettings settings) {
        this.settings = settings;
        currentView.setSettings(settings);
    }

    public Date getDate() {
        return currentView.getDate();
    }

    public void setDate(Date date, int days) {
        currentView.setDate(date);
    }

    public void setDate(Date date) {
        currentView.setDate(date);
    }

    public int getDays() {
        return currentView.getDays();
    }

    public void setDays(int days) {
        currentView.setDays(days);
    }

    public int getAppointmentCount() {
        return currentView.getAppointmentCount();
    }

    public AppointmentInterface getAppointmentAtIndex(int index) {
        return currentView.getAppointmentAtIndex(index);
    }

    public AppointmentInterface getSelectedAppointment() {
        return currentView.getSelectedAppointment();
    }

    public void setSelectedAppointment(Appointment appointment) {
        currentView.setSelectedAppointment(appointment);
    }

    public void updateAppointment(Appointment appointment) {
        currentView.updateAppointment(appointment);
    }

    public void clearAppointments() {
        currentView.clearAppointments();
    }

    public void removeAppointment(Appointment appointment) {
        currentView.removeAppointment(appointment);
    }

    public void addAppointment(Appointment appointment) {
        currentView.addAppointment(appointment);
    }

    public void addAppointments(ArrayList<Appointment> appointments) {
        currentView.addAppointments(appointments);
    }

    public Appointment getValue() {
        return currentView.getValue();
    }

    public void setValue(Appointment value) {
        currentView.setValue(value, true);
    }

    public void setValue(Appointment value, boolean fireEvents) {
        currentView.setValue(value, fireEvents);
    }

    public HandlerRegistration addValueChangeHandler(
            ValueChangeHandler<Appointment> handler) {
        
        this.handlers.add(handler);
        return currentView.addValueChangeHandler(handler);
    }
    
    
    
    @Override
    public void setHeight(String height) {
        super.setHeight(height);
        if(currentView!=null) {
            int h = super.getOffsetHeight();
            currentView.setHeight(height);//h+"px");
            
        }
    }

    @Override
    public void setSize(String width, String height) {
        super.setSize(width, height);
        if(currentView!=null) {
            int h = super.getOffsetHeight();
            currentView.setSize(width, height); //h+"px"
        }
    }

}
