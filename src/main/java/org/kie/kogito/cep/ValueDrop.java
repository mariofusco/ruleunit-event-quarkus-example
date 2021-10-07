package org.kie.kogito.cep;

public class ValueDrop {
    private String company;
    private long oldValue;
    private long newValue;

    public ValueDrop() { }

    public ValueDrop(String company, long oldValue, long newValue) {
        this.company = company;
        this.oldValue = oldValue;
        this.newValue = newValue;
    }

    public String getCompany() {
        return company;
    }

    public long getOldValue() {
        return oldValue;
    }

    public long getNewValue() {
        return newValue;
    }

    public long getDropAmount() {
        return oldValue - newValue;
    }
}
