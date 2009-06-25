package edu.nrao.dss.client;

import java.util.List;
import java.util.ArrayList;

import com.extjs.gxt.ui.client.Style.HorizontalAlignment;
import com.extjs.gxt.ui.client.data.BaseModelData;
import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.CheckBox;
import com.extjs.gxt.ui.client.widget.form.DateField;
import com.extjs.gxt.ui.client.widget.form.NumberField;
import com.extjs.gxt.ui.client.widget.form.SimpleComboBox;
import com.extjs.gxt.ui.client.widget.form.TextField;
import com.extjs.gxt.ui.client.widget.form.ComboBox.TriggerAction;
import com.extjs.gxt.ui.client.widget.grid.CellEditor;
import com.extjs.gxt.ui.client.widget.grid.CheckColumnConfig;
import com.extjs.gxt.ui.client.widget.grid.ColumnConfig;
import com.extjs.gxt.ui.client.widget.grid.ColumnModel;
import com.extjs.gxt.ui.client.widget.grid.EditorGrid;
import com.extjs.gxt.ui.client.widget.layout.FlowLayout;
import com.extjs.gxt.ui.client.widget.toolbar.ToolBar;
import com.google.gwt.core.client.GWT;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.i18n.client.NumberFormat;

public class ProjectExplorer extends ContentPanel {
	public ProjectExplorer() {
		initLayout();
	}
	
	public void initLayout() {
		setLayout(new FlowLayout(10));

	    List<ColumnConfig> configs = new ArrayList<ColumnConfig>();

	    ColumnConfig column = new ColumnConfig();
	    column.setId("name");
	    column.setHeader("Common Name");
	    column.setWidth(220);

	    TextField<String> text = new TextField<String>();
	    text.setAllowBlank(false);
	    column.setEditor(new CellEditor(text));
	    configs.add(column);

	    final SimpleComboBox<String> combo = new SimpleComboBox<String>();
	    combo.setForceSelection(true);
	    combo.setTriggerAction(TriggerAction.ALL);
	    combo.add("Shade");
	    combo.add("Mostly Shady");
	    combo.add("Sun or Shade");
	    combo.add("Mostly Sunny");
	    combo.add("Sunny");

	    CellEditor editor = new CellEditor(combo) {
	      @Override
	      public Object preProcessValue(Object value) {
	        if (value == null) {
	          return value;
	        }
	        return combo.findModel(value.toString());
	      }

	      @Override
	      public Object postProcessValue(Object value) {
	        if (value == null) {
	          return value;
	        }
	        return ((ModelData) value).get("value");
	      }
	    };

	    column = new ColumnConfig();
	    column.setId("light");
	    column.setHeader("Light");
	    column.setWidth(130);
	    column.setEditor(editor);
	    configs.add(column);

	    column = new ColumnConfig();
	    column.setId("price");
	    column.setHeader("Price");
	    column.setAlignment(HorizontalAlignment.RIGHT);
	    column.setWidth(70);
	    column.setNumberFormat(NumberFormat.getCurrencyFormat());
	    column.setEditor(new CellEditor(new NumberField()));

	    configs.add(column);

	    DateField dateField = new DateField();
	    dateField.getPropertyEditor().setFormat(DateTimeFormat.getFormat("MM/dd/y"));

	    column = new ColumnConfig();
	    column.setId("available");
	    column.setHeader("Available");
	    column.setWidth(95);
	    column.setEditor(new CellEditor(dateField));
	    column.setDateTimeFormat(DateTimeFormat.getMediumDateFormat());
	    configs.add(column);

	    CheckColumnConfig checkColumn = new CheckColumnConfig("indoor", "Indoor?", 55);
	    CellEditor checkBoxEditor = new CellEditor(new CheckBox());
	    checkColumn.setEditor(checkBoxEditor);
	    configs.add(checkColumn);

	    final ListStore<BaseModelData> store = new ListStore<BaseModelData>();

	    ColumnModel cm = new ColumnModel(configs);

	    final EditorGrid<BaseModelData> grid = new EditorGrid<BaseModelData>(store, cm);
	    grid.setAutoExpandColumn("name");
	    grid.setBorders(true);
	    grid.addPlugin(checkColumn);
	    add(grid);

	    ToolBar toolBar = new ToolBar();
	    Button add = new Button("Add");
	    add.addSelectionListener(new SelectionListener<ButtonEvent>() {

	      @Override
	      public void componentSelected(ButtonEvent ce) {
	    	  GWT.log("added", null);
	      }

	    });
	    toolBar.add(add);
	    setTopComponent(toolBar);
	    setButtonAlign(HorizontalAlignment.CENTER);
	    addButton(new Button("Reset", new SelectionListener<ButtonEvent>() {

	      @Override
	      public void componentSelected(ButtonEvent ce) {
	        //store.rejectChanges();
	      }
	    }));

	    addButton(new Button("Save", new SelectionListener<ButtonEvent>() {

	      @Override
	      public void componentSelected(ButtonEvent ce) {
	        //store.commitChanges();
	      }
	    }));
	}

}
