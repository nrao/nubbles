// Copyright (C) 2011 Associated Universities, Inc. Washington DC, USA.
// 
// This program is free software; you can redistribute it and/or modify
// it under the terms of the GNU General Public License as published by
// the Free Software Foundation; either version 2 of the License, or
// (at your option) any later version.
// 
// This program is distributed in the hope that it will be useful, but
// WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
// General Public License for more details.
// 
// You should have received a copy of the GNU General Public License
// along with this program; if not, write to the Free Software
// Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.
// 
// Correspondence concerning GBT software should be addressed as follows:
//       GBT Operations
//       National Radio Astronomy Observatory
//       P. O. Box 2
//       Green Bank, WV 24944-0002 USA

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
