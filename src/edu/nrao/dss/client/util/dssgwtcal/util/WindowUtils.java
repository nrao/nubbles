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

package edu.nrao.dss.client.util.dssgwtcal.util;

/**
 * Provides a set of re-usable methods related to the client's
 * browser window.
 * @author Brad Rydzewski
 */
public class WindowUtils {

	/**
	 * Width in pixels of Client's scroll bar.
	 */
	private static int scrollBarWidth;

	/**
	 * Gets the width of the client's scroll bar.
	 * @param useCachedValue Indicates if cached value should be used, or refreshed.
	 * @return Width, in pixels, of Client's scroll bar
	 */
	public static int getScrollBarWidth(boolean useCachedValue) {
		if(useCachedValue && scrollBarWidth>0) {
			return scrollBarWidth;
		}
		
		scrollBarWidth = getScrollBarWidth();
		return scrollBarWidth;
	}
	
	/** 
	 * Calculates the width of the clients scroll bar, which can vary among operations systems,
	 * browsers and themes. Based on code from: http://www.alexandre-gomes.com/?p=115
	 * @return
	 */
	private static native int getScrollBarWidth() /*-{
	
		var inner = document.createElement("p");
		inner.style.width = "100%";
		inner.style.height = "200px";
		
		var outer = document.createElement("div");
		outer.style.position = "absolute";
		outer.style.top = "0px";
		outer.style.left = "0px";
		outer.style.visibility = "hidden";
		outer.style.width = "200px";
		outer.style.height = "150px";
		outer.style.overflow = "hidden";
		outer.appendChild (inner);
		
		document.body.appendChild (outer);
		var w1 = inner.offsetWidth;
		outer.style.overflow = "scroll";
		var w2 = inner.offsetWidth;
		if (w1 == w2) w2 = outer.clientWidth;
		
		document.body.removeChild (outer);
		 
		return (w1 - w2);
	}-*/;
}
