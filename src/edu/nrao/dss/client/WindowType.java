package edu.nrao.dss.client;

import com.extjs.gxt.ui.client.data.ModelType;

public class WindowType extends ModelType {
	public WindowType(ColumnType[] columnTypes) {
		setRoot("windows");
		
        addField("id");
        for (ColumnType ct : columnTypes) {
            addField(ct.getId());
        }
	}

}