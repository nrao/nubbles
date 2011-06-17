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

package edu.nrao.dss.client.widget;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import com.extjs.gxt.ui.client.Style;
import com.extjs.gxt.ui.client.event.BaseEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.widget.Dialog;
import com.extjs.gxt.ui.client.widget.HorizontalPanel;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.Text;
import com.extjs.gxt.ui.client.widget.VerticalPanel;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.ComboBox.TriggerAction;
import com.extjs.gxt.ui.client.widget.form.SimpleComboBox;
import com.extjs.gxt.ui.client.widget.form.TextArea;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.CheckBox;

import edu.nrao.dss.client.util.JSONCallbackAdapter;
import edu.nrao.dss.client.util.JSONRequest;

public class ProjectsEmailDialogBox extends Dialog {
	public ProjectsEmailDialogBox(String pcodes, String pi, String pc, String ci, String ob, String fs, String gb, String [][] temps)
	{
		super();

		setHeading("Email to Projects: " + pcodes);
		setButtons(Dialog.OKCANCEL);
		principal_investigators = pi;
		principal_contacts = pc;
		co_investigators = ci;
		observers = ob;
		friends = fs;
		gbtime = gb;
		pi_selected = true;
		pc_selected = false;
		ci_selected = false;
		fs_selected = false;
		gb_selected = true;

		// populate the template maps
		for (int i=0; i < temps.length; i++) {
			subjects.put(temps[i][0], temps[i][1]);
			bodies.put(temps[i][0], temps[i][2]);
		}
		
	}

	@Override
	protected void onRender(Element parent, int index) {
		super.onRender(parent, index);
		initLayout();
	}

	// this is only public so we can call it from unit tests
	public void initLayout() {
		String addr = "", subj = "", body = "";
		setSize(950, 700);
		VerticalPanel vp = new VerticalPanel();

		vp.setSpacing(10);
		vp.setScrollMode(Style.Scroll.NONE);
		vp.add(recipient_selector("Recipients:", 850, 40));
		vp.add(template_selector("Template:", 250, 25));
		vp.add(email_field("to:", addr, 850, 120));
		vp.add(email_field("subject:", subj, 850, 30));
		vp.add(email_field("body:", body, 850, 390));
		update_recipients();

		add(vp);
		
		Button cancel = getButtonById(Dialog.CANCEL);
		cancel.addListener(Events.OnClick, new Listener<BaseEvent>() {
			public void handleEvent(BaseEvent be) {
				hide();
			}
		});

		Button ok = getButtonById(Dialog.OK);
		ok.setText("Send");
		ok.addListener(Events.OnClick,new Listener<BaseEvent>() {
			public void handleEvent(BaseEvent be) {
	    		HashMap<String, Object> keys = new HashMap<String, Object>();
	    		String subject, body, address;
	    		
	    		TextArea subjectText = tareas.get("subject:");
	    		TextArea bodyText = tareas.get("body:");
	    		TextArea addressText = tareas.get("to:");
	    		subject = subjectText.getValue();
	    		body = bodyText.getValue();
	    		address = addressText.getValue();
	    		
	    		if (address == null)
	    		{
	    			MessageBox.alert("Projects Email", "Error! Address field is empty", null);
	    			return;
	    		}
	    		
	    		body = (body == null) ? "" : body;
	    		subject = (subject == null) ? "" : subject;
	    		keys.put("subject", subject);
	    		keys.put("body", body);
	    		keys.put("address", address);
	    		
	    		final MessageBox box = MessageBox.wait("Sending Email", "Sending e-mails to observers.", "Be Patient ...");
				JSONRequest.post("/scheduler/projects/email", keys,
						new JSONCallbackAdapter() {
							@Override
							public void onSuccess(JSONObject json) {
								box.close();
							}
							
							public void onError(String error, JSONObject json)
							{
								box.close();
								super.onError(error, json);
							}
						});
				hide();
			}
		});
	}

	private HorizontalPanel template_selector(String label, int width, int height) 
	{
		HorizontalPanel hp = new HorizontalPanel();
		
		// label
		Text field_label = new Text();		
	    field_label.setText(label);
	    hp.setSpacing(10);
	    hp.add(field_label);
	    
	    // picker
        final SimpleComboBox<String> templatePicker = new SimpleComboBox<String>();		
	    templatePicker.setForceSelection(true);
	    templatePicker.setTriggerAction(TriggerAction.ALL);       
	    templatePicker.setSize(width, height);
		for (String  name : subjects.keySet()) {
			templatePicker.add(name);
		}
		templatePicker.setSimpleValue("Blank");  
		
	    templatePicker.addListener(Events.Valid, new Listener<BaseEvent>() {
	    	public void handleEvent(BaseEvent be) {
	    		// what was picked?
	    		String name = (String) templatePicker.getSimpleValue();
	    		
	    		// set the subject
	    		TextArea subjectText = tareas.get("subject:");
	    		subjectText.setValue(subjects.get(name));
	    		
	    		// set the body
	    		TextArea bodyText = tareas.get("body:");
	    		bodyText.setValue(bodies.get(name));
	    		
	    		GWT.log("Set subj & body w/ template: " + name);
	    	}
	    });		
	    
		hp.add(templatePicker);
		
		return hp;
	}
	
	private HorizontalPanel recipient_selector(String label, int width, int height)
	{
		HorizontalPanel hp = new HorizontalPanel();
		Text field_label = new Text();

		
	    pi_cb.setValue(pi_selected);
	    pc_cb.setValue(pc_selected);
	    ci_cb.setValue(ci_selected);
	    ob_cb.setValue(ob_selected);
	    fs_cb.setValue(fs_selected);
	    gb_cb.setValue(gb_selected);

	    addCheckBoxClickHandler(pi_cb);
	    addCheckBoxClickHandler(pc_cb);
	    addCheckBoxClickHandler(ci_cb);
	    addCheckBoxClickHandler(ob_cb);
	    addCheckBoxClickHandler(fs_cb);
	    addCheckBoxClickHandler(gb_cb);
	    
	    field_label.setText(label);
	    hp.setSpacing(10);
	    hp.add(field_label);
	    hp.add(pi_cb);
	    hp.add(pc_cb);
	    hp.add(ci_cb);
	    hp.add(ob_cb);
	    hp.add(fs_cb);
	    hp.add(gb_cb);
	    
		return hp;
	}

	// whenever a checkbox is clicked, we update who the emails go to
	private void addCheckBoxClickHandler(CheckBox cb) {
	    cb.addClickHandler(new ClickHandler() {
	        public void onClick(ClickEvent event)
	        {
	        	update_recipients();
	        }
	     });		
	}
	
	private String join(Set<String> s, String delimiter) {
        StringBuffer buffer = new StringBuffer();
        Iterator<String> iter = s.iterator();
        
        while (iter.hasNext())
        {
            buffer.append(iter.next().trim());
            
            if (iter.hasNext())
            {
                buffer.append(delimiter);
            }
        }
        
        return buffer.toString();
    }
	
	private void add_addresses_to_set(String addresses, Set<String> ss, boolean add)
	{
		String v[];
		
		
		if (add)
		{
			v = addresses.split(", ");
			
			for (int i = 0; i < v.length; ++i)
			{
				ss.add(v[i]);
			}
		}
	}
	
	// public so we can unit test it
	public void update_recipients()
	{
		Set<String> recipients = new TreeSet<String>();
		String to;
		TextArea ta;
		
		// update the check box fields
		pi_selected = pi_cb.getValue();
		pc_selected = pc_cb.getValue();
		ci_selected = ci_cb.getValue();
		ob_selected = ob_cb.getValue();
		fs_selected = fs_cb.getValue();
		gb_selected = gb_cb.getValue();
		
		add_addresses_to_set(principal_investigators, recipients, pi_selected);
		add_addresses_to_set(principal_contacts, recipients, pc_selected);
		add_addresses_to_set(co_investigators, recipients, ci_selected);
		add_addresses_to_set(observers, recipients, ob_selected);
		add_addresses_to_set(friends, recipients, fs_selected);	
		add_addresses_to_set(gbtime, recipients, gb_selected);
		
		to = join(recipients, ", ");
		ta = tareas.get("to:");
		ta.setValue(to);
	}
	
	private HorizontalPanel email_field(String label, String content, int width, int height)
	{
		HorizontalPanel hp = new HorizontalPanel();
		Text field_label = new Text();
		TextArea field_text = new TextArea();
		field_label.setText(label);
		field_label.setWidth(50);
		field_text.setValue(content);
		field_text.setSize(width, height);
		field_text.setInputStyleAttribute("font-family", "monospace");
		hp.add(field_label);
		hp.add(field_text);
		tareas.put(label, field_text);
		return hp;
	}

	// for testing purposes only
	public String getTextArea(String name) {
		return tareas.get(name).getValue();
	}
	
	// for testing purposes only
	public void checkObservers(boolean check) {
		ob_cb.setValue(check);
	}
	
	public void checkPI(boolean check) {
		pi_cb.setValue(check);
	}
	
	private CheckBox pi_cb = new CheckBox("Principal Investigators");
	private CheckBox pc_cb = new CheckBox("Principal Contacts");
	private CheckBox ci_cb = new CheckBox("Co-Investigators");
	private CheckBox ob_cb = new CheckBox("Observers");
	private CheckBox fs_cb = new CheckBox("Friends");
	private CheckBox gb_cb = new CheckBox("gbtime");
	
	private boolean pi_selected;
	private boolean pc_selected;
	private boolean ci_selected;
	private boolean ob_selected;
	private boolean fs_selected;
	private boolean gb_selected;
	
	private String principal_investigators;
	private String principal_contacts;
	private String co_investigators;
	private String observers;
	private String friends;
	private String gbtime;
	
	private Map<String, String> subjects = new HashMap<String, String>();
	private Map<String, String> bodies   = new HashMap<String, String>();
	
	Map<String,TextArea> tareas = new HashMap<String, TextArea>();

}
