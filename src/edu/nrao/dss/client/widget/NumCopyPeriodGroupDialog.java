package edu.nrao.dss.client.widget;

import com.extjs.gxt.ui.client.widget.Dialog;
import com.extjs.gxt.ui.client.widget.form.FormPanel;
import com.extjs.gxt.ui.client.widget.form.NumberField;
import com.extjs.gxt.ui.client.widget.layout.FlowLayout;
import com.google.gwt.i18n.client.NumberFormat;

public class NumCopyPeriodGroupDialog extends Dialog {
	
	private NumberField numCopies;
	
	public NumCopyPeriodGroupDialog() {
		initLayout();
	}
	
	private void initLayout() {
		
		//setLayout(new FlowLayout());
		
		FormPanel fp = new FormPanel();
		fp.setHeaderVisible(false);
		
		numCopies = new NumberField();
		numCopies.setFieldLabel("Copies");
		numCopies.setFormat(NumberFormat.getFormat("#0"));
		numCopies.setValue(1);
		
		fp.add(numCopies);
		
		add(fp);
	}
	
	public int getNumCopies() {
		return numCopies.getValue().intValue();
	}
}
