package edu.nrao.dss.client.data;

import java.util.ArrayList;
import java.util.HashMap;

import com.extjs.gxt.ui.client.Style.HideMode;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.FieldEvent;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.MenuEvent;
import com.extjs.gxt.ui.client.event.SelectionChangedEvent;
import com.extjs.gxt.ui.client.event.SelectionChangedListener;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.button.ToggleButton;
import com.extjs.gxt.ui.client.widget.form.ComboBox.TriggerAction;
import com.extjs.gxt.ui.client.widget.form.SimpleComboBox;
import com.extjs.gxt.ui.client.widget.menu.CheckMenuItem;
import com.extjs.gxt.ui.client.widget.menu.Menu;
import com.extjs.gxt.ui.client.widget.menu.MenuItem;
import com.extjs.gxt.ui.client.widget.menu.SeparatorMenuItem;
import com.extjs.gxt.ui.client.widget.toolbar.ToolBar;
import com.google.gwt.core.client.GWT;

import edu.nrao.dss.client.util.Subject;

public class OptionsFilter extends Subject {
	private SimpleComboBox<String> notcomplete, enabled;
	private Button semester;
	private ToolBar toolbar;
	private ArrayList<CheckMenuItem> semesterItems;
	
	public OptionsFilter() {
		initNotComplete();
		initEnabled();
		initSemester();
	}
	
	private void initNotComplete() {
		notcomplete = new SimpleComboBox<String>();
		notcomplete.setToolTip("Filters for sessions that are not complete in the options below when checked.");
		notcomplete.add("Not Complete");
		notcomplete.add("Complete");
		notcomplete.add("All");
		notcomplete.setSimpleValue("Not Complete");
		notcomplete.setAllowBlank(false);
		notcomplete.setEditable(false);
		notcomplete.setTriggerAction(TriggerAction.ALL);
		
		notcomplete.addListener(Events.Select, new Listener<FieldEvent> () {
			@Override
			public void handleEvent(FieldEvent be) {
				notifyObservers();
			}
		});
	}
	
	private void initEnabled() {
		enabled = new SimpleComboBox<String>();
		enabled.setToolTip("Filters for enabled sessions in the options below when checked.");
		enabled.add("Enabled");
		enabled.add("Not Enabled");
		enabled.add("All");
		enabled.setSimpleValue("Enabled");
		enabled.setAllowBlank(false);
		enabled.setEditable(false);
		enabled.setTriggerAction(TriggerAction.ALL);
		
		enabled.addListener(Events.Select, new Listener<FieldEvent> () {
			@Override
			public void handleEvent(FieldEvent be) {
				notifyObservers();
			}
		});
	}
	
	private void initSemester() {
		semester = new Button("Semester");
		semester.setToolTip("Select which semesters you want to see in the project or session options below in the drop down menu.");
		semester.setMenu(initSemesterMenu());
	}
	
	private Menu initSemesterMenu(){
		Menu menu = new Menu();
		menu.setHideMode(HideMode.DISPLAY);
		MenuItem all = new MenuItem("Select All");
		all.addSelectionListener(new SelectionListener<MenuEvent>() {

			@Override
			public void componentSelected(MenuEvent ce) {
				for(CheckMenuItem s: semesterItems) {
					s.setChecked(true);
				}
				notifyObservers();
			}
			
		});
		menu.add(all);
		MenuItem none = new MenuItem("Select None");
		none.addSelectionListener(new SelectionListener<MenuEvent>() {

			@Override
			public void componentSelected(MenuEvent ce) {
				for(CheckMenuItem s: semesterItems) {
					s.setChecked(false);
				}
				notifyObservers();
			}
			
		});
		menu.add(none);
		menu.add(new SeparatorMenuItem());
		String[] semesters = new String[]{"11B", "11A", "10C", "10B", "10A"
				                        , "09C", "09B", "09A", "08C", "08B","08A"
				                        , "07C", "07B", "07A"};
		semesterItems = new ArrayList<CheckMenuItem>();
		CheckMenuItem cmi;
		for(String s: semesters) {
			cmi = new CheckMenuItem(s);
			menu.add(cmi);
			semesterItems.add(cmi);
			cmi.addSelectionListener(new SelectionListener<MenuEvent> () {
			@Override
			public void componentSelected(MenuEvent ce) {
				notifyObservers();
			}
			});
		}
		// Set the default, last two semesters checked
		semesterItems.get(0).setChecked(true);
		semesterItems.get(1).setChecked(true);
		return menu;
	}
	
	private void initToolBar(){
		toolbar = new ToolBar();
		toolbar.add(semester);
		toolbar.add(enabled);
		toolbar.add(notcomplete);
	}
	
	public ToolBar getToolbar() {
		initToolBar();
		return toolbar;
	}
	
	public SimpleComboBox<String> getNotComplete() {
		return notcomplete;
	}
	
	public SimpleComboBox<String> getEnabled() {
		return enabled;
	}
	
	public Button getSemester() {
		return semester;
	}
	
	public HashMap<String, Object> getState() {
		HashMap<String, Object> state = new HashMap<String, Object>();
		state.put("enabled", enabled.getRawValue());
		state.put("notcomplete", notcomplete.getRawValue());
		ArrayList<String> semesters = new ArrayList<String>();
		for (CheckMenuItem cmi : semesterItems) {
			if (cmi.isChecked()) {
				semesters.add(cmi.getText());
			}
		}
		state.put("semesters", semesters);
		return state;
	}
	
	@SuppressWarnings("serial")
	public static HashMap<String, Object> getDefaultState(final String mode) {
		return new HashMap<String, Object>() {{
	    	  put("mode", mode);
	    	  put("semesters", "[11A, 11B]");
	    	  put("enabled", "Enabled");
	    	  put("notcomplete", "Not Complete");
	        }};
	}

}
