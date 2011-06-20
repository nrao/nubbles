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

import java.util.Date;

import com.extjs.gxt.ui.client.data.BaseListLoadResult;
import com.extjs.gxt.ui.client.data.BaseModelData;
import com.extjs.gxt.ui.client.data.BasePagingLoadResult;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.layout.FitData;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.google.gwt.http.client.RequestBuilder;

import edu.nrao.dss.client.util.DynamicHttpProxy;
import edu.nrao.dss.client.widget.ReservationsGrid;

public class Reservations extends ContentPanel {

	public ReservationsGrid res;
	
	public Reservations (Date start, int days) {
		super();
		initLayout(start, days);
	}
	
	private void initLayout(Date start, int days){
		setHeading("Reservations");
		setBorders(true);
		
		// put the reservation grid inside
		FitLayout fl = new FitLayout();
		setLayout(fl);
	    res = new ReservationsGrid(start, days);
	    add(res, new FitData(10));
	}
	
	public void update(String start, String days) {
		// get the period explorer to load these
		String url = "/scheduler/reservations?start=" + start + "&days=" + days;
		RequestBuilder builder = new RequestBuilder(RequestBuilder.GET, url);
		DynamicHttpProxy<BaseListLoadResult<BaseModelData>> proxy = res.getProxy();
		proxy.setBuilder(builder);
		res.load();
	}
}
