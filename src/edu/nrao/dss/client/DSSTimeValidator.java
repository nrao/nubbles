package edu.nrao.dss.client;

import com.extjs.gxt.ui.client.widget.form.Field;
import com.extjs.gxt.ui.client.widget.form.Validator;

public class DSSTimeValidator implements Validator {
	@Override
	public String validate(Field<?> field, String value) {
    	// TODO: reg. expression would be more elegant
//	    String timeReg = "";
//	    value.matches(timeReg)
    	if (value.endsWith(".00") || value.endsWith(".0") || value.endsWith(".25") || value.endsWith(".50") || value.endsWith(".5") || value.endsWith(".75")) {
    		//return null;
		} else {
			return "Time must be on 15-min boundary";
		}		
    	if (value.startsWith("-")) {
    		return "Time must be positive hours";
    	}
    	
    	// valid
    	return null;
	}
}
