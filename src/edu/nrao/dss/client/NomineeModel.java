package edu.nrao.dss.client;

import java.util.Date;

import com.extjs.gxt.ui.client.data.BaseModel;

public class NomineeModel extends BaseModel {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public NomineeModel(String sname, String pname, Float score, Integer duration, String durationStr) {  // TBF window
//	public NomineeModel(String sname, String stype, String pname, Float score, String scoreStr, Integer duration, String durationStr) {
		set("sess_name", sname);
//		set("sess_type", stype);   // TBF window
		set("proj_name", pname);
		set("score", score);
//		set("scoreStr", scoreStr);  // TBF window
		set("duration",  duration);
		set("durationStr", durationStr);
	}

}
