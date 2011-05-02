package edu.nrao.dss.client;

import com.extjs.gxt.ui.client.widget.form.Field;
import com.extjs.gxt.ui.client.widget.form.NumberField;
import com.google.gwt.junit.client.GWTTestCase;

import edu.nrao.dss.client.widget.form.DSSTimeValidator;

public class TestDSSTimeValidator extends GWTTestCase {
	@Override
	public String getModuleName() {
		return "edu.nrao.dss.Nubbles";		
	}
    public void testOne() {
    	String errMsg1 = "Time must be on 15-min boundary";
    	String errMsg2 = "Time must be positive hours";
    	NumberField f = new NumberField();
    	DSSTimeValidator v = new DSSTimeValidator();
    	assertEquals(null, v.validate(f, "1.0"));
    	assertEquals(null, v.validate(f, "1.25"));
    	assertEquals(null, v.validate(f, "1.5"));
    	assertEquals(null, v.validate(f, "1.75"));
    	assertEquals(null, v.validate(f, "2.0"));
    	assertEquals(errMsg1, v.validate(f, "2.3"));
    	assertEquals(errMsg2, v.validate(f, "-1.0"));
    	assertEquals(null, v.validate(f, "1.00"));
    	assertEquals(null, v.validate(f, "1.250"));
    	assertEquals(null, v.validate(f, "1.50"));
    	assertEquals(null, v.validate(f, "1.750"));
    	assertEquals(null, v.validate(f, "2.00"));
    	assertEquals(errMsg1, v.validate(f, "2.33333"));
    	assertEquals(errMsg1, v.validate(f, "2.00001"));
    	assertEquals(errMsg2, v.validate(f, "-0.01"));
    	
    }

}
