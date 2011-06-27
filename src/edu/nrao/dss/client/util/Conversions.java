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

package edu.nrao.dss.client.util;

import com.google.gwt.core.client.GWT;
import com.google.gwt.i18n.client.NumberFormat;
import java.lang.Math;

public class Conversions {
    public static String radiansToTime(double radians) {
        return degreesToTime(radiansToDegrees(radians));
    }

    public static String radiansToSexagesimal(double radians) {
        return degreesToSexagesimal(radiansToDegrees(radians));
    }
    
    public static String degreesToTime(double signed_degrees) {
    	double degrees = Math.abs(signed_degrees);
    	String sign = "";
        int    hours   = (int)  (degrees / 15.0);
        int    minutes = (int) ((degrees / 15.0 - hours) * 60.0);
        double seconds =       ((degrees / 15.0 - hours) * 60.0 - minutes) * 60.0;

        if (Math.abs(seconds - 60.0) < 0.1) {
        	minutes += 1;
        	seconds = Math.abs(seconds - 60.0);
        }
        if (Math.abs(minutes - 60.0) < 0.1) {
        	hours += 1;
        	minutes = Math.abs(minutes - 60);
        }
        if (Math.abs(hours - 24.0) < 0.1) {
        	hours = Math.abs(hours - 24);
        }
        if (signed_degrees < 0.0) {
        	sign = "-";
        }

        StringBuilder result = new StringBuilder();
        result.append(sign)
              .append(NumberFormat.getFormat("00").format(hours))
              .append(":")
              .append(NumberFormat.getFormat("00").format(minutes))
              .append(":")
              .append(NumberFormat.getFormat("00.0").format(seconds));

        return result.toString();
    }

    public static String degreesToSexagesimal(double signed_decimaldegrees) {
    	double decimaldegrees = Math.abs(signed_decimaldegrees);
        int    degrees = (int)  (decimaldegrees);
        int    minutes = (int) ((Math.abs(decimaldegrees) - Math.abs(degrees)) * 60.0);
        double seconds =       ((Math.abs(decimaldegrees) - Math.abs(degrees)) * 60.0 - minutes) * 60.0;
    	String sign = "";

        if (Math.abs(seconds - 60.0) < 0.1) {
        	minutes += 1;
        	seconds = Math.abs(seconds - 60.0);
        }
        if (Math.abs(minutes - 60.0) < 0.1) {
        	degrees += 1;
        	minutes = Math.abs(minutes - 60);
        }
        if (signed_decimaldegrees < 0.0) {
        	sign = "-";
        }

        String degreeFormat;
        if (degrees >= 100 | degrees <= -100) {
        	degreeFormat = "000";
        } else {
        	degreeFormat = "00";
        }
        StringBuilder result = new StringBuilder();
        result.append(sign)
        	  .append(NumberFormat.getFormat(degreeFormat).format(degrees))
              .append(":")
              .append(NumberFormat.getFormat("00").format(minutes))
              .append(":")
              .append(NumberFormat.getFormat("00.0").format(seconds));

        return result.toString();
    }
    
    /** HH:MM:SS.S -> Radians */
    public static double timeToRadians(String time) {
        return degreesToRadians(timeToDegrees(time));
    }

    /** DD:MM:SS.S -> Radians */
    public static double sexagesimalToRadians(String sexagesimal) {
        return degreesToRadians(sexigesimalToDegrees(sexagesimal));
    }
    
    /** HH:MM:SS.S -> Degrees */
    public static double timeToDegrees(String time) {
        int start = 0;
        double factor = 1.0;
        if (time.charAt(0) == '-') {
        	start = 1;
        	factor = -1.0;
        }
        double hours   = Double.parseDouble(time.substring(start, start + 2));
        double minutes = Double.parseDouble(time.substring(start + 3, start + 5));
        double seconds = Double.parseDouble(time.substring(start + 6));
        
        return factor*(hours + (minutes + seconds / 60.0) / 60.0) * 15.0;
    }

    /** DD:MM:SS.S -> Degrees */
    public static double sexigesimalToDegrees(String sexigesimal) {
        int start = 0;
        double factor = 1.0;
        if (sexigesimal.charAt(0) == '-') {
        	start = 1;
        	factor = -1.0;
        }
        
        String[] dms = sexigesimal.split(":");
        double degrees = Double.parseDouble(dms[0]);
        double minutes = Double.parseDouble(dms[1]);
        double seconds = Double.parseDouble(dms[2]);
//        double degrees = Double.parseDouble(sexigesimal.substring(start, start + 2));
//        double minutes = Double.parseDouble(sexigesimal.substring(start + 3, start + 5));
//        double seconds = Double.parseDouble(sexigesimal.substring(start + 6));
        
        //return factor*(degrees + (minutes + seconds / 60.0) / 60.0);
        if (degrees < 0) {
        	return (degrees - (minutes + seconds / 60.0) / 60.0);
        } else {
        	return (degrees + (minutes + seconds / 60.0) / 60.0);
        }
    }
    
    public static double radiansToDegrees(double radians) {
        return radians * 180.0 / Math.PI;
    }

    public static double degreesToRadians(double degrees) {
        return degrees * Math.PI / 180.0;
    }
}
