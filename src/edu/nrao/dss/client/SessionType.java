package edu.nrao.dss.client;

import com.extjs.gxt.ui.client.data.ModelType;

class SessionType extends ModelType {
    public SessionType(ColumnType[] columnTypes) {
        this.setRoot("sessions");
        this.setTotalName("total");

        addField("id");
        for (ColumnType ct : columnTypes) {
                addField(ct.getId());
        }
    }
}
