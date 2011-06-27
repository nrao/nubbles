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

package edu.nrao.dss.client.widget;

import com.google.gwt.json.client.JSONObject;

public class ProjectTimeAccountPanel extends TimeAccountingPanel {

	public void setValues(JSONObject json) {
		
		// these are all read-only, so there's no change in state; we can just set them
	    scheduled.setValue(json.get("scheduled").isNumber().doubleValue());
	    notBillable.setValue(json.get("not_billable").isNumber().doubleValue());	
	    shortNotice.setValue(json.get("short_notice").isNumber().doubleValue());	
	    observed.setValue(json.get("observed").isNumber().doubleValue());	
	    timeBilled.setValue(json.get("time_billed").isNumber().doubleValue());	
	    lp.setValue(json.get("lost_time_bill_project").isNumber().doubleValue());
	    unaccounted.setValue(json.get("unaccounted_time").isNumber().doubleValue());		    
	    
	    lt.setValue(json.get("lost_time").isNumber().doubleValue());	
	    ltw.setValue(json.get("lost_time_weather").isNumber().doubleValue());	
	    ltr.setValue(json.get("lost_time_rfi").isNumber().doubleValue());	
	    lto.setValue(json.get("lost_time_other").isNumber().doubleValue());
	    
	    os.setValue(json.get("other_session").isNumber().doubleValue());	
	    osw.setValue(json.get("other_session_weather").isNumber().doubleValue());	
	    osr.setValue(json.get("other_session_rfi").isNumber().doubleValue());	
	    oso.setValue(json.get("other_session_other").isNumber().doubleValue());
	    
	    // Description is NOT read-only, so must set state
	    setDescription(json.get("notes").isString().stringValue());
//	    desc.setValue(json.get("notes").isString().stringValue());
	    
	    
	    
	}
}
