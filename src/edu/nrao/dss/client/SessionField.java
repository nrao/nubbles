package edu.nrao.dss.client;

class SessionField {
    public SessionField(String value) {
        this.value = value;
    }

    public String toString() {
        return value;
    }
    
    private final String value;
}
