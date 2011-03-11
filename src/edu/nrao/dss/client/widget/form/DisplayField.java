package edu.nrao.dss.client.widget.form;

import com.google.gwt.core.client.GWT;

public class DisplayField {

	public DisplayField(String value) {
		this.value = value;
	}
	
	public String toString() {
		return this.value;
	}
	
	private final String value;
}
