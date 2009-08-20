package edu.nrao.dss.client;

class HourField {
    
    public HourField(String value) {
        this.value = value;
    }
    
    public String toString() {
        return value;
    }
    
    private final String value;
}
