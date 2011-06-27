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

package edu.nrao.dss.client.widget;

import com.extjs.gxt.ui.client.data.BaseModelData;
import com.extjs.gxt.ui.client.data.BasePagingLoadResult;
import com.extjs.gxt.ui.client.event.ComponentEvent;
import com.extjs.gxt.ui.client.event.KeyListener;
import com.extjs.gxt.ui.client.widget.form.TextField;
import com.extjs.gxt.ui.client.widget.menu.MenuItem;
import com.google.gwt.http.client.RequestBuilder;

import edu.nrao.dss.client.util.DynamicHttpProxy;
import edu.nrao.dss.client.widget.explorers.Explorer;

public class FilterItem extends MenuItem {
	public FilterItem(final Explorer explorer, Boolean submitOnEnter) {
		super("Filter");
		textField = new TextField<String>();
		textField.setToolTip("Enter text to filter the information below.");
		setTitle("Display rows containing ...");
		if (submitOnEnter) {
			defineListener(explorer);
		}
	}
	
	private void defineListener(final Explorer explorer) {
		textField.addKeyListener(new KeyListener() {
			@Override
			public void componentKeyPress(ComponentEvent e) {
				if (e.getKeyCode() == 13) {
					String filterText = textField.getValue();
					String url = explorer.getRootURL() + (filterText != null ? "?filterText=" + filterText : "");
					RequestBuilder builder = new RequestBuilder(RequestBuilder.GET, url);
					DynamicHttpProxy<BasePagingLoadResult<BaseModelData>> proxy = explorer.getProxy();
					proxy.setBuilder(builder);
					explorer.loadData();
				}
			}
		});
	}
	
	public TextField<String> getTextField() {
		return textField;
	}
	
	private final TextField<String> textField;

}
