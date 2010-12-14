package edu.nrao.dss.client;

import com.extjs.gxt.ui.client.data.ModelType;

public class WindowRangeType extends ModelType {
	public WindowRangeType(ColumnType[] columnTypes) {
		setRoot("windowRanges");
        this.setTotalName("total");
		
        addField("id");
        for (ColumnType ct : columnTypes) {
            addField(ct.getId());
        }
	}

}

