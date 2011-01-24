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

// An abstract class supporting list of users associated with a project (Investigators & Friends)

public abstract class UserProjectExplorer extends Explorer {
	
	private List<ColumnConfig> configs;
	private Integer project_id;
	private UserForm addUser;

	// NOTE: we are getting the column definitions passed in from the child class
	public UserProjectExplorer(String rootType, String baseURL, ColumnType[] columnTypes) {
		super(baseURL, "", new GenericType(rootType, columnTypes));
		setShowColumnsMenu(false);
		setAutoHeight(true);
		setCreateFilterToolBar(false);
		initLayout(initColumnModel(columnTypes), true);
		viewItem.setVisible(false);
		initToolBar();
	}	
	
	// we need to override Explorer's initColumnModel because we are being passed in our columnTypes
	private ColumnModel initColumnModel(ColumnType[] columnTypes) {
		configs = new ArrayList<ColumnConfig>();
		CheckColumnConfig checkColumn;
		for (ColumnType ct : columnTypes) {
			if (ct.getClasz() != Boolean.class) {
			    configs.add(new PeriodColConfig(ct.getId(), ct.getName(), ct.getLength(), ct.getClasz()));
			} else {
				checkColumn = new CheckColumnConfig(ct.getId(), ct.getName(), ct.getLength());
			    checkColumn.setEditor(new CellEditor(new CheckBox()));
			    configs.add(checkColumn);
			    checkBoxes.add(checkColumn);
			}
		}
	    return new ColumnModel(configs);
	}	

	protected abstract void showAddWindow();
	
	protected void initToolBar() {
		final PagingToolBar pagingToolBar = new PagingToolBar(50);
		final TextField<String> pages = new TextField<String>();
		pages.setWidth(30);
		pages.setValue("50");
		pages.addKeyListener(new KeyListener() {
			public void componentKeyPress(ComponentEvent e) {
				if (e.getKeyCode() == 13) {
					int page_size = Integer.valueOf(pages.getValue()).intValue();
					pagingToolBar.setPageSize(page_size);
					setPageSize(page_size);
					loadData();
				}
			}
		});
		pages.setTitle("Page Size");
		pagingToolBar.add(pages);
		setBottomComponent(pagingToolBar);
		pagingToolBar.bind(loader);
		
		ToolBar toolBar = new ToolBar();
		setTopComponent(toolBar);
		
		
		
		addItem = new Button("Add");
		toolBar.add(addItem);
		addItem.setToolTip("Add a new row.");
		addItem.addSelectionListener(new SelectionListener<ButtonEvent>() {
	        @Override
	        public void componentSelected(ButtonEvent ce) {
	        	showAddWindow();
	        	addUser.getWindow().show();
				addUser.show();
	        }
	    });
		
		removeDialog = new Dialog();
		removeDialog.setHeading("Confirmation");
		removeDialog.addText("Remove record?");
		removeDialog.setButtons(Dialog.YESNO);
		removeDialog.setHideOnButtonClick(true);
		removeApproval = removeDialog.getButtonById(Dialog.YES);
		removeApproval.addSelectionListener(new SelectionListener<ButtonEvent>() {
			@Override
			public void componentSelected(ButtonEvent ce) {
				Double id = grid.getSelectionModel().getSelectedItem().get("id");
				//GWT.log(rootURL);
				JSONRequest.delete(rootURL + "/" + id.intValue(),
						new JSONCallbackAdapter() {
							public void onSuccess(JSONObject json) {
								store.remove(grid.getSelectionModel()
										.getSelectedItem());
							}
						});
			}
		});	
		removeDialog.hide();

	
		removeItem = new Button("Delete");
		toolBar.add(removeItem);
		removeItem.setToolTip("Delete a row.");
		removeItem.addSelectionListener(new SelectionListener<ButtonEvent>() {
			@Override
			public void componentSelected(ButtonEvent be) {
				removeDialog.show();
			}
		});
		
		toolBar.add(new FillToolItem());
		toolBar.add(new SeparatorToolItem());

		saveItem = new Button("Save");
		toolBar.add(saveItem);

		// Commit outstanding changes to the server.
		saveItem.addSelectionListener(new SelectionListener<ButtonEvent>() {
			@Override
			public void componentSelected(ButtonEvent be) {
				setCommitState(true);
				store.commitChanges();
				setCommitState(false);
				loadData();
				grid.getView().refresh(true);
			}
		});
	}
	
	public void loadProject(Integer project_id) {
		this.setProject_id(project_id);
		String url = getRootURL() + "?project_id=" + project_id;
		RequestBuilder builder = new RequestBuilder(RequestBuilder.GET, url);
		DynamicHttpProxy<BasePagingLoadResult<BaseModelData>> proxy = getProxy();
		proxy.setBuilder(builder);
		loadData();
	}

	public void setProject_id(Integer project_id) {
		this.project_id = project_id;
	}

	public Integer getProject_id() {
		return project_id;
	}

	public void setAddUser(UserForm addUser) {
		this.addUser = addUser;
	}

	public UserForm getAddUser() {
		return addUser;
	}

}
