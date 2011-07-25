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

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;

import com.extjs.gxt.ui.client.Style.Scroll;
import com.extjs.gxt.ui.client.data.BaseModelData;
import com.extjs.gxt.ui.client.data.LoadEvent;
import com.extjs.gxt.ui.client.data.Loader;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.LoadListener;
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
import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONObject;

import edu.nrao.dss.client.Schedule;
import edu.nrao.dss.client.data.PeriodType;
import edu.nrao.dss.client.util.JSONCallbackAdapter;
import edu.nrao.dss.client.util.JSONRequest;
import edu.nrao.dss.client.widget.form.DateEditField;
import edu.nrao.dss.client.widget.form.DisplayField;
import edu.nrao.dss.client.widget.form.ScoreField;
import edu.nrao.dss.client.widget.form.SessionField;
import edu.nrao.dss.client.widget.form.TimeField;

public class PeriodExplorer extends Explorer {
	public PeriodExplorer() {
		super("/scheduler/periods/UTC", "", new PeriodType(columnTypes));
		setShowColumnsMenu(false);
		setAutoHeight(true);
		setCreateFilterToolBar(false);
		setLoadDataInitially(false);
		initLayout(initColumnModel(), true);
<<<<<<< HEAD
		// Setting auto height for the PeriodExplorer only because
		// it is contained in the schedule tab.
		setAutoHeight(true);
		setScrollMode(Scroll.AUTOY);
		getGrid().setAutoHeight(true);
		
=======
		// make sure every time data is loaded, scores are recalculated
		loader.addListener(Loader.Load, new LoadListener() {
    	      @Override
		      public void loaderLoad(LoadEvent le) {
		    	  getScores();
    	      }    
		});	
>>>>>>> d381a5bf0ab7db35872ff8802bc5b96f316e3129
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
				String state = getGrid().getSelectionModel().getSelectedItem().get("state");
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
		getSaveItem().addSelectionListener(new SelectionListener<ButtonEvent>() {
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
		getRemoveApproval().addSelectionListener(new SelectionListener<ButtonEvent>() {
			@Override
			public void componentSelected(ButtonEvent be) {
				// we don't need to update scores when removing a period
	            schedule.updateCalendar();
			}
		});
		store.addStoreListener(new StoreListener<BaseModelData>() {
			@Override
			public void handleEvent(StoreEvent<BaseModelData> se) {
				List<BaseModelData> data = (List<BaseModelData>) se.getStore().getModels();
				schedule.vacancyControl.setVacancyOptions(data);
				schedule.scheduleControl.setScheduleSummary(data);
			}
		});
	}
	
	public void setDefaultDate(Date date) {
		DateTimeFormat fmt = DateTimeFormat.getFormat("yyyy-MM-dd");
		defaultDate = fmt.format(date);
	}
	
	public ColumnConfig getSessionConfig() {
		return configs.get(0);
	}

	// makes a request to Antioch for periods' scores (current, historical, and MOCs)
	private void getScores() {
		      
        // ex: /update_periods?pids=9346&pids=9351
        String params = new String();
        String sep, idStr, idStr2, url;
        Float idFlt;
        int idInt;
        // get the ID for each period displayed in the Period Explorer
        // and build up the params needed for the request
        for(BaseModelData m  : store.getModels()){
           sep = (params.length() == 0) ? "?" : "&";
           // id : string -> string requires string -> float -> int -> string.  WTF
           idStr = m.get("id").toString();
           idFlt = Float.parseFloat(idStr);
           idInt = idFlt.intValue();
           idStr2 = Integer.toString(idInt);
           params = params + sep + "pids=" + idStr2;
        }
        url = "/update_periods"+params;
        JSONRequest.get(url, new JSONCallbackAdapter() {
          public void onSuccess(JSONObject json) {
              setScores(json);
          }   
        });   
	}
	   

	// processes the result from the request for scores from Antioch -
	// that means displaying them!
	private void setScores(JSONObject json) {
	      // ex: {"scores":[{"pid":8902, "score":5.7115545, "hscore" : {"Just":0},"moc":{"Nothing":null}}, ...]}
	      int id, idInt;
	      Float idFlt;
	      String idStr;
	      double cscore, sscore;
	      BaseModelData bmd;
	      JSONObject jsonScore = new JSONObject();

	      JSONArray scores = new JSONArray();
	      scores = json.get("scores").isArray();
	      	      
	      for (int i = 0; i < scores.size(); i++) {
	         jsonScore = scores.get(i).isObject();
	         // we need the id to know which row to update
	         id = (int) jsonScore.get("pid").isNumber().doubleValue();
	         // we'll always be update the current score
	         cscore = jsonScore.get("score").isNumber().doubleValue();
	         // go through each row in the Explorer until we find the period
	         /// we want to update
	         for(BaseModelData m  : store.getModels()){
	             idStr = m.get("id").toString();
	             idFlt = Float.parseFloat(idStr);
	             idInt = idFlt.intValue();
	             if (id == idInt) {
	                 // this is the period we wish to update            
	                 m.set("cscore", cscore);
	                 // now, see if we need to update the historical score
	                 if (jsonScore.get("hscore").isObject().containsKey("Just")) {
	            	     sscore = jsonScore.get("hscore").isObject().get("Just").isNumber().doubleValue();
		               //GWT.log("Setting sscore: " + Integer.toString(id) + " to " + Double.toString(sscore));
		                 m.set("sscore", sscore);
	                 }
	                 // this finally gets the new values to appear for the user
	                 store.update(m);
	             }
	         }   
	      }
	}
	
	private List<ColumnConfig> configs;

	private static final ColumnType[] columnTypes = {
       	new ColumnType("handle",                "Session (Project) VP", 220, false, SessionField.class),
       	new ColumnType("stype",                 "T",                     20, false, DisplayField.class),
       	new ColumnType("state",                 "S",                     20, false, DisplayField.class),
        new ColumnType("date",                  "Day",                   70, false, DateEditField.class),
        new ColumnType("time",                  "Time",                  40, false, TimeField.class),
        new ColumnType("lst",                   "LST",                   55, false, DisplayField.class),
        new ColumnType("duration",              "Duration",              55, false, Double.class),
        new ColumnType("sscore",                "Hist Score",            65, false, ScoreField.class),
        new ColumnType("cscore",                "Curr Score",            65, false, ScoreField.class),
        new ColumnType("receivers",             "Rcvrs",                 40, false, String.class),
       	new ColumnType("not_billable",          "Not Bill",              45, false, Double.class),
	};
}
