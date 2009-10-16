package edu.nrao.dss.client;

import com.extjs.gxt.ui.client.Style.HorizontalAlignment;
import com.extjs.gxt.ui.client.Style.VerticalAlignment;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.form.FormPanel;
import com.extjs.gxt.ui.client.widget.form.NumberField;
import com.extjs.gxt.ui.client.widget.form.TextArea;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.extjs.gxt.ui.client.widget.layout.FlowLayout;
import com.extjs.gxt.ui.client.widget.layout.FormData;
import com.extjs.gxt.ui.client.widget.layout.RowLayout;
import com.extjs.gxt.ui.client.widget.layout.TableData;
import com.extjs.gxt.ui.client.widget.layout.TableLayout;

public class TimeAccountingPanel extends ContentPanel { 
	
    protected NumberField scheduled = new NumberField();
    protected NumberField notBillable = new NumberField();
    protected NumberField shortNotice = new NumberField();
    protected NumberField lt = new NumberField();
    protected NumberField ltw = new NumberField();
    protected NumberField ltr = new NumberField();
    protected NumberField lto = new NumberField();
    protected NumberField os = new NumberField();
    protected NumberField osw = new NumberField();
    protected NumberField osr = new NumberField();
    protected NumberField oso = new NumberField();
    protected TextArea desc = new TextArea();
    
	public TimeAccountingPanel() {
		initLayout();
	}
	
	private void initLayout() {
		
		setLayout(new RowLayout());
		setCollapsible(true); 
		setHeaderVisible(true);
		//setHeading("Time Accounting");
		setBorders(true);
		
		// first row - a 3 column table for times
		LayoutContainer row1 = new LayoutContainer();
		TableLayout row1table = new TableLayout(4);
		row1table.setWidth("100%");
		row1table.setBorder(1);
		row1.setBorders(true);
		row1.setLayout(row1table);
		TableData td = new TableData();
		td.setVerticalAlign(VerticalAlignment.TOP);
		td.setHorizontalAlign(HorizontalAlignment.CENTER);

		// 1st column of first row is a form for misc. times
		FormPanel miscTimes = new FormPanel();
		miscTimes.setHeaderVisible(true);
		miscTimes.setHeading("Times (Hrs)");
		
		// start adding the misc. times
		//NumberField scheduled = new NumberField();
		scheduled.setFieldLabel("Scheduled");
		scheduled.setReadOnly(true);
		miscTimes.add(scheduled, new FormData(30, 25));
		
		//NumberField notBillable = new NumberField();
		notBillable.setFieldLabel("Not Billable");
		notBillable.setReadOnly(true);
		miscTimes.add(notBillable, new FormData(30, 25));

		//NumberField shortNotice = new NumberField();
		shortNotice.setFieldLabel("Short Notice");
		shortNotice.setReadOnly(true);
		miscTimes.add(shortNotice, new FormData(30, 25));
		
		row1.add(miscTimes, td);
		
        // lost time is the middle column in the first row 
        FormPanel lostTimePanel = new FormPanel();
        lostTimePanel.setHeading("Lost Time (Hrs)");
        lostTimePanel.setHeaderVisible(true);
        
		//NumberField nb = new NumberField();
		lt.setFieldLabel("Lost Time");
		lt.setReadOnly(true);
		
		lostTimePanel.add(lt, new FormData(30, 25));

		//NumberField nb2 = new NumberField();
		ltw.setFieldLabel("LT Weather");
		ltw.setReadOnly(true);
		lostTimePanel.add(ltw, new FormData(30, 25));

		//NumberField ltr = new NumberField();
		ltr.setFieldLabel("LT RFI");
		ltr.setReadOnly(true);
		lostTimePanel.add(ltr, new FormData(30, 25));

		//NumberField lto = new NumberField();
		lto.setFieldLabel("LT Other");
		lto.setReadOnly(true);
		lostTimePanel.add(lto, new FormData(30, 25));
		
		row1.add(lostTimePanel, td);
		
		// time to other session to right in second row
        FormPanel otherTimePanel = new FormPanel();
        otherTimePanel.setHeading("Time to Other Session (Hrs)");
        otherTimePanel.setHeaderVisible(true);
        
		//NumberField nb3 = new NumberField();
		os.setFieldLabel("Other Session");
		os.setReadOnly(true);
		otherTimePanel.add(os, new FormData(30, 25));

		//NumberField osw = new NumberField();
		osw.setFieldLabel("OS Weather");
		osw.setReadOnly(true);
		otherTimePanel.add(osw, new FormData(30, 25));

		//NumberField osr = new NumberField();
		osr.setFieldLabel("OS RFI");
		osr.setReadOnly(true);
		otherTimePanel.add(osr, new FormData(30, 25));
		
		//NumberField oso = new NumberField();
		oso.setFieldLabel("OS Other");
		oso.setReadOnly(true);
		otherTimePanel.add(oso, new FormData(30, 25));
		
		row1.add(otherTimePanel, td);
		
		add(row1);

		// second row is just for the description for now
		LayoutContainer row2 = new LayoutContainer();
		row2.setLayout(new FlowLayout());
		row2.setBorders(true);
		//row2.setWidth("100%");
		//row2.setHeight("100%");
		
		FormPanel miscTimes2 = new FormPanel();
		miscTimes2.setHeaderVisible(false);
		miscTimes2.setHeading("Times (Hrs)");
		
		//TextArea desc = new TextArea();
		desc.setFieldLabel("Description");
		miscTimes2.add(desc, new FormData(600, 50));	
        
		row2.add(miscTimes2, td);
		
		add(row2);
	}		
}
