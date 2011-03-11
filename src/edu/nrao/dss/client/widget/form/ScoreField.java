package edu.nrao.dss.client.widget.form;

import com.google.gwt.core.client.GWT;

public class ScoreField {

	public ScoreField(Float value) {
		this.value = value;
	}
	
	public String toString() {
		return this.value.toString();
	}
	
	private final Float value;
}
