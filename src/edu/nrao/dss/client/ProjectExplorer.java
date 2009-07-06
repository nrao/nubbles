package edu.nrao.dss.client;

import java.util.List;
import java.util.ArrayList;

import com.extjs.gxt.ui.client.widget.form.CheckBox;
import com.extjs.gxt.ui.client.widget.form.TextField;
import com.extjs.gxt.ui.client.widget.grid.CellEditor;
import com.extjs.gxt.ui.client.widget.grid.CheckColumnConfig;
import com.extjs.gxt.ui.client.widget.grid.ColumnConfig;
import com.extjs.gxt.ui.client.widget.grid.ColumnModel;

public class ProjectExplorer extends Explorer {
	public ProjectExplorer() {
		super("/projects", new ProjectType());
		initLayout(initColumnModel());
	}
	
	private ColumnModel initColumnModel() {
		
		List<ColumnConfig> configs = new ArrayList<ColumnConfig>();

	    ColumnConfig column = new ColumnConfig("pcode", "PCode", 100);
	    column.setEditor(new CellEditor(new TextField<String>()));
	    configs.add(column);

	    column = new ColumnConfig("name", "Name", 400);
	    column.setEditor(new CellEditor(new TextField<String>()));
	    configs.add(column);

	    column = new ColumnConfig("semester", "Semester", 80);
	    column.setEditor(new CellEditor(new TextField<String>()));
	    configs.add(column);

	    column = new ColumnConfig("type", "Type", 80);
	    column.setEditor(getCombo(new String[]{"science", "non-science"}));
	    configs.add(column);
	    
	    column = new ColumnConfig("total_time", "Total Time(s)", 80);
	    column.setEditor(new CellEditor(new TextField<String>()));
	    configs.add(column);

	    column = new ColumnConfig("PSC_time", "PSC Time(s)", 80);
	    column.setEditor(new CellEditor(new TextField<String>()));
	    configs.add(column);

	    column = new ColumnConfig("sem_time", "Max. Semester Time(s)", 130);
	    column.setEditor(new CellEditor(new TextField<String>()));
	    configs.add(column);

	    column = new ColumnConfig("grade", "Grade(s)", 80);
	    column.setEditor(new CellEditor(new TextField<String>()));
	    configs.add(column);

	    CheckColumnConfig checkColumn = new CheckColumnConfig("thesis", "Thesis?", 55);
	    checkColumn.setEditor(new CellEditor(new CheckBox()));
	    configs.add(checkColumn);
	    checkBoxes.add(checkColumn);
	    
	    checkColumn = new CheckColumnConfig("complete", "Complete?", 65);
	    checkColumn.setEditor(new CellEditor(new CheckBox()));
	    configs.add(checkColumn);
	    checkBoxes.add(checkColumn);
	    
	    checkColumn = new CheckColumnConfig("ignore_grade", "Ignore Grade?", 85);
	    checkColumn.setEditor(new CellEditor(new CheckBox()));
	    configs.add(checkColumn);
	    checkBoxes.add(checkColumn);
	    
	    return new ColumnModel(configs);
	}
}
