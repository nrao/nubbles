package edu.nrao.dss.client;

import com.extjs.gxt.ui.client.data.ModelType;

public class NomineeType extends ModelType {
	public NomineeType() {
        this.setRoot("nominees");
        this.setTotalName("total");
        
        addField("sess_name");
        addField("proj_name");
        addField("score");
        addField("duration");
        addField("durationStr");
	}
}
