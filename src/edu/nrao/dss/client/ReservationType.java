package edu.nrao.dss.client;

import com.extjs.gxt.ui.client.data.ModelType;

public class ReservationType extends ModelType {
	public ReservationType() {
		setRoot("reservations");
		setTotalName("total");
		
		addField("name");
		addField("start");
		addField("end");
	}

}
