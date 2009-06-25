package edu.nrao.dss.client;

import com.extjs.gxt.ui.client.data.BaseModelData;
import com.extjs.gxt.ui.client.data.BasePagingLoadResult;
import com.extjs.gxt.ui.client.event.ComponentEvent;
import com.extjs.gxt.ui.client.event.KeyListener;
import com.extjs.gxt.ui.client.widget.form.TextField;
import com.extjs.gxt.ui.client.widget.menu.MenuItem;
import com.google.gwt.http.client.RequestBuilder;

public class FilterItem extends MenuItem {
	public FilterItem(final SessionExplorer sx) {
		super("Filter");
		textField = new TextField<String>(); //(TextField<String>) getWidget();
		setTitle("Display sessions containing ...");
		defineListener(sx);
	}
	
	private void defineListener(final SessionExplorer sx) {
		textField.addKeyListener(new KeyListener() {
			@Override
			public void componentKeyPress(ComponentEvent e) {
				if (e.getKeyCode() == 13) {
					String filterText = textField.getValue();
					String url = "/sessions" + (filterText != null ? "?filterText=" + filterText : "");
					RequestBuilder builder = new RequestBuilder(RequestBuilder.GET, url);
					DynamicHttpProxy<BasePagingLoadResult<BaseModelData>> proxy = sx.getProxy();
					proxy.setBuilder(builder);
					sx.loadData();
				}
			}
		});
	}
	
	private final TextField<String> textField;

}
