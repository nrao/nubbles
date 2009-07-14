package edu.nrao.dss.client;

import java.util.Date;
import java.util.List;
import java.util.ArrayList;

import com.extjs.gxt.ui.client.widget.form.CheckBox;
import com.extjs.gxt.ui.client.widget.form.DateField;
import com.extjs.gxt.ui.client.widget.form.TextField;
import com.extjs.gxt.ui.client.widget.grid.CellEditor;
import com.extjs.gxt.ui.client.widget.grid.CheckColumnConfig;
import com.extjs.gxt.ui.client.widget.grid.ColumnConfig;
import com.extjs.gxt.ui.client.widget.grid.ColumnModel;
import com.google.gwt.core.client.GWT;
import com.google.gwt.i18n.client.DateTimeFormat;

public class PeriodExplorer extends Explorer {
	public PeriodExplorer() {
		super("/periods", new PeriodType());
		initLayout(initColumnModel());
	}
	
	private ColumnModel initColumnModel() {
		
		List<ColumnConfig> configs = new ArrayList<ColumnConfig>();

	    ColumnConfig column = new ColumnConfig("id", "Id", 100);
	    column.setEditor(new CellEditor(new TextField<String>()));
	    configs.add(column);

	    column = new ColumnConfig("start", "Start", 200);
	    column.setEditor(new CellEditor(new TextField<String>()));
	    configs.add(column);
	    
	    // TBF: here's how to get a date picker working - but shit, we need date times!   
//	    column.setEditor(new CellEditor(new DateField()){
//			@Override
//			public Object preProcessValue(Object value) {
//				if (value == null) {
//					return null;
//				}
//				//return DateFormat.getDateInstance().parse(value.toString());
//				String str = value.toString();
//				GWT.log(str, null);
//				DateTimeFormat fmt = DateTimeFormat.getFormat("yyyy-MM-dd HH:mm:ss");
//				return fmt.parse(str);
//			}
//
//			@Override
//			public Object postProcessValue(Object value) {
//				if (value == null) {
//					return null;
//				}
//				DateTimeFormat fmt = DateTimeFormat.getFormat("yyyy-MM-dd HH:mm:ss");
//				Date d = (Date) value;
//				return fmt.format(d);
//			}
//		} );
	    
	    column = new ColumnConfig("duration", "Duration", 80);
	    column.setEditor(new CellEditor(new TextField<String>()));
	    configs.add(column);

	    // TBF: other columns
	    
	    // how to do a check box:
//	    CheckColumnConfig checkColumn = new CheckColumnConfig("thesis", "Thesis?", 55);
//	    checkColumn.setEditor(new CellEditor(new CheckBox()));
//	    configs.add(checkColumn);
//	    checkBoxes.add(checkColumn);
	    
	    return new ColumnModel(configs);
	}
}
