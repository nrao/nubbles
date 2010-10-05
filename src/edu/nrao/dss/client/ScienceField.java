package edu.nrao.dss.client;

class ScienceField {
    public static final String[] values = new String[] { "radar"
                                                       , "vlbi"
    	                                               , "pulsar"
                                                       , "continuum"
                                                       , "spectral line"
                                                       , "maintenance"
                                                       , "calibration"
                                                       , "testing"
                                                       , "commissioning"
                                                       };

    public ScienceField(String value) {
        this.value = value;
    }
    
    public String toString() {
        return value;
    }
    
    private final String value;
}
