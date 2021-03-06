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

package edu.nrao.dss.client.util;

import com.extjs.gxt.ui.client.widget.MessageBox;
import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONObject;

interface JSONCallback {
	public void onSuccess(JSONObject json);
	public void onError(String error, JSONObject json);
	public void setUri(String uri);
}

public class JSONCallbackAdapter implements JSONCallback {
	private String uri;
	
	public void onSuccess(JSONObject json) {
	}
	
	public void setUri(String uri) {
		this.uri = uri;
	}

	// Default error response is to alert the user.
	public void onError(String error, JSONObject json) {
		if (json != null && json.containsKey("message")) {
			MessageBox.alert(error, getString(json, "message"), null);
		}
		else if (json != null && json.containsKey("exception_data"))
		{
			try
			{
				String emsg = "An unexpected error has occured on the server. ";
				emsg += "You can help the DSS team solve this problem by cutting and pasting ";
				emsg += "this traceback when reporting this error.\n\n";
				emsg += "Traceback (most recent call last):\n";
				JSONObject einfo = json.get("exception_data").isObject();
				String exception_type = einfo.get("exception_type").toString();
				String exception_data = einfo.get("exception_args").toString();
				JSONArray tb = einfo.get("exception_traceback").isArray();
				
				for (int i = 0; i < tb.size(); ++i)
				{
					emsg += tb.get(i);
				}
				
				// all this is needed to display the traceback properly, with line breaks, etc.
				// MessageBox is a JavaScript creature that only understands HTML
				emsg += exception_type + ": " + exception_data;
				emsg = toHTML(emsg);
				MessageBox.alert(error, emsg, null);
			}
			catch (Exception e)
			{
				String m = e.toString();
				MessageBox.alert("Error", "exception in error handler: " + m, null);
			}
		}
		else 
		{
			String msg = ": ";
			
			if (json != null)
			{
				msg += "An unexpected error has occurred.  You can help the DSS team solve this problem ";
				msg += "by cutting and pasting this JSON object when reporting the error.\n\n";
				msg += "JSON object:\n";
				msg += json.toString();
				msg = toHTML(msg);
			}
			else
			{
				msg += "No response received from server at " + this.uri + "  This could indicate a network ";
				msg += "problem, that the server is down, or an error saving data.";
			}

			MessageBox.alert("Error", error + msg, null);
		}
	}

	protected String getString(JSONObject json, String key) {
		return JSONRequest.getString(json, key);
	}
	
	private String toHTML(String str)
	{
		str = str.replace("<", "&lt;").replace(">", "&gt;").replace("\\\"", "&quot;");
		str = str.replace("\n", "<br/>").replace("\\n", "<br/>");
		return str;
	}
}
