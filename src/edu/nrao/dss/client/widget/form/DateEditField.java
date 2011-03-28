package edu.nrao.dss.client.widget.form;

import java.util.Date;

import com.google.gwt.core.client.GWT;
import com.google.gwt.i18n.client.DateTimeFormat;

public class DateEditField {
    
    public DateEditField(Date value) {
        this.value = value;
    }
    
    public String toString() {
    	DateTimeFormat fmt = DateTimeFormat.getFormat("yyyy-MM-dd");	
    	GWT.log(fmt.format(value), null);
        return fmt.format(value);
    }
    
    private final Date value;
}
