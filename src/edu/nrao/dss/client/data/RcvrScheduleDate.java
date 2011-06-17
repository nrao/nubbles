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

import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONObject;

public class RcvrScheduleDate {

	private Date date;
	private String[] up;
	private String[] down;
	private String[] availableRx;

	// sets availableRx field
	public void parseRxJson(JSONArray onRcvrs) {
		// here we have the rcvr's available on this date
		String[] available = new String[onRcvrs.size()];
		for(int ri = 0; ri < onRcvrs.size(); ri++) {
			available[ri] = onRcvrs.get(ri).isString().stringValue();
		}
		setAvailableRx(available);		
	}
	
	// sets up and down fields
	public void parseDiffJson(JSONObject diff) {
		// get the list of rcvrs that are going up this day
		JSONArray ups = diff.get("up").isArray();
		this.up = new String[ups.size()];
		for (int j = 0; j<ups.size(); j++) {
			
			this.up[j] = ups.get(j).isString().stringValue();
		}
		// get the list of rcvrs that are going down this day
		JSONArray downs = diff.get("down").isArray();
		this.down = new String[downs.size()];
		for (int j = 0; j<downs.size(); j++) {
			this.down[j] = downs.get(j).isString().stringValue();
		}
	}
	
	public void setDate(Date date) {
		this.date = date;
	}
	public Date getDate() {
		return date;
	}
	public String getDateStr() {
		return RcvrScheduleData.DATE_FORMAT.format(date);
	}	
	public void setUp(String[] up) {
		this.up = up;
	}
	public String[] getUp() {
		return up;
	}
	public String getUpStr() {
		return concatStrings(up);
	}
	private String concatStrings(String[] strs) {
		String value = "";
		for (int i=0; i<strs.length; i++) {
			String sep = (i==0) ? "" : ", ";
			value += sep + strs[i];
		}
		return value;
	}
	public void setDown(String[] down) {
		this.down = down;
	}
	public String[] getDown() {
		return down;
	}
	public String getDownStr() {
		return concatStrings(down);
	}	
	public void setAvailableRx(String[] availableRx) {
		this.availableRx = availableRx;
	}
	public String[] getAvailableRx() {
		return availableRx;
	}
	public boolean isRcvrAvailable(String rcvr) {
		for (int i=0; i<availableRx.length; i++) {
			if (availableRx[i].compareTo(rcvr) == 0) {
				return true;
			}
		}
		return false; // never found it
	}
}
