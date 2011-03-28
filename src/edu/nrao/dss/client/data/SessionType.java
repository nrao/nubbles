package edu.nrao.dss.client.data;

import com.extjs.gxt.ui.client.data.ModelType;

import edu.nrao.dss.client.widget.explorers.ColumnType;

public class SessionType extends ModelType {
    public SessionType(ColumnType[] columnTypes) {
        this.setRoot("sessions");
        this.setTotalName("total");

        addField("id");
        for (ColumnType ct : columnTypes) {
                addField(ct.getId());
        }
    }
}
