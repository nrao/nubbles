package edu.nrao.dss.client;

import java.util.List;

import com.extjs.gxt.ui.client.data.ModelType;

public class GenericType extends ModelType {
	public GenericType(String rootType, List<String> fields) {
		setRoot(rootType);
		setTotalName("total");
		
		// mandatory id
        addField("id");
        
        // additional fields
        for (String field : fields) {
        	addField(field);
        }
        
        
	}

}
