package edu.nrao.dss.client;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.extjs.gxt.ui.client.data.BaseModelData;
import com.extjs.gxt.ui.client.data.BasePagingLoadResult;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.ComponentEvent;
import com.extjs.gxt.ui.client.event.KeyListener;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.widget.Dialog;
import com.extjs.gxt.ui.client.widget.Window;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.CheckBox;
import com.extjs.gxt.ui.client.widget.form.SimpleComboBox;
import com.extjs.gxt.ui.client.widget.form.TextField;
import com.extjs.gxt.ui.client.widget.grid.CellEditor;
import com.extjs.gxt.ui.client.widget.grid.CheckColumnConfig;
import com.extjs.gxt.ui.client.widget.grid.ColumnConfig;
import com.extjs.gxt.ui.client.widget.grid.ColumnModel;
import com.extjs.gxt.ui.client.widget.toolbar.FillToolItem;
import com.extjs.gxt.ui.client.widget.toolbar.PagingToolBar;
import com.extjs.gxt.ui.client.widget.toolbar.SeparatorToolItem;
import com.extjs.gxt.ui.client.widget.toolbar.ToolBar;
import com.google.gwt.core.client.GWT;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.json.client.JSONObject;

public class InvestigatorExplorer extends UserProjectExplorer {

//	private Integer project_id;
//	private InvestigatorForm addInvest;
	
	public InvestigatorExplorer() {
		super("investigators", "/investigators", getColumnTypes(), getFields());
		// setup the widget used for adding a new investigator to the project
		setAddUser(new UserForm("Investigator", "users",
				new Window(), InvestigatorExplorer.this));
		setHeight(250);
		setWidth(600);
	}
	
	// JSON-fields
	protected static List<String> getFields() {
		ArrayList<String> fields = new ArrayList<String>();
		fields.add("name");
		fields.add("pi");
		fields.add("contact");
		fields.add("remote");
		fields.add("observer");
		fields.add("project_id");
		fields.add("user_id");
		return fields;
	}
	
	// Columns in the Explorer - first params have to match list in getFields
	protected static ColumnType[] getColumnTypes() {
		ColumnType[] columnTypes = {
	    	new ColumnType("name",              "Name",            250,false, DisplayField.class),
	    	new ColumnType("pi",                "PI",               65,false, Boolean.class),
	    	new ColumnType("contact",           "Contact",          65,false, Boolean.class),
	    	new ColumnType("remote",            "Remote",           65,false, Boolean.class),
	    	new ColumnType("observer",          "Observer(s)",      65,false, Boolean.class)
	    };
		return columnTypes;
	}	
}
