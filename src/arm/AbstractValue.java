package arm;

public abstract class AbstractValue implements Value {
    protected String value;

    public AbstractValue(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public abstract String toString();

    public abstract boolean equals(Object o);
}
