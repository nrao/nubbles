package edu.nrao.dss.client;

import com.extjs.gxt.ui.client.data.ModelType;

public class WindowedPeriodType extends ModelType {
	public WindowedPeriodType(ColumnType[] columnTypes) {
		setRoot("periods");
		setTotalName("total");
		
        addField("id");
        for (ColumnType ct : columnTypes) {
            addField(ct.getId());
        }
	}

}