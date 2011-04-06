package edu.nrao.dss.client.widget.form;

public class CoordModeField {
    public static final String[] values = new String[] { "J2000"
    	                                               , "B1950"
                                                       , "Galactic"
                                                       , "RaDecOfDate"
                                                       , "AzEl"
                                                       , "HaDec"
                                                       , "ApparentRaDec"
                                                       , "CableWrap"
                                                       , "Encoder"
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