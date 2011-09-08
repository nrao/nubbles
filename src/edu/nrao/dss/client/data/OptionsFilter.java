package edu.nrao.dss.client.data;

import java.util.ArrayList;

import com.extjs.gxt.ui.client.event.MenuEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.button.ToggleButton;
import com.extjs.gxt.ui.client.widget.menu.CheckMenuItem;
import com.extjs.gxt.ui.client.widget.menu.Menu;
import com.extjs.gxt.ui.client.widget.menu.MenuItem;
import com.extjs.gxt.ui.client.widget.menu.SeparatorMenuItem;
import com.extjs.gxt.ui.client.widget.toolbar.ToolBar;

public class OptionsFilter {
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
		notcomplete.setToolTip("Filters for session that are not complete when checked.");
		notcomplete.toggle(true);
	}
	
	private void initEnabled() {
		enabled = new ToggleButton("Enabled");
		enabled.setToolTip("Filters for enabled session when checked.");
		enabled.toggle(true);
	}
	
	private void initSemester() {
		semester = new Button("Semester");
		semester.setToolTip("Select which semesters you want to see.");
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
			}
			
		});
		menu.add(none);
		menu.add(new SeparatorMenuItem());
		String[] semesters = new String[]{"11B", "11A", "10C", "10B", "10A"
				                        ,  "9C",  "9B",  "9A",  "8C",  "8B", "8A"};
		semesterItems = new ArrayList<CheckMenuItem>();
		CheckMenuItem cmi;
		for(String s: semesters) {
			cmi = new CheckMenuItem(s);
			cmi.setChecked(true);
			menu.add(cmi);
			semesterItems.add(cmi);
		}
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

}
