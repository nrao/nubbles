package edu.nrao.dss.client.widget.form;

public class CoordModeField {
    public static final String[] values = new String[] { "J2000"
    	                                               , "Galactic"
                                                       , "Ephemeris"
                                                       };

    public CoordModeField(String value) {
        this.value = value;
    }
    
    public String toString() {
        return value;
    }
    
    private final String value;
}
