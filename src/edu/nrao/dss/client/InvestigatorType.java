package edu.nrao.dss.client;

import com.extjs.gxt.ui.client.data.ModelType;

public class InvestigatorType extends ModelType {

	public InvestigatorType() {
		setRoot("investigators");
		setTotalName("total");
		
		addField("name");
		addField("pi");
		addField("contact");
		addField("remote");
		addField("observer");
		addField("priority");
		addField("project_id");
		addField("user_id");
	}

}
