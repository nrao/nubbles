package edu.nrao.dss.client.widget.form;

public class STypeField {
    public static final String[] values = new String[] { "open"
                                                       , "windowed"
                                                       , "fixed"
                                                       , "elective"
                                                       };

    public STypeField(String value) {
        this.value = value;
    }
    
    public String toString() {
        return value;
    }
    
    private final String value;
}
