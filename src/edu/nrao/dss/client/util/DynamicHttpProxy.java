package edu.nrao.dss.client.util;

import com.extjs.gxt.ui.client.data.HttpProxy;
import com.google.gwt.http.client.RequestBuilder;

public class DynamicHttpProxy<D> extends HttpProxy<D> {

	public DynamicHttpProxy(RequestBuilder builder) {
		super(builder);
	}
	
	public void setBuilder(RequestBuilder builder) {
		this.builder = builder;
		this.initUrl = builder.getUrl();
	}

}
