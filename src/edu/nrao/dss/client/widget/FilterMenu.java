package edu.nrao.dss.client.widget;

import java.util.ArrayList;
import java.util.List;

import com.extjs.gxt.ui.client.widget.menu.Menu;

import edu.nrao.dss.client.widget.explorers.FilterComboMenuItem;

public class FilterMenu extends Menu{
	List<FilterComboMenuItem> items = new ArrayList<FilterComboMenuItem>();

	public void add(FilterComboMenuItem item) {
		super.add(item);
		items.add(item);
	}
	
	public List<FilterComboMenuItem> getFilterItems(){
		return items;
	}
}
