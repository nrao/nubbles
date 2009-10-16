package edu.nrao.dss.client;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;
import java.util.TreeMap;
import java.util.List;

import com.extjs.gxt.ui.client.Style.Orientation;
import com.extjs.gxt.ui.client.Style.Scroll;
import com.extjs.gxt.ui.client.Style.VerticalAlignment;
import com.extjs.gxt.ui.client.util.Margins;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.form.FormPanel;
import com.extjs.gxt.ui.client.widget.form.NumberField;
import com.extjs.gxt.ui.client.widget.form.TextArea;
import com.extjs.gxt.ui.client.widget.layout.RowData;
import com.extjs.gxt.ui.client.widget.layout.RowLayout;
import com.extjs.gxt.ui.client.widget.layout.TableData;
import com.extjs.gxt.ui.client.widget.layout.TableLayout;
import com.google.gwt.core.client.GWT;

public class PeriodTimeAccountPanel extends TimeAccountingPanel {
	
	private Period period;
	
	public void setPeriod(Period p) {
		period = p;
		setValues(p);
	}
	
	private void setValues(Period p) {
		GWT.log("PeriodTimeAccountPanel.setValues", null);
		if (p != null) {
			GWT.log(String.valueOf(p.getScheduled()), null);
			scheduled.setValue(p.getScheduled());
			notBillable.setValue(p.getNot_billable());
			shortNotice.setValue(p.getShort_notice());
			lt.setValue(p.getLost_time());
			ltw.setValue(p.getLost_time_weather());
			ltr.setValue(p.getLost_time_rfi());
			lto.setValue(p.getLost_time_other());
			os.setValue(p.getOther_Session());
			osw.setValue(p.getOther_session_weather());
			osr.setValue(p.getOther_session_rfi());
			oso.setValue(p.getOther_session_other());
			desc.setValue(p.getDescription());
		}
	}
}

