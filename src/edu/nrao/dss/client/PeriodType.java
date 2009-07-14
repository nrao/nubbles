package edu.nrao.dss.client;

import com.extjs.gxt.ui.client.data.ModelType;

public class PeriodType extends ModelType {
	public PeriodType() {
		setRoot("periods");
		setTotalName("total");
		
		addField("id");
		addField("start");
		addField("duration");
		
		//TBF: other fields
	}

}
