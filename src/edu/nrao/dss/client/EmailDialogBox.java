package edu.nrao.dss.client;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import com.extjs.gxt.ui.client.widget.Dialog;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.DateField;
import com.extjs.gxt.ui.client.widget.form.FormPanel;
import com.extjs.gxt.ui.client.widget.form.SimpleComboBox;
import com.extjs.gxt.ui.client.widget.form.TextArea;
import com.extjs.gxt.ui.client.widget.form.TextField;
import com.extjs.gxt.ui.client.widget.form.Time;
import com.extjs.gxt.ui.client.widget.form.TimeField;
import com.extjs.gxt.ui.client.widget.layout.FormData;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.extjs.gxt.ui.client.widget.layout.VBoxLayout;
import com.extjs.gxt.ui.client.widget.layout.VBoxLayout.VBoxLayoutAlign;
import com.extjs.gxt.ui.client.event.BaseEvent;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.Listener;
import com.google.gwt.core.client.GWT;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.user.client.Window;

import edu.nrao.dss.client.util.TimeUtils;

// TODO: this does more then just change a period - it can replace several periods
// w/ a single period.  But it's also ideal for inserting backups.  so what to call it?

class EmailDialogBox extends Dialog {
	public EmailDialogBox(String emails, String subject, String body) {
		super();
		
		// Basic Dlg settings
		setHeading("Email Schedule");
		addText("Review (and edit) the schedule e-mail before sending it to observers and staff.");
		setButtons(Dialog.OKCANCEL);
		GWT.log("EmailDialogBox", null);

		// now set up the form w/ all it's fields
		final FormPanel fp = new FormPanel();

		final TextArea addressText = new TextArea();
		addressText.setFieldLabel("Addresses");
		addressText.setValue(emails);
		fp.add(addressText, new FormData(500, 100));
		
		final TextField subjectText = new TextField();
		subjectText.setFieldLabel("Subject");
		subjectText.setValue(subject);
		fp.add(subjectText, new FormData(500, 25));

		final TextArea bodyText = new TextArea();
		bodyText.setFieldLabel("Body");
		bodyText.setValue(body);
		fp.add(bodyText, new FormData(500, 350));
		
		add(fp);
		setSize(700, 650);

		Button cancel = getButtonById(Dialog.CANCEL);
		cancel.addListener(Events.OnClick, new Listener<BaseEvent>() {
			public void handleEvent(BaseEvent be) {
				close();
			}
		});

		Button ok = getButtonById(Dialog.OK);
		ok.addListener(Events.OnClick,new Listener<BaseEvent>() {
			public void handleEvent(BaseEvent be) {
	    		HashMap<String, Object> keys = new HashMap<String, Object>();
	    		keys.put("subject", subjectText.getValue());
	    		keys.put("body", bodyText.getValue());
	    		keys.put("emails", addressText.getValue());
	    		final MessageBox box = MessageBox.wait("Sending Email", "Sending scheduling e-mails to observers and staff.", "Be Patient ...");
				JSONRequest.post("/schedule/email", keys,
						new JSONCallbackAdapter() {
							public void onSuccess(JSONObject json) {
								GWT.log("/schedule/email (POST) onSuccess", null);
								box.close();
							}
						});
				close();
			}
		});
	}
}