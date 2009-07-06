package edu.nrao.dss.client;

import java.util.ArrayList;
import java.util.List;

import com.extjs.gxt.ui.client.widget.form.CheckBox;
import com.extjs.gxt.ui.client.widget.grid.CellEditor;
import com.extjs.gxt.ui.client.widget.grid.CheckColumnConfig;
import com.extjs.gxt.ui.client.widget.grid.ColumnConfig;
import com.extjs.gxt.ui.client.widget.grid.ColumnModel;

public class SessionExplorer extends Explorer {
	public SessionExplorer() {
		super("/sessions", new SessionType(columnTypes));
		initLayout(initColumnModel());
	}
	
	private ColumnModel initColumnModel() {
		configs = new ArrayList<ColumnConfig>();
		CheckColumnConfig checkColumn;
		for (ColumnType ct : columnTypes) {
			if (ct.getClasz() != null) {
			    configs.add(new SessionColConfig(ct.getId(), ct.getName(), ct.getLength(), ct.getClasz()));
			} else {
				checkColumn = new CheckColumnConfig(ct.getId(), ct.getName(), ct.getLength());
			    checkColumn.setEditor(new CellEditor(new CheckBox()));
			    configs.add(checkColumn);
			    checkBoxes.add(checkColumn);
			}
		}
	    
	    return new ColumnModel(configs);
	}
	
	public ColumnConfig getPcodeConfig() {
		return configs.get(0);
	}
	
	private List<ColumnConfig> configs;

    private static final ColumnType[] columnTypes = {
       	new ColumnType("pcode",          "Proj Code",      100, PCodeField.class),
        new ColumnType("name",           "Name",           100, String.class), 
        new ColumnType("source",         "Source",         100, String.class),
        new ColumnType("orig_ID",        "Orig ID",         50, Integer.class),
        new ColumnType("type",           "Type",            60, STypeField.class),
        new ColumnType("science",        "Science",         75, ScienceField.class),
        new ColumnType("PSC_time",       "PSC Time",        60, Double.class),
        new ColumnType("total_time",     "Total Time",      60, Double.class),
        new ColumnType("sem_time",       "Semester Time",  100, Double.class),
        new ColumnType("grade",          "Grade",           50, GradeField.class),
        new ColumnType("freq",           "Freq",            50, Double.class),
        new ColumnType("receiver",       "Receiver(s)",    100, String.class),
        new ColumnType("req_min",        "Req Min",         60, Double.class),
        new ColumnType("req_max",        "Req Max",         60, Double.class),
        new ColumnType("coord_mode",     "Coord Mode",      75, CoordModeField.class),
        new ColumnType("source_h",       "Source H",        75, TimeField.class),
        new ColumnType("source_v",       "Source V",        75, DegreeField.class),
        new ColumnType("between",        "Between",         60, Double.class),
       	new ColumnType("authorized",     "Authorized?",     70, null),
       	new ColumnType("enabled",        "Enabled?",        65, null),
       	new ColumnType("complete",       "Complete?",       65, null),
       	new ColumnType("backup",         "Backup?",         55, null),
    	};
}
