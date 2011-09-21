// Copyright (C) 2011 Associated Universities, Inc. Washington DC, USA.
// 
// This program is free software; you can redistribute it and/or modify
// it under the terms of the GNU General Public License as published by
// the Free Software Foundation; either version 2 of the License, or
// (at your option) any later version.
// 
// This program is distributed in the hope that it will be useful, but
// WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
// General Public License for more details.
// 
// You should have received a copy of the GNU General Public License
// along with this program; if not, write to the Free Software
// Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.
// 
// Correspondence concerning GBT software should be addressed as follows:
//       GBT Operations
//       National Radio Astronomy Observatory
//       P. O. Box 2
//       Green Bank, WV 24944-0002 USA

package edu.nrao.dss.client.widget.explorers;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.Style.HorizontalAlignment;
import com.extjs.gxt.ui.client.data.BaseModelData;
import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.widget.form.CheckBox;
import com.extjs.gxt.ui.client.widget.form.DateField;
import com.extjs.gxt.ui.client.widget.form.NumberField;
import com.extjs.gxt.ui.client.widget.form.SimpleComboBox;
import com.extjs.gxt.ui.client.widget.form.TextField;
import com.extjs.gxt.ui.client.widget.form.ComboBox.TriggerAction;
import com.extjs.gxt.ui.client.widget.grid.CellEditor;
import com.extjs.gxt.ui.client.widget.grid.ColumnConfig;
import com.extjs.gxt.ui.client.widget.grid.ColumnData;
import com.extjs.gxt.ui.client.widget.grid.Grid;
import com.extjs.gxt.ui.client.widget.grid.GridCellRenderer;
import com.google.gwt.core.client.GWT;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.i18n.client.NumberFormat;
import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONObject;

import edu.nrao.dss.client.data.OptionsFilter;
import edu.nrao.dss.client.util.JSONCallbackAdapter;
import edu.nrao.dss.client.util.JSONRequest;
import edu.nrao.dss.client.util.JSONRequestCache;
import edu.nrao.dss.client.widget.form.DateEditField;
import edu.nrao.dss.client.widget.form.DisplayField;
import edu.nrao.dss.client.widget.form.ScoreField;
import edu.nrao.dss.client.widget.form.SessionField;
import edu.nrao.dss.client.widget.form.TimeField;

// Should extend an intermediate class for duplicate code
// between SessionColConfig and PeriodColConfig
public class PeriodColConfig extends ColumnConfig {

	@SuppressWarnings("unchecked")
	public PeriodColConfig(String fName, String name, int width, Class clasz) {
		super(fName, name, width);
		
		this.clasz = clasz;

        if (clasz == Double.class) {
			doubleField();
		} else if (clasz == SessionField.class) {
			setSessionOptions();
		} else if (clasz == DateEditField.class) {
			dateField();
		} else if (clasz == DisplayField.class) {
			displayField();
		} else if (clasz == TimeField.class) {
			timeField();
		} else if (clasz == ScoreField.class) {
			scoreField();
		} else if (clasz == Boolean.class) {
			checkboxField();
		} else {
			textField();
		}
	};

	@SuppressWarnings("serial")
	public void setSessionOptions() {
		JSONRequestCache.get("/scheduler/sessions/options"
				, OptionsFilter.getDefaultState("session_handles")
				, new JSONCallbackAdapter() {
			@Override
			public void onSuccess(JSONObject json) {
				ArrayList<String> sess_handles = new ArrayList<String>();
				JSONArray sessions = json.get("session handles").isArray();
				for (int i = 0; i < sessions.size(); ++i) {
					sess_handles.add(sessions.get(i).toString().replace('"', ' ').trim());
				}
				typeField(sess_handles.toArray(new String[] {}));
			}
    	});
	}
	
	public void updateSessionHandles(JSONObject json) {
		SimpleComboBox<String> typeCombo = (SimpleComboBox<String>) getEditor().getField();
		typeCombo.removeAll();
		JSONArray sessions = json.get("session handles").isArray();
		for (int i = 0; i < sessions.size(); ++i) {
			typeCombo.add(sessions.get(i).toString().replace('"', ' ').trim());
		}
	}
	
	@SuppressWarnings("serial")
	public void updateSessionOptions() {
		JSONRequestCache.get("/scheduler/sessions/options"
				, new HashMap<String, Object>() {{
			    	  put("mode", "session_handles");
			        }}
				, new JSONCallbackAdapter() {
			@SuppressWarnings("unchecked")
			@Override
			public void onSuccess(JSONObject json) {
				updateSessionHandles(json);
			}
    	});
	}
	
	public void updateSessionOptions(HashMap<String, Object> state) {
		state.put("mode", "project_codes");
		JSONRequestCache.get("/scheduler/sessions/options"
				, state
				, new JSONCallbackAdapter() {
			@SuppressWarnings("unchecked")
			@Override
			public void onSuccess(JSONObject json) {
				updateSessionHandles(json);
			}
    	});
	}
	
	private NumberField createDoubleField() {
		NumberField field = new NumberField();
		field.setPropertyEditorType(Double.class);
		return field;
	}

	private void checkboxField() {
		setEditor(new CellEditor(new CheckBox()));
	}
	
	private void dateField() {
		setEditor(new CellEditor(new DateField()){
			@Override
			public Object preProcessValue(Object value) {
				if (value == null) {
					return null;
				}
				//return DateFormat.getDateInstance().parse(value.toString());
				String str = value.toString();
				DateTimeFormat fmt = DateTimeFormat.getFormat("yyyy-MM-dd");
				return fmt.parse(str);
			}

			@Override
			public Object postProcessValue(Object value) {
				if (value == null) {
					return null;
				}
				DateTimeFormat fmt = DateTimeFormat.getFormat("yyyy-MM-dd");
				Date d = (Date) value;
				return fmt.format(d);
			}
		});
	}
	
	private void scoreField() {
		setAlignment(HorizontalAlignment.RIGHT);

		setNumberFormat(NumberFormat.getFormat("0"));
		setRenderer(new GridCellRenderer<BaseModelData>() {
			public Object render(BaseModelData model, String property,
					ColumnData config, int rowIndex, int colIndex,
					ListStore<BaseModelData> store, Grid<BaseModelData> grid) {
				if (model.get(property) != null) {
					String retval = model.get(property).toString();
					if (4 < retval.length()) {
						retval = retval.substring(0,4);
					}
					return retval;
				} else {
					return "";
				}
			}
		});
	}
	
	private void displayField() {
		setAlignment(HorizontalAlignment.RIGHT);

		setRenderer(new GridCellRenderer<BaseModelData>() {
			public Object render(BaseModelData model, String property,
					ColumnData config, int rowIndex, int colIndex,
					ListStore<BaseModelData> store, Grid<BaseModelData> grid) {
				if (model.get(property) != null) {
					return model.get(property).toString();
				} else {
					return "";
				}
			}
		});
	}
	private void doubleField() {
		NumberField field = createDoubleField();

		setAlignment(HorizontalAlignment.RIGHT);
		setEditor(new CellEditor(field) {
			@Override
			public Object preProcessValue(Object value) {
				if (value == null) {
					return null;
				}
				return Double.valueOf(value.toString());
			}

			@Override
			public Object postProcessValue(Object value) {
				if (value == null) {
					return null;
				}
				return value.toString();
			}
		});

		setNumberFormat(NumberFormat.getFormat("0"));
		setRenderer(new GridCellRenderer<BaseModelData>() {
			public Object render(BaseModelData model, String property,
					ColumnData config, int rowIndex, int colIndex,
					ListStore<BaseModelData> store, Grid<BaseModelData> grid) {
				if (model.get(property) != null) {
					return model.get(property).toString();
				} else {
					return "";
				}
			}
		});
	}

	/** Construct an editable field supporting free-form text. */
	private void textField() {
		setEditor(new CellEditor(new TextField<String>()));
	}

	// Note: allows entries outside list of options
	private SimpleComboBox<String> createSimpleComboBox(String[] options) {
		SimpleComboBox<String> typeCombo = new SimpleComboBox<String>();
		typeCombo.setTriggerAction(TriggerAction.ALL);

		for (String o : options) {
			typeCombo.add(o);
		}

		return typeCombo;
	}
	
	private void typeField(String[] options) {
		final SimpleComboBox<String> typeCombo = createSimpleComboBox(options);

		setEditor(new CellEditor(typeCombo) {
			@Override
			public Object preProcessValue(Object value) {
				if (value == null) {
					return value;
				}
				return typeCombo.findModel(value.toString());
			}

			@Override
			public Object postProcessValue(Object value) {
				if (value == null) {
					return value;
				}
				return ((ModelData) value).get("value");
			}
		});
	}
	
	private void timeField() {
		TextField<String> field = new TextField<String>();
		field.setRegex("[0-2]\\d:(00|15|30|45)");

		setAlignment(HorizontalAlignment.RIGHT);
		setEditor(new CellEditor(field) {
			@Override
			public Object preProcessValue(Object value) {
				if (value == null) {
					return value;
				}
				return value;
			}

			@Override
			public Object postProcessValue(Object value) {
				if (value == null) {
					return value;
				}
				return value;
			}
		});
	}

	@SuppressWarnings("unchecked")
	protected final Class clasz;
}
