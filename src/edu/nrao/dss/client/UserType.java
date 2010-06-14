package edu.nrao.dss.client;

import com.extjs.gxt.ui.client.data.ModelType;

public class UserType extends ModelType {
	public UserType() {
		setRoot("users");
		setTotalName("total");
		
		addField("id");
		addField("first_name");
		addField("last_name");
		addField("sanctioned");
		addField("projects");
		addField("role");
		addField("staff");
		addField("username");
	}

}
