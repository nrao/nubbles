package edu.nrao.dss.client.data;

import com.extjs.gxt.ui.client.data.ModelType;

import edu.nrao.dss.client.widget.explorers.ColumnType;


public class WindowType extends ModelType {
	public WindowType(ColumnType[] columnTypes) {
		setRoot("windows");
        this.setTotalName("total");
		
        addField("id");
        for (ColumnType ct : columnTypes) {
            addField(ct.getId());
        }
	}

}