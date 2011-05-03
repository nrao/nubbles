package edu.nrao.dss.client.widget.form;

import com.extjs.gxt.ui.client.widget.form.Field;
import com.extjs.gxt.ui.client.widget.form.Validator;

public class IntegerValidator implements Validator {

	@Override
	public String validate(Field<?> field, String value) {
		try {
			int days = Integer.parseInt(value);
		} catch (Exception e) {
			return "Value must be an Integer.";
		}		
		return null;
	}

}
