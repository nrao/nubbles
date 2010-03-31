package edu.nrao.dss.client;

import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.Set;

import com.extjs.gxt.ui.client.data.BaseModelData;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.store.StoreEvent;
import com.extjs.gxt.ui.client.store.StoreListener;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.form.CheckBox;
import com.extjs.gxt.ui.client.widget.grid.CellEditor;
import com.extjs.gxt.ui.client.widget.grid.CheckColumnConfig;
import com.extjs.gxt.ui.client.widget.grid.ColumnConfig;
import com.extjs.gxt.ui.client.widget.grid.ColumnModel;
import com.google.gwt.core.client.GWT;
import com.google.gwt.i18n.client.DateTimeFormat;

public class PeriodExplorer extends Explorer {
	public PeriodExplorer() {
		super("/periods/UTC", new PeriodType(columnTypes));
		initLayout(initColumnModel(), true);
		
	}
	
	private ColumnModel initColumnModel() {
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
	
	// override this method from the parent class so that we can enforce that only pending
	// periods are removed from this explorer
	protected void setRemoveItemListener() {
		removeItem.addSelectionListener(new SelectionListener<ButtonEvent>() {
			@Override
			public void componentSelected(ButtonEvent be) {
				// TODO: check state of period
				String state = grid.getSelectionModel().getSelectedItem().get("state");
				// if pending, do this
				if (state.compareTo("P") == 0) {
					removeDialog.show();
				} else {
				    // if not, let them know they can't
					MessageBox.alert("Not Allowed To Delete This Period", "Please use the Period Summary Dialog to delete a non-Pending Period.", null);
				}	
			}
		});
	}

	
	public void addButtonsListener(final Schedule schedule) {
		saveItem.addSelectionListener(new SelectionListener<ButtonEvent>() {
			@Override
			public void componentSelected(ButtonEvent be) {
	            schedule.updateCalendar();
			}
		});
		addItem.addSelectionListener(new SelectionListener<ButtonEvent>() {
			@Override
			public void componentSelected(ButtonEvent be) {
	            schedule.updateCalendar();
			}
		});
		removeApproval.addSelectionListener(new SelectionListener<ButtonEvent>() {
			@Override
			public void componentSelected(ButtonEvent be) {
	            schedule.updateCalendar();
			}
		});
		store.addStoreListener(new StoreListener<BaseModelData>() {
			@Override
			public void handleEvent(StoreEvent<BaseModelData> se) {
				GWT.log("hhhheeeeeeeelllllllllloooooo?", null);
	            //GWT.log(se.toString(), null);
				//List<? extends BaseModelData> xxxx = se.getStore().getModels();
				List<BaseModelData> data = (List<BaseModelData>) se.getStore().getModels();
//				for (BaseModelData datum : data) {
//					//BaseModelData first = data.get(0);
//					//Collection<String> names = datum.getPropertyNames();
//					//GWT.log(names.toString(), null);
//					//first.get
//					Double value = datum.get("duration");
//					GWT.log(value.getClass().toString(), null);
//					GWT.log(value.toString(), null);
//				}
				schedule.northNominee.setVacancyOptions(data);
	            //GWT.log(se.getStore().getModels().toString(), null);
	            //GWT.log(se.getStore().getModels().get(0).getClass().toString(), null);
			}
		});

/*		// TODO Instead of listening to each button, we should just listen to the store!
		store.addListener(Events.Update, new Listener<StoreEvent>() {
			public void handleEvent(StoreEvent se) {
				GWT.log("hhhheeeeeeeelllllllllloooooo?", null);
	            GWT.log(se.toString(), null);
			}
		});*/
	}
	
	public void setDefaultDate(Date date) {
		DateTimeFormat fmt = DateTimeFormat.getFormat("yyyy-MM-dd");
		defaultDate = fmt.format(date);
	}
	
	public ColumnConfig getSessionConfig() {
		return configs.get(0);
	}
	
	private List<ColumnConfig> configs;

	private static final ColumnType[] columnTypes = {
       	new ColumnType("handle",                "Session (Project) VP", 220, SessionField.class),
       	new ColumnType("stype",                 "T",                     20, DisplayField.class),
       	new ColumnType("state",                 "S",                     20, DisplayField.class),
        new ColumnType("date",                  "Day",                   70, DateEditField.class),
        new ColumnType("time",                  "Time",                  40, TimeField.class),
        new ColumnType("lst",                   "LST",                   55, DisplayField.class),
        new ColumnType("duration",              "Duration",              55, Double.class),
        new ColumnType("sscore",                "Hist Score",            65, ScoreField.class),
        new ColumnType("cscore",                "Curr Score",            65, ScoreField.class),
        new ColumnType("receivers",             "Rcvrs",                 40, String.class),
       	new ColumnType("not_billable",          "Not Bill",              45, Double.class),
       	new ColumnType("backup",                "Backup?",               55, Boolean.class),
	};
}
