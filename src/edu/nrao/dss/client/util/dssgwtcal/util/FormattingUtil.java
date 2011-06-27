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

package edu.nrao.dss.client.util.dssgwtcal.util;

import edu.nrao.dss.client.util.dssgwtcal.util.impl.FormattingImpl;
import com.google.gwt.core.client.GWT;

/**
 * This utility provides access to data to help format widgets
 * correctly across browsers.
 * @author Brad Rydzewski
 */
public class FormattingUtil {

    /**
     * Implementation of formatting class. Holds browser-specific
     * values, loaded by GWT deferred binding.
     */
    private static FormattingImpl impl = GWT.create(FormattingImpl.class);
    
    /**
     * All CSS2 compliant browsers count the border height in the
     * overall height of an Element. This method returns an offset
     * value that should be added to the height or width of an item
     * before setting its size. This will ensure consistent sizing
     * across compliant and non-compliant browsers.
     * @return
     */
    public static int getBorderOffset() {
        return impl.getBorderOffset();
    }
}
