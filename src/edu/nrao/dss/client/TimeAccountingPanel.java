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
import com.google.gwt.core.client.GWT;

public class TimeAccountingPanel extends ContentPanel { 
	
    protected NumberField scheduled = new NumberField();
    protected NumberField timeBilled = new NumberField();
    protected NumberField observed = new NumberField();
    protected NumberField unaccounted = new NumberField();
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
    
    protected int fieldWidth = 45;
    protected int fieldHeight = 20;
    
    protected FormData fd = new FormData(fieldWidth, fieldHeight);
    
	public TimeAccountingPanel() {
		initLayout();
	}
	
	protected void initLayout() {
		
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
//		scheduled.setFieldLabel("Scheduled");
//		setDefaultField("Scheduled", scheduled);
		//scheduled.setReadOnly(true);
		miscTimes.add(scheduled, new FormData(fieldWidth, fieldHeight));

//		observed.setFieldLabel("Observed");
//		observed.setReadOnly(true);
		miscTimes.add(observed, new FormData(fieldWidth, fieldHeight));

//		timeBilled.setFieldLabel("Billed");
//		timeBilled.setReadOnly(true);
		miscTimes.add(timeBilled, new FormData(fieldWidth, fieldHeight));

//		unaccounted.setFieldLabel("Unaccounted");
//		unaccounted.setReadOnly(true);
		miscTimes.add(unaccounted, new FormData(fieldWidth, fieldHeight));

		//NumberField notBillable = new NumberField();
//		notBillable.setFieldLabel("Not Billable");
//		notBillable.setReadOnly(true);
		miscTimes.add(notBillable, new FormData(fieldWidth, fieldHeight));

		//NumberField shortNotice = new NumberField();
//		shortNotice.setFieldLabel("Short Notice");
//		shortNotice.setReadOnly(true);
		miscTimes.add(shortNotice, new FormData(fieldWidth, fieldHeight));
		
		row1.add(miscTimes, td);
		
        // lost time is the middle column in the first row 
        FormPanel lostTimePanel = new FormPanel();
        lostTimePanel.setHeading("Lost Time (Hrs)");
        lostTimePanel.setHeaderVisible(true);
        
		//NumberField nb = new NumberField();
//		lt.setFieldLabel("Lost Time");
//		lt.setReadOnly(true);
		
		lostTimePanel.add(lt, new FormData(fieldWidth, fieldHeight));

		//NumberField nb2 = new NumberField();
//		ltw.setFieldLabel("LT Weather");
//		ltw.setReadOnly(true);
		lostTimePanel.add(ltw, new FormData(fieldWidth, fieldHeight));

		//NumberField ltr = new NumberField();
//		ltr.setFieldLabel("LT RFI");
//		ltr.setReadOnly(true);
		lostTimePanel.add(ltr, new FormData(fieldWidth, fieldHeight));

		//NumberField lto = new NumberField();
//		lto.setFieldLabel("LT Other");
//		lto.setReadOnly(true);
		lostTimePanel.add(lto, new FormData(fieldWidth, fieldHeight));
		
		row1.add(lostTimePanel, td);
		
		// time to other session to right in second row
        FormPanel otherTimePanel = new FormPanel();
        otherTimePanel.setHeading("Time to Other Session (Hrs)");
        otherTimePanel.setHeaderVisible(true);
        
		//NumberField nb3 = new NumberField();
//		os.setFieldLabel("Other Session");
//		os.setReadOnly(true);
		otherTimePanel.add(os, new FormData(fieldWidth, fieldHeight));

		//NumberField osw = new NumberField();
//		osw.setFieldLabel("OS Weather");
//		osw.setReadOnly(true);
		otherTimePanel.add(osw, new FormData(fieldWidth, fieldHeight));

		//NumberField osr = new NumberField();
//		setDefaultField("OS RFI", osr);
//		osr.setFieldLabel("OS RFI");
//		osr.setReadOnly(true);
		otherTimePanel.add(osr, new FormData(fieldWidth, fieldHeight));
		setDefaultField("OS Other", oso);
        otherTimePanel.add(oso, new FormData(fieldWidth, fieldHeight));
		
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
		
		setFieldAttributes();
	}	
	
	protected void setFieldAttributes() {
		setDefaultField("Scheduled", scheduled);
		setDefaultField("Not Billable", notBillable);
		setDefaultField("Short Notice", shortNotice);
		setDefaultField("Time Billed", timeBilled);
		setDefaultField("Unaccounted", unaccounted);
		setDefaultField("Observed", observed);
		
		setDefaultField("Lost Time", lt);
		setDefaultField("LT Weather", ltw);
		setDefaultField("LT RFI", ltr);
		setDefaultField("LT Other", lto);
		
		setDefaultField("Other Session", os);
		setDefaultField("OS Weather", osw);
		setDefaultField("OS RFI", osr);
		setDefaultField("OS Other", oso);
	}
	
	private void setDefaultField(String label, NumberField nf) {
		nf.setFieldLabel(label);
		nf.setReadOnly(true);
		// TODO: read only fields should just have their background color darkened!
		//nf.setEnabled(false);
		//nf.setStyleAttribute("border" , "5px solid line");
		//nf.setStyleAttribute("background-color", "#FFFFFF");
		nf.setStyleAttribute("color", "grey");
	}
	
	protected void setEditable(NumberField nf) {
		nf.setReadOnly(false);
		nf.setStyleAttribute("color", "black");
	}
}
