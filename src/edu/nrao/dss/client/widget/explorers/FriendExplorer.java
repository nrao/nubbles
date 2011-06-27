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

import edu.nrao.dss.client.widget.form.DisplayField;
import edu.nrao.dss.client.widget.form.UserForm;

public class FriendExplorer extends UserProjectExplorer {

//	private Integer project_id;
//	private UserForm addUser;
	
	public FriendExplorer() {
		super("friends", "/scheduler/friends", getColumnTypes(), getFields());
		// setup the widget used for adding a new friend
		setAddUser(new UserForm("Friend", "friends",
				new Window(), FriendExplorer.this));
		setHeight(250);
		setWidth(600);
	}
	
	// JSON-fields
	protected static List<String> getFields() {
		ArrayList<String> fields = new ArrayList<String>();
		fields.add("name");
		fields.add("required");
		fields.add("project_id");
		fields.add("user_id");
		return fields;
	}
	
	// columns in the Explorer
	protected static ColumnType[] getColumnTypes() {
		ColumnType[] columnTypes = {
	    	new ColumnType("name",                "Name",               250,false, DisplayField.class),
	    	new ColumnType("required",            "Required",            65,false, Boolean.class)
	    };
		return columnTypes;
	}
}
