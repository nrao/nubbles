package edu.nrao.dss.client.widget.form;

import com.extjs.gxt.ui.client.widget.form.Field;
import com.extjs.gxt.ui.client.widget.form.Validator;

public class DSSTimeValidator implements Validator {
	@Override
	public String validate(Field<?> field, String value) {

		// make sure it's positive
		Double hours = Double.parseDouble(value);
		if (hours < 0.0) {
    		return "Time must be positive hours";
			
		}
		// make sure it's on 15 minute boundaries
		Double minutes = hours * 60.0;
		if (minutes % 15.0 != 0.0) {
			return "Time must be on 15-min boundary";
		}
    	// well, it must be valid
    	return null;
	}
}
