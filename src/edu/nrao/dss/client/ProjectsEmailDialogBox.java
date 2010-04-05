package edu.nrao.dss.client;

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
import com.extjs.gxt.ui.client.widget.form.TextArea;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.CheckBox;

public class ProjectsEmailDialogBox extends Dialog {
	public ProjectsEmailDialogBox(String pi, String pc, String ci)
	{
		super();

		setHeading("Projects Email");
		setButtons(Dialog.OKCANCEL);
		principal_investigators = pi;
		principal_contacts = pc;
		co_investigators = ci;
		pi_selected = true;
		pc_selected = false;
		ci_selected = false;
	}

	@Override
	protected void onRender(Element parent, int index) {
		super.onRender(parent, index);
		String addr = "", subj = "", body = "";
		setSize(950, 700);
		VerticalPanel vp = new VerticalPanel();

		vp.setSpacing(10);
		vp.setScrollMode(Style.Scroll.NONE);
		vp.add(recipient_selector("Recipients:", 850, 40));
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
				JSONRequest.post("/projects/email", keys,
						new JSONCallbackAdapter() {
							@Override
							public void onSuccess(JSONObject json) {
								box.close();
							}
						});
				hide();
			}
		});
	}

	private HorizontalPanel recipient_selector(String label, int width, int height)
	{
		HorizontalPanel hp = new HorizontalPanel();
		Text field_label = new Text();
		CheckBox pi_cb = new CheckBox("Principal Investigators");
		CheckBox pc_cb = new CheckBox("Principal Contacts");
		CheckBox ci_cb = new CheckBox("Co-Investigators");
	    pi_cb.setValue(pi_selected);
	    pc_cb.setValue(pc_selected);
	    ci_cb.setValue(ci_selected);

	    pi_cb.addClickHandler(new ClickHandler() {
	        public void onClick(ClickEvent event)
	        {
	        	CheckBox cb = (CheckBox)event.getSource();
	        	pi_selected = cb.getValue();
	        	update_recipients();
	        }
	     });

	    pc_cb.addClickHandler(new ClickHandler() {
	        public void onClick(ClickEvent event)
	        {
	        	CheckBox cb = (CheckBox)event.getSource();
	        	pc_selected = cb.getValue();
	        	update_recipients();
	        }
	     });

	    ci_cb.addClickHandler(new ClickHandler() {
	        public void onClick(ClickEvent event)
	        {
	        	CheckBox cb = (CheckBox)event.getSource();
	        	ci_selected = cb.getValue();
	        	update_recipients();
	        }
	     });

	    field_label.setText(label);
	    hp.setSpacing(10);
	    hp.add(field_label);
	    hp.add(pi_cb);
	    hp.add(pc_cb);
	    hp.add(ci_cb);
	    
		return hp;
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
	
	private void update_recipients()
	{
		Set<String> recipients = new TreeSet<String>();
		String to;
		TextArea ta;
		
		add_addresses_to_set(principal_investigators, recipients, pi_selected);
		add_addresses_to_set(principal_contacts, recipients, pc_selected);
		add_addresses_to_set(co_investigators, recipients, ci_selected);
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
		//field_text.setStyleAttribute("font-family", "monospace");
		// TBF: Use this line instead of above for extGWT 2.x!!!!
		field_text.setInputStyleAttribute("font-family", "monospace");
		hp.add(field_label);
		hp.add(field_text);
		tareas.put(label, field_text);
		return hp;
	}

	private boolean pi_selected;
	private boolean pc_selected;
	private boolean ci_selected;
	
	private String principal_investigators;
	private String principal_contacts;
	private String co_investigators;
	
	Map<String,TextArea> tareas = new HashMap<String, TextArea>();

}
