package edu.nrao.dss.client;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.extjs.gxt.ui.client.Style.SelectionMode;
import com.extjs.gxt.ui.client.data.BaseModelData;
import com.extjs.gxt.ui.client.data.BasePagingLoadResult;
import com.extjs.gxt.ui.client.data.BasePagingLoader;
import com.extjs.gxt.ui.client.data.DataField;
import com.extjs.gxt.ui.client.data.DataReader;
import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.data.ModelType;
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
import com.extjs.gxt.ui.client.widget.form.SimpleComboBox;
import com.extjs.gxt.ui.client.widget.form.ComboBox.TriggerAction;
import com.extjs.gxt.ui.client.widget.grid.CellEditor;
import com.extjs.gxt.ui.client.widget.grid.CheckColumnConfig;
import com.extjs.gxt.ui.client.widget.grid.ColumnModel;
import com.extjs.gxt.ui.client.widget.grid.EditorGrid;
import com.extjs.gxt.ui.client.widget.grid.GridSelectionModel;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.extjs.gxt.ui.client.widget.toolbar.FillToolItem;
import com.extjs.gxt.ui.client.widget.toolbar.PagingToolBar;
import com.extjs.gxt.ui.client.widget.toolbar.SeparatorToolItem;
import com.extjs.gxt.ui.client.widget.toolbar.ToolBar;
import com.google.gwt.core.client.GWT;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONValue;
import com.google.gwt.user.client.Window;

public class Explorer extends ContentPanel{
	public Explorer(String url, ModelType mType) {
		rootURL = url;
		modelType = mType;
	}
	
	@SuppressWarnings("unchecked")
	protected void initLayout(ColumnModel cm) {
		setHeaderVisible(false);
		setLayout(new FitLayout());
		commitState = false;
				
		RequestBuilder builder = new RequestBuilder(RequestBuilder.GET, rootURL);

		DataReader reader = new PagingJsonReader<BasePagingLoadResult>(modelType);
		proxy  = new DynamicHttpProxy<BasePagingLoadResult<BaseModelData>>(builder);
		loader = new BasePagingLoader<BasePagingLoadResult<BaseModelData>>(proxy, reader);  
		loader.setRemoteSort(true);

		store = new ListStore<BaseModelData>(loader);
		
	    grid  = new EditorGrid<BaseModelData>(store, cm);
		GridSelectionModel<BaseModelData> selectionModel = new GridSelectionModel<BaseModelData>();
		selectionModel.setSelectionMode(SelectionMode.MULTI);
		grid.setSelectionModel(selectionModel);
		addPlugins();
		add(grid);
		grid.setBorders(true);

		initListeners();
		initToolBar();
		loadData();
	}
	
	private void addPlugins() {
		for (CheckColumnConfig cb : checkBoxes) {
			grid.addPlugin(cb);
		}
	}
	
	public void loadData() {
		loader.load(0, 50);
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

        for (String name : model.getPropertyNames()) {
        	Object value = model.get(name);
            if (value != null) {
                keys.add(name);
                values.add(value.toString());
            }
        }
        JSONRequest.post(rootURL + "/" + ((Number) model.get("id")).intValue(),
		         keys.toArray(new String[]{}),
		         values.toArray(new String[]{}),
		         null);
	}
	
	private void initToolBar() {
		PagingToolBar pagingToolBar = new PagingToolBar(50);
		setBottomComponent(pagingToolBar);
		pagingToolBar.bind(loader);
		
		ToolBar toolBar = new ToolBar();
		setTopComponent(toolBar);

		Button addItem = new Button("Add");
		toolBar.add(addItem);
		addItem.setToolTip("Add a new row.");
		addItem.addSelectionListener(new SelectionListener<ButtonEvent>() {
	        @Override
	        public void componentSelected(ButtonEvent ce) {
	        	addRecord(new HashMap<String, Object>());
	        }
	    });
		
		final Button duplicateItem = new Button("Duplicate");
		toolBar.add(duplicateItem);
		duplicateItem.setToolTip("Copy a row.");
		duplicateItem.setEnabled(false);
		duplicateItem.addSelectionListener(new SelectionListener<ButtonEvent>() {
            @Override
            public void componentSelected(ButtonEvent be) {
                addRecord(new HashMap<String, Object>(grid.getSelectionModel()
            			.getSelectedItem().getProperties()));
                grid.getView().refresh(true);
            }
        });
		
		Button removeItem = new Button("Delete");
		toolBar.add(removeItem);
		removeItem.setToolTip("Delete a row.");
		removeItem.addSelectionListener(new SelectionListener<ButtonEvent>() {
			@Override
			public void componentSelected(ButtonEvent be) {
				Double id = grid.getSelectionModel().getSelectedItem().get("id");
				JSONRequest.delete(rootURL + "/" + id.intValue(),
						new JSONCallbackAdapter() {
							public void onSuccess(JSONObject json) {
								store.remove(grid.getSelectionModel()
										.getSelectedItem());
							}
						});
			}
		});

		FilterItem filter = new FilterItem(Explorer.this);
		toolBar.add(filter.getTextField());

		toolBar.add(new SeparatorToolItem());

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
				new SelectionChangedListener<BaseModelData>() {
					@Override
					public void selectionChanged(SelectionChangedEvent<BaseModelData> se) {
						duplicateItem.setEnabled(!grid.getSelectionModel().getSelectedItems().isEmpty());
					}
				});
	}
	
	private void addRecord(HashMap<String, Object> fields) {
		JSONRequest.post(rootURL, fields, new JSONCallbackAdapter() {
			@Override
			public void onSuccess(JSONObject json) {
				BaseModelData model = new BaseModelData();
				for (int i = 0; i < modelType.getFieldCount(); ++i) {
					DataField field = modelType.getField(i);
					String fName = field.getName();
					if (json.containsKey(fName)) {
						// Set model value dependent on data type
						JSONValue value = json.get(fName);
						if (value.isNumber() != null) {
							double numValue = value.isNumber().doubleValue();
							//TODO conditional for case to int
							//model.set(fName, (int) numValue);
							model.set(fName, numValue);
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
				grid.getSelectionModel().select(model, false);
			}
		});
	}
	
	protected CellEditor getCombo(String[] options) {
	    final SimpleComboBox<String> combo = new SimpleComboBox<String>();
	    combo.setForceSelection(true);
	    combo.setTriggerAction(TriggerAction.ALL);
	    for (String o : options) {
	    	combo.add(o);
	    }

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
	    return editor;
	}

	public DynamicHttpProxy<BasePagingLoadResult<BaseModelData>> getProxy() {
		return proxy;
	}
	
	public String getRootURL() {
		return rootURL;
	}
	
	/** Provides basic spreadsheet-like functionality. */
	private EditorGrid<BaseModelData> grid;

	/** Use store.add() to remember dynamically created records. */
	private ListStore<BaseModelData> store;
	
	/** Flag for enforcing saves only on Save button press. **/
	private boolean commitState;
	
	private ModelType modelType;

	protected List<CheckColumnConfig> checkBoxes = new ArrayList<CheckColumnConfig>();
	
	/** Use loader.load() to refresh with the list of records on the server. */
	protected PagingLoader<BasePagingLoadResult<BaseModelData>> loader;
	
	protected DynamicHttpProxy<BasePagingLoadResult<BaseModelData>> proxy;
	
	protected String rootURL;
}
