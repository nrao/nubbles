package edu.nrao.dss.client.util.dssgwtcal;

import java.util.Date;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class Gwt_cal implements EntryPoint {
	/**
	 * The message displayed to the user when the server cannot be reached or
	 * returns an error.
	 */
	private static final String SERVER_ERROR = "An error occurred while "
			+ "attempting to contact the server. Please check your network "
			+ "connection and try again.";

	/**
	 * Create a remote service proxy to talk to the server-side Greeting service.
	 */
	private final GreetingServiceAsync greetingService = GWT
			.create(GreetingService.class);

	/**
	 * This is the entry point method.
	 */
	public void onModuleLoad() {
		final Button sendButton = new Button("Send");
		final TextBox nameField = new TextBox();
		nameField.setText("GWT User");

		// We can add style names to widgets
		sendButton.addStyleName("sendButton");

		// Add the nameField and sendButton to the RootPanel
		// Use RootPanel.get() to get the entire body element
		RootPanel.get("nameFieldContainer").add(nameField);
		RootPanel.get("sendButtonContainer").add(sendButton);

		// Focus the cursor on the name field when the app loads
		nameField.setFocus(true);
		nameField.selectAll();

		// Create the popup dialog box
		final DialogBox dialogBox = new DialogBox();
		dialogBox.setText("Remote Procedure Call");
		dialogBox.setAnimationEnabled(true);
		final Button closeButton = new Button("Close");
		// We can set the id of a widget by accessing its Element
		closeButton.getElement().setId("closeButton");
		final Label textToServerLabel = new Label();
		final HTML serverResponseLabel = new HTML();
		VerticalPanel dialogVPanel = new VerticalPanel();
		dialogVPanel.addStyleName("dialogVPanel");
		dialogVPanel.add(new HTML("<b>Sending name to the server:</b>"));
		dialogVPanel.add(textToServerLabel);
		dialogVPanel.add(new HTML("<br><b>Server replies:</b>"));
		dialogVPanel.add(serverResponseLabel);
		dialogVPanel.setHorizontalAlignment(VerticalPanel.ALIGN_RIGHT);
		dialogVPanel.add(closeButton);
		dialogBox.setWidget(dialogVPanel);

		// Add a handler to close the DialogBox
		closeButton.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				dialogBox.hide();
				sendButton.setEnabled(true);
				sendButton.setFocus(true);
			}
		});

		// Create a handler for the sendButton and nameField
		class MyHandler implements ClickHandler, KeyUpHandler {
			/**
			 * Fired when the user clicks on the sendButton.
			 */
			public void onClick(ClickEvent event) {
				sendNameToServer();
			}

			/**
			 * Fired when the user types in the nameField.
			 */
			public void onKeyUp(KeyUpEvent event) {
				if (event.getNativeKeyCode() == KeyCodes.KEY_ENTER) {
					sendNameToServer();
				}
			}

			/**
			 * Send the name from the nameField to the server and wait for a response.
			 */
			private void sendNameToServer() {
				sendButton.setEnabled(false);
				String textToServer = nameField.getText();
				textToServerLabel.setText(textToServer);
				serverResponseLabel.setText("");
				greetingService.greetServer(textToServer,
						new AsyncCallback<String>() {
							public void onFailure(Throwable caught) {
								// Show the RPC error message to the user
								dialogBox
										.setText("Remote Procedure Call - Failure");
								serverResponseLabel
										.addStyleName("serverResponseLabelError");
								serverResponseLabel.setHTML(SERVER_ERROR);
								dialogBox.center();
								closeButton.setFocus(true);
							}

							public void onSuccess(String result) {
								dialogBox.setText("Remote Procedure Call");
								serverResponseLabel
										.removeStyleName("serverResponseLabelError");
								serverResponseLabel.setHTML(result);
								dialogBox.center();
								closeButton.setFocus(true);
							}
						});
			}
		}

		// Add a handler to send the name to the server
		MyHandler handler = new MyHandler();
		sendButton.addClickHandler(handler);
		nameField.addKeyUpHandler(handler);
		
	   	GWT.log("TestCalendar", null);
    	Label label = new Label("Hello world");
    	RootPanel.get().add(label);
        //initLayout();
    	
    	Date startCalendarDay = new Date();
    	Integer numCalendarDays = 3;
		DayView dayView = new DayView();
		GWT.log("staring calendar with: " + startCalendarDay.toString(), null);
		//Date day = new Date(startCalendarDay.getYear(), startCalendarDay.getMonth(), startCalendarDay.getMonth());
		//GWT.log("staring calendar at: " + day.toString(), null);
		dayView.setDate(startCalendarDay); //calendar date, not required
		dayView.setDays((int) numCalendarDays); //number of days displayed at a time, not required
		dayView.setWidth("100%");
		//dayView.setHeight("100%");
		dayView.setTitle("Schedule Calendar");
		CalendarSettings settings = new CalendarSettings();
		// this fixes offset issue with time labels
		settings.setOffsetHourLabels(false);
		// 15-min. boundaries!
		settings.setIntervalsPerHour(4);
		settings.setEnableDragDrop(true);
		dayView.setSettings(settings);   	
		RootPanel.get().add(dayView);

		
		// test data - should work
        //Appointment appt = new Appointment();
        Date dt = new Date();
        Date start = dt;
        Date end = new Date((long) (dt.getTime() + (60.0 * 60.0 * 1000.0) - (60.0 * 1000.0)));
        Event event = new Event(0, "Event", "desc", start, end, "test", "test", "test");
        
        dayView.addAppointments(event.getAppointments());
//        appt.setStart(start);
//        appt.setEnd(end);
//        appt.setTitle("Period");
//        appt.setDescription("should be an hour long");
//        appt.addStyleName("gwt-appointment-blue");
//        dayView.addAppointment(appt);
//        
//        // test data - should wrap around midnight and needs to be fixed
//        Appointment appt2 = new Appointment();
        Date end2 = new Date((long) (dt.getTime() + (24.0 * 60.0 * 60.0 * 1000.0)));
//        appt2.setStart(start);
//        appt2.setEnd(end2);
//        appt2.setTitle("Period2");
//        appt2.setDescription("should be a day long");
//        appt2.addStyleName("gwt-appointment-blue");
//        dayView.addAppointment(appt2);
        Event event2 = new Event(0, "Event2", "desc2", start, end2, "test", "test", "test");
        
        dayView.addAppointments(event2.getAppointments());

        Date end3 = new Date((long) (dt.getTime() + (2.0 * 24.0 * 60.0 * 60.0 * 1000.0)));
        Event event3 = new Event(0, "Event3", "desc3", start, end3, "test", "test", "test");
        
        dayView.addAppointments(event3.getAppointments());

//        long day  = (long) (start.getTime() / (60.0 * 60.0 * 24.0 * 1000.0));
//        long day2 = (long) ( end2.getTime() / (60.0 * 60.0 * 24.0 * 1000.0)); 
//        GWT.log(Long.toString(day), null);
//        GWT.log(Long.toString(day2), null);

	}
	
	
}
