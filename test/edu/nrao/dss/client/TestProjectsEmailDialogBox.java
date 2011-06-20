// Copyright (C) 2011 Associated Universities, Inc. Washington DC, USA.
// 
// This program is free software; you can redistribute it and/or modify
// it under the terms of the GNU General Public License as published by
// the Free Software Foundation; either version 2 of the License, or
// (at your option) any later version.
// 
// This program is distributed in the hope that it will be useful, but
// WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
// General Public License for more details.
// 
// You should have received a copy of the GNU General Public License
// along with this program; if not, write to the Free Software
// Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.
// 
// Correspondence concerning GBT software should be addressed as follows:
//       GBT Operations
//       National Radio Astronomy Observatory
//       P. O. Box 2
//       Green Bank, WV 24944-0002 USA

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
