package edu.nrao.dss.client;

import com.extjs.gxt.ui.client.data.ModelType;

public class PeriodType extends ModelType {
	public PeriodType(ColumnType[] columnTypes) {
		setRoot("periods");
		setTotalName("total");
		
        addField("id");
        for (ColumnType ct : columnTypes) {
            addField(ct.getId());
        }
	}

}
