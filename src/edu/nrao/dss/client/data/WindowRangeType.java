package edu.nrao.dss.client.data;

import com.extjs.gxt.ui.client.data.ModelType;

import edu.nrao.dss.client.widget.explorers.ColumnType;

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

