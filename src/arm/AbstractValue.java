package arm;

public abstract class AbstractValue implements Value {
    private String value;

    public AbstractValue(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public abstract String toString();
}
