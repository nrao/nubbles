package edu.nrao.dss.client.data;

import com.extjs.gxt.ui.client.data.ModelType;

public class ProjectType extends ModelType {
	public ProjectType() {
		setRoot("projects");
		setTotalName("total");
		
		addField("id");
		addField("pcode");
		addField("type");
		addField("semester");
		addField("pi");
		addField("co_i");
		addField("friends");
		addField("name");
		addField("total_time");
		addField("PSC_time");
		addField("sem_time");
		addField("remaining");
		addField("grade");
		addField("thesis");
		addField("blackouts");
		addField("complete");
	}

}