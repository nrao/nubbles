package edu.nrao.dss.client;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.extjs.gxt.ui.client.data.BaseListLoadResult;
import com.extjs.gxt.ui.client.data.BaseListLoader;
import com.extjs.gxt.ui.client.data.BaseModelData;
import com.extjs.gxt.ui.client.data.HttpProxy;
import com.extjs.gxt.ui.client.data.JsonReader;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.grid.ColumnConfig;
import com.extjs.gxt.ui.client.widget.grid.ColumnModel;
import com.extjs.gxt.ui.client.widget.grid.Grid;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.google.gwt.core.client.GWT;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.i18n.client.DateTimeFormat;

public class ReservationsGrid extends ContentPanel{
	public DynamicHttpProxy<BaseListLoadResult<BaseModelData>> proxy;
	BaseListLoader<BaseListLoadResult<BaseModelData>> loader;
	
	public ReservationsGrid(Date start, int days) {
		initLayout(start, days);
	}
	
	private void initLayout(Date start, int days) {
		setHeaderVisible(false);
		setLayout(new FitLayout());
		setAutoHeight(true);
		
		DateTimeFormat fmt = DateTimeFormat.getFormat("MM/dd/yyyy");
		String startStr = fmt.format(start);
		
		String rootUrl = "/reservations?start=" + startStr + "&days=" + days;
				
		RequestBuilder builder = new RequestBuilder(RequestBuilder.GET, rootUrl);

		JsonReader<BaseListLoadResult<BaseModelData>> reader = new JsonReader<BaseListLoadResult<BaseModelData>>(new ReservationType());
		proxy = new DynamicHttpProxy<BaseListLoadResult<BaseModelData>>(builder);
		loader = new BaseListLoader<BaseListLoadResult<BaseModelData>>(proxy, reader);  
		ListStore<BaseModelData> store = new ListStore<BaseModelData>(loader);
		
		ColumnModel cm = initColumnModel();
	    Grid<BaseModelData> grid = new Grid<BaseModelData>(store, cm);
	    grid.setAutoHeight(true);
	    
		add(grid);
		grid.setBorders(true);
		load();
	}
	
	private ColumnModel initColumnModel() {
		List<ColumnConfig> configs = new ArrayList<ColumnConfig>();

	    ColumnConfig column = new ColumnConfig("name", "Name", 100);
	    configs.add(column);

	    column = new ColumnConfig("start", "Check-in", 70);
	    configs.add(column);
	    
	    column = new ColumnConfig("end", "Check-out", 70);
	    configs.add(column);
	    
	    column = new ColumnConfig("pcodes", "Projects", 300);
	    configs.add(column);	   
	    
	    return new ColumnModel(configs);
	}
	
	public void load() {
		loader.load();
	}
	
	public DynamicHttpProxy<BaseListLoadResult<BaseModelData>> getProxy() {
		return proxy;
	}
}
