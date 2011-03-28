package edu.nrao.dss.client.data;

import com.extjs.gxt.ui.client.data.ModelType;

import edu.nrao.dss.client.widget.explorers.ColumnType;

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