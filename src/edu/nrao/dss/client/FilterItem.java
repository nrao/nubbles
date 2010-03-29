package edu.nrao.dss.client;

import com.extjs.gxt.ui.client.data.BaseModelData;
import com.extjs.gxt.ui.client.data.BasePagingLoadResult;
import com.extjs.gxt.ui.client.event.ComponentEvent;
import com.extjs.gxt.ui.client.event.KeyListener;
import com.extjs.gxt.ui.client.widget.form.TextField;
import com.extjs.gxt.ui.client.widget.menu.MenuItem;
import com.google.gwt.http.client.RequestBuilder;

public class FilterItem extends MenuItem {
	public FilterItem(final Explorer explorer, Boolean submitOnEnter) {
		super("Filter");
		textField = new TextField<String>();
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
