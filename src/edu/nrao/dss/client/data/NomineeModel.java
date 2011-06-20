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

package edu.nrao.dss.client.data;

import java.util.Date;

import com.extjs.gxt.ui.client.data.BaseModel;

public class NomineeModel extends BaseModel {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public NomineeModel(String sname, String stype, String pname, Float score, String scoreStr, Integer duration, String durationStr) {
		set("sess_name", sname);
		set("sess_type", stype);
		set("proj_name", pname);
		set("score", score);
		set("scoreStr", scoreStr);
		set("duration",  duration);
		set("durationStr", durationStr);
	}

}
