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
