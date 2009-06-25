package edu.nrao.dss.client;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.extjs.gxt.ui.client.Style.SelectionMode;
import com.extjs.gxt.ui.client.data.BaseModelData;
import com.extjs.gxt.ui.client.data.BasePagingLoadConfig;
import com.extjs.gxt.ui.client.data.BasePagingLoadResult;
import com.extjs.gxt.ui.client.data.BasePagingLoader;
import com.extjs.gxt.ui.client.data.DataField;
import com.extjs.gxt.ui.client.data.DataReader;
import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.data.PagingLoader;
import com.extjs.gxt.ui.client.event.BaseEvent;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.GridEvent;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.SelectionChangedEvent;
import com.extjs.gxt.ui.client.event.SelectionChangedListener;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.store.StoreEvent;
import com.extjs.gxt.ui.client.store.StoreListener;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.Field;
import com.extjs.gxt.ui.client.widget.form.SimpleComboValue;
import com.extjs.gxt.ui.client.widget.grid.ColumnConfig;
import com.extjs.gxt.ui.client.widget.grid.ColumnModel;
import com.extjs.gxt.ui.client.widget.grid.EditorGrid;
import com.extjs.gxt.ui.client.widget.grid.GridSelectionModel;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.extjs.gxt.ui.client.widget.toolbar.FillToolItem;
import com.extjs.gxt.ui.client.widget.toolbar.PagingToolBar;
import com.extjs.gxt.ui.client.widget.toolbar.SeparatorToolItem;
import com.extjs.gxt.ui.client.widget.toolbar.ToolBar;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONValue;
import com.google.gwt.user.client.Window;

import edu.nrao.dss.client.view.UndefaultedValuesDialog;

public class SessionExplorer extends ContentPanel {
	public SessionExplorer() {
		initLayout();
	}
	
	public void loadData() {
		loader.load(0, 50);
	}
	
	/** Construct the grid. */
	@SuppressWarnings("unchecked")
	private void initLayout() {
		setHeaderVisible(false);
		setLayout(new FitLayout());
		commitState = false;

		RequestBuilder builder = new RequestBuilder(RequestBuilder.GET, "/sessions");

		DataReader reader = new PagingJsonReader<BasePagingLoadConfig>(new SessionType(rows.getColumnDefinition()));
		proxy  = new DynamicHttpProxy<BasePagingLoadResult<BaseModelData>>(builder);
		loader = new BasePagingLoader<BasePagingLoadResult<BaseModelData>>(proxy, reader);  
		loader.setRemoteSort(true);

		store = new ListStore<BaseModelData>(loader);
		
		List<ColumnConfig> col = new ArrayList<ColumnConfig>();
		ColumnConfig column = new ColumnConfig();
		column.setId("col1");
		column.setHeader("column 1");
		column.setWidth(80);
		col.add(column);
		
		column = new ColumnConfig();
		column.setId("col1");
		column.setHeader("column 1");
		column.setWidth(80);
		col.add(column);
		
		grid  = new EditorGrid<BaseModelData>(store, new ColumnModel(col));//rows.getColumnModel(null));  // selection.getColumn()));
		GridSelectionModel<BaseModelData> selectionModel = new GridSelectionModel<BaseModelData>();
		selectionModel.setSelectionMode(SelectionMode.MULTI);
		grid.setSelectionModel(selectionModel);
		add(grid);
		grid.setBorders(true);

		//toolBar = new PagingToolBar(50);
		//toolBar.add(new FilterItem(SessionExplorer.this));
		//setBottomComponent(toolBar);
		//toolBar.bind(loader);
        
        //initToolBar();
		//initListeners();

		loadData();
	}

	private void initListeners() {
		grid.addListener(Events.AfterEdit, new Listener<GridEvent<BaseModelData>>() {
			public void handleEvent(GridEvent<BaseModelData> ge) {
				Object value = ge.getRecord().get(ge.getProperty());
				for (BaseModelData model : grid.getSelectionModel()
						.getSelectedItems()) {
					store.getRecord(model).set(ge.getProperty(), value);
				}
			}
		});

		store.addStoreListener(new StoreListener<BaseModelData>() {
			@Override
			public void storeUpdate(StoreEvent<BaseModelData> se) {
				save(se.getModel());
			}
		});
	}
	
	private void save(ModelData model) {
		if (!commitState) {
			return;
		}
        ArrayList<String> keys   = new ArrayList<String>();
        ArrayList<String> values = new ArrayList<String>();

        keys.add("_method");
        values.add("put");

        ArrayList<String> undefaulted = new ArrayList<String>();
        Map<String, Object> map = model.getProperties();
        for (String name : model.getPropertyNames()) {
        	Object value = model.get(name);
            if (value != null && !rows.isColumnDefault(name, map)) {
                keys.add(name);
                values.add(value.toString());
                if (rows.hasColumnDefault(name)) {
                	undefaulted.add(name);
                }
            }
        }
        if (undefaulted.isEmpty()) {
        	saveModel(model, keys, values);
        } else {
        	new UndefaultedValuesDialog(this, undefaulted, model, keys, values);
        }
	}
	
	public void saveModel(ModelData model, ArrayList<String> keys, ArrayList<String> values) {
        JSONRequest.post("/sessions/" + ((Number) model.get("id")).intValue(),
        		         keys.toArray(new String[]{}),
        		         values.toArray(new String[]{}),
        		         null);
	}

	public void createNewSessionRow(RowType row, HashMap<String, Object> fields) {
	    if (fields == null) {
	        fields = new HashMap<String, Object>();
	    }
        row.populateDefaultValues(fields);
	    addSession(fields);

	    setColumnHeaders(row.getFieldNames());
	}

	private void setColumnHeaders(List<String> headers) {
		setColumnHeaders(headers, true);
	}
	
	private void setColumnHeaders(List<String> headers, Boolean invert) {
		Boolean hidden;
		int count = grid.getColumnModel().getColumnCount();
        for (int i = 1; i < count; ++i) {
            String column_id = grid.getColumnModel().getColumnId(i);
            if (invert == true) {
            	hidden = !headers.contains(column_id);
            } else {
            	hidden = headers.contains(column_id);
            }
            grid.getColumnModel().getColumnById(column_id).setHidden(hidden);
        }
        
        store.addStoreListener(new StoreListener<BaseModelData>() {
            @Override
            public void storeUpdate(StoreEvent<BaseModelData> se) {
				save(se.getModel());
            }
            
            @Override
            public void storeDataChanged(StoreEvent<BaseModelData> se) {
            }
        });
        grid.getView().refresh(true);
    }

	@SuppressWarnings("unchecked")
	public HashMap<String, Object> populateSessionFields(
			List<String> sFields, ArrayList<Field> fFields) {
		HashMap<String, Object> retval = new HashMap<String, Object>();
		for (Field f : fFields) {
			Object value = f.getValue();
			if (value instanceof SimpleComboValue) {
				retval.put(f.getFieldLabel(), ((SimpleComboValue) value).getValue());
			} else {
				retval.put(f.getFieldLabel(), value);
			}
		}
		return retval;
	}
	
	/*
	private void addMenuItems(Menu addMenu) {
		for (final RowType row : rows.getAllRows()) {
		    String   name = row.getName() + (row.hasRequiredFields() ? "..." : "");
		    MenuItem mi   = new MenuItem(name, new SelectionListener<ComponentEvent>() {
		        @Override
		        public void componentSelected(ComponentEvent ce) {
		            if (row.hasRequiredFields()) {
		                new RequiredFieldsDialog(SessionExplorer.this, row);
		            } else {
		                createNewSessionRow(row, null);
		            }
		        }
		    });
		    addMenu.add(mi);
		}
	}
	*/
		
	@SuppressWarnings("unchecked")
	private void initToolBar() {
		ToolBar toolBar = new ToolBar();
		setTopComponent(toolBar);

		Button addItem = new Button("Add");
		toolBar.add(addItem);
		addItem.setToolTip("Add a new session.");
		addItem.addSelectionListener(new SelectionListener<ButtonEvent>() {
	        @Override
	        public void componentSelected(ButtonEvent ce) {
	        	RowType row = rows.getAllRows().get(0);
	            if (row.hasRequiredFields()) {
	                new RequiredFieldsDialog(SessionExplorer.this, row);
	            } else {
	                createNewSessionRow(row, null);
	            }
	        }
	    });
		//Menu addMenu = new Menu();
		//addMenuItems(addMenu);
		//addItem.setMenu(addMenu);

		// TBF these sections should be separate functions like the add menu above
		final Button duplicateItem = new Button("Duplicate");
		toolBar.add(duplicateItem);
		duplicateItem.setToolTip("Copy a session.");
		duplicateItem.setEnabled(false);
		duplicateItem.addSelectionListener(new SelectionListener<ButtonEvent>() {
            @Override
            public void componentSelected(ButtonEvent be) {
                HashMap<String, Object> fields = (HashMap<String, Object>) grid
                        .getSelectionModel().getSelectedItem()
                        .getProperties();
                addSession(fields);
                grid.getView().refresh(true);
            }
        });

		Button removeItem = new Button("Delete");
		toolBar.add(removeItem);
		removeItem.setToolTip("Delete a session.");
		removeItem.addSelectionListener(new SelectionListener<ButtonEvent>() {
			@Override
			public void componentSelected(ButtonEvent be) {
				JSONRequest.delete("/sessions/"
						+ ((Number) grid.getSelectionModel().getSelectedItem()
								.get("id")).intValue(),
						new JSONCallbackAdapter() {
							public void onSuccess(JSONObject json) {
								store.remove(grid.getSelectionModel()
										.getSelectedItem());
							}
						});
			}
		});

		toolBar.add(new SeparatorToolItem());
		
		//FilterItem filterItem = new FilterItem(SessionExplorer.this);
		//toolBar.add(filterItem);

		toolBar.add(new FillToolItem());

		Button saveItem = new Button("Save");
		toolBar.add(saveItem);

		// Commit outstanding changes to the server.
		saveItem.addSelectionListener(new SelectionListener<ButtonEvent>() {
			@Override
			public void componentSelected(ButtonEvent be) {
				commitState = true;
				store.commitChanges();
				commitState = false;
			}
		});

		// Enable the "Duplicate" button only if there is a selection.
		grid.getSelectionModel().addSelectionChangedListener(
				new SelectionChangedListener() {
					@Override
					public void selectionChanged(SelectionChangedEvent se) {
					}
					@SuppressWarnings("unused")
                    public void handleEvent(BaseEvent e) {
                        //Window.alert(""+grid.getSelectionModel().getSelectionMode());
                        duplicateItem.setEnabled(!grid.getSelectionModel().getSelectedItems().isEmpty());
					}
				});
	}

	private void addSession(HashMap<String, Object> data) {
		
		JSONRequest.post("/sessions", data, new JSONCallbackAdapter() {
			@SuppressWarnings("unchecked")
			@Override
			public void onSuccess(JSONObject json) {

				BaseModelData model = new BaseModelData();	
				SessionType type = new SessionType(rows.getColumnDefinition());
				for (int i = 0; i < type.getFieldCount(); ++i) {
					DataField field = type.getField(i);
					String fName = field.getName();
					if (json.containsKey(fName)) {
						Class target_type = rows.getColumnDefinition().getClasz(fName);
						
						// Set model value dependent on data type
						JSONValue value = json.get(fName);
						if (value.isNumber() != null) {
							double numValue = value.isNumber().doubleValue();
							if (target_type == Integer.class) {
							    model.set(fName, (int) numValue);
							} else {
								model.set(fName, numValue);
							}
						} else if (value.isBoolean()!= null) {
							model.set(fName, value.isBoolean().booleanValue());
						} else if (value.isString() != null) {
							model.set(fName, value.isString().stringValue());
						} else {
							Window.alert("unknown JSON value type");
						}
					}
				}
				store.add(model);
				grid.getView().refresh(true);
			}
		});
	}
	
	public EditorGrid<BaseModelData> getGrid(){
		return grid;
	}

	public DynamicHttpProxy<BasePagingLoadResult<BaseModelData>> getProxy() {
		return proxy;
	}
	
	private final RowDefinition rows = new RowDefinition();

	private PagingToolBar toolBar;
	
	/** Provides basic spreadsheet-like functionality. */
	private EditorGrid<BaseModelData> grid;

	/** Use store.add() to remember dynamically created sessions. */
	private ListStore<BaseModelData> store;

	private DynamicHttpProxy<BasePagingLoadResult<BaseModelData>> proxy;
	
	/** Use loader.load() to refresh with the list of sessions on the server. */
	private PagingLoader<BasePagingLoadResult<BaseModelData>> loader;
	
	/** Flag for enforcing saves only on Save button press. **/
	private boolean commitState;
}
