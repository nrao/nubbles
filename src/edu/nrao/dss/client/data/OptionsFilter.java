package edu.nrao.dss.client.data;

import java.util.ArrayList;
import java.util.HashMap;

import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.MenuEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.button.ToggleButton;
import com.extjs.gxt.ui.client.widget.menu.CheckMenuItem;
import com.extjs.gxt.ui.client.widget.menu.Menu;
import com.extjs.gxt.ui.client.widget.menu.MenuItem;
import com.extjs.gxt.ui.client.widget.menu.SeparatorMenuItem;
import com.extjs.gxt.ui.client.widget.toolbar.ToolBar;
import com.google.gwt.core.client.GWT;

import edu.nrao.dss.client.util.Subject;

public class OptionsFilter extends Subject {
	private ToggleButton notcomplete, enabled;
	private Button semester;
	private ToolBar toolbar;
	private ArrayList<CheckMenuItem> semesterItems;
	
	public OptionsFilter() {
		initNotComplete();
		initEnabled();
		initSemester();
	}
	
	private void initNotComplete() {
		notcomplete = new ToggleButton("Not Complete");
		notcomplete.setToolTip("Filters for sessions that are not complete in the options below when checked.");
		notcomplete.toggle(true);
		
		notcomplete.addSelectionListener(new SelectionListener<ButtonEvent> () {
			@Override
			public void componentSelected(ButtonEvent ce) {
				notifyObservers();
			}
		});
	}
	
	private void initEnabled() {
		enabled = new ToggleButton("Enabled");
		enabled.setToolTip("Filters for enabled sessions in the options below when checked.");
		enabled.toggle(true);
		
		enabled.addSelectionListener(new SelectionListener<ButtonEvent> () {
			@Override
			public void componentSelected(ButtonEvent ce) {
				notifyObservers();
			}
		});
	}
	
	private void initSemester() {
		semester = new Button("Semester");
		semester.setToolTip("Select which semesters you want to see in the project or session options below.");
		semester.setMenu(initSemesterMenu());
	}
	
	private Menu initSemesterMenu(){
		Menu menu = new Menu();
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
	
	public ToggleButton getNotComplete() {
		return notcomplete;
	}
	
	public ToggleButton getEnabled() {
		return enabled;
	}
	
	public Button getSemester() {
		return semester;
	}
	
	public HashMap<String, Object> getState() {
		HashMap<String, Object> state = new HashMap<String, Object>();
		state.put("enabled", enabled.isPressed());
		state.put("notcomplete", notcomplete.isPressed());
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
	    	  put("enabled", "true");
	    	  put("notcomplete", "true");
	        }};
	}

}
