package edu.nrao.dss.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.junit.client.GWTTestCase;

import edu.nrao.dss.client.widget.ProjectsEmailDialogBox;

public class TestProjectsEmailDialogBox extends GWTTestCase {
	@Override
	public String getModuleName() {
		return "edu.nrao.dss.Nubbles";		
	}
    public void testUpdateRecipients() {
    	ProjectsEmailDialogBox dlg = new ProjectsEmailDialogBox("pcode1, pcode2", "pi", "pc", "ci", "ob", "fs", "gb"
    			, new String [][] {{"temp1", "subj1", "body1"},{"temp2", "subj2", "body2"}});
    	dlg.initLayout();
    	
    	// check initial state
    	assertEquals("gb, pi", dlg.getTextArea("to:"));
    	assertEquals("", dlg.getTextArea("subject:"));
    	assertEquals("", dlg.getTextArea("body:"));
    	
    	// make sure nothing changes
    	dlg.update_recipients();
    	assertEquals("gb, pi", dlg.getTextArea("to:"));
    	assertEquals("", dlg.getTextArea("subject:"));
    	assertEquals("", dlg.getTextArea("body:"));
    	
    	// change the recipients
    	dlg.checkObservers(true);
    	dlg.checkPI(false);
    	dlg.update_recipients();
    	assertEquals("gb, ob", dlg.getTextArea("to:"));
    	assertEquals("", dlg.getTextArea("subject:"));
    	assertEquals("", dlg.getTextArea("body:"));
    }

}
