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
