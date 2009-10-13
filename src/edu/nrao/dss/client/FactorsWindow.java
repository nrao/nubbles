package edu.nrao.dss.client;

import com.extjs.gxt.ui.client.widget.Window;

public class FactorsWindow extends Window {

	public FactorsWindow() {
		initLayout();
		hide();
		//show();
	}
	
	private void initLayout() {
		setHeading("Factors");
		setModal(false);
		setSize(800, 400);
		setMaximizable(true);
		setToolTip("Individual factors whose product determines a sessions score at a specific time.");
	}
	
}
