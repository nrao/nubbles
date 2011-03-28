package edu.nrao.dss.client.widget.explorers;

import java.util.ArrayList;
import java.util.Stack;

import com.extjs.gxt.ui.client.data.BaseModelData;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.GridEvent;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.store.Record;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.grid.Grid;

public class UndoRedo {
	
	private Explorer explorer;
	private Stack<Record> undoStackRecords = new Stack<Record>();
	private Stack<ArrayList<Object>> undoStackData = new Stack<ArrayList<Object>>();
	private Stack<Record> redoStackRecords = new Stack<Record>();
	private Stack<ArrayList<Object>> redoStackData = new Stack<ArrayList<Object>>();
	private Button undoItem;
	private Button redoItem;
	
	
	public UndoRedo (Explorer explorer){
		this.explorer = explorer;
		this.undoItem = explorer.undoItem;
		this.redoItem = explorer.redoItem;
	}
	
	private void add2UndoStack(Record record, String property, Object value) {
		ArrayList<Object> data = new ArrayList<Object>();
		data.add(property);
		data.add(value);

		// Update undo stacks
		undoStackRecords.push(record);
		undoStackData.push(data);
		
		if(!undoItem.isEnabled()) {
			undoItem.enable();
		}
	}
	
	public void initListeners() {
		final Grid<BaseModelData> grid = explorer.getGrid();
		grid.addListener(Events.CellMouseUp, new Listener<GridEvent<BaseModelData>>() {

			@Override
			public void handleEvent(GridEvent<BaseModelData> ge) {
				BaseModelData m = ge.getModel();
				Record record   = explorer.store.getRecord(m);  
				String property = grid.getColumnModel().getColumnId(ge.getColIndex());
				try {
				//  We only use this event to handle checkboxes (bool)
				//  all other fields are handled below.
				Boolean value   = m.get(property);
			    add2UndoStack(record, property, !value);
			    } catch (ClassCastException e) {
					
				}
			}
		});
		grid.addListener(Events.BeforeEdit, new Listener<GridEvent<BaseModelData>>() {

			@Override
			public void handleEvent(GridEvent<BaseModelData> ge) {
				Record record   = ge.getRecord();
				String property = ge.getProperty();
				Object value    = record.get(property);
				add2UndoStack(record, property, value);
				
			}
			
		});
		grid.addListener(Events.AfterEdit, new Listener<GridEvent<BaseModelData>>() { 
			public void handleEvent(GridEvent<BaseModelData> ge) {
				//  Check to see if the value has actually changed
				Object value = ge.getRecord().get(ge.getProperty());
				Record record = undoStackRecords.pop();
				ArrayList<Object> data = undoStackData.pop();
				if (data.get(1) != value) {
					undoStackRecords.push(record);
					undoStackData.push(data);
				} else if (undoStackRecords.isEmpty() & undoStackData.isEmpty()) {
					undoItem.disable();
				}
				
				if (explorer.getColumnEditItem().isPressed()) {
					for (BaseModelData model : grid.getSelectionModel()
							.getSelectedItems()) {
						record = explorer.store.getRecord(model);
						Object old_value = record.get(ge.getProperty());
						record.set(ge.getProperty(), value);
						
						//  Place the old values on the undo stack
						undoStackRecords.push(record);
						data = new ArrayList<Object>();
						data.add(ge.getProperty());
						data.add(old_value);
						undoStackData.push(data);
					}
				}
			}
		});
	}
	
	public void doUndo() {
	//  If the undo stacks are not empty pop the top and update the record.
		if (!undoStackRecords.isEmpty() & !undoStackData.isEmpty()) {
			Record record = undoStackRecords.pop();
			ArrayList<Object> undoData = undoStackData.pop();
			Object value = record.get(undoData.get(0).toString());
			
			ArrayList<Object> data = new ArrayList<Object>();
			data.add(undoData.get(0));
			data.add(value);
			redoStackRecords.push(record);
			redoStackData.push(data);
			
			record.set(undoData.get(0).toString(), undoData.get(1));
			
			// Disable the undo button if there are no more undos.
			if (undoStackRecords.isEmpty()) {
				undoItem.disable();
			}
			
			if (!redoItem.isEnabled()) {
				redoItem.enable();
			}
		}
	}
	
	public void doRedo() {
	//  If the undo stacks are not empty pop the top and update the record.
		if (!redoStackRecords.isEmpty() & !redoStackData.isEmpty()) {
			Record record = redoStackRecords.pop();
			ArrayList<Object> redoData = redoStackData.pop();
			Object value = record.get(redoData.get(0).toString());
			
			ArrayList<Object> data = new ArrayList<Object>();
			data.add(redoData.get(0));
			data.add(value);
			undoStackRecords.push(record);
			undoStackData.push(data);
			
			record.set(redoData.get(0).toString(), redoData.get(1));
			
			// Disable the undo button if there are no more undos.
			if (redoStackRecords.isEmpty()) {
				redoItem.disable();
			}
			
			if (!undoItem.isEnabled()) {
				undoItem.enable();
			}
		}
	}
	
	public void reset() {
		undoStackRecords.clear();
		undoStackData.clear();
		undoItem.disable();
		redoStackRecords.clear();
		redoStackData.clear();
		redoItem.disable();
	}
	
}
