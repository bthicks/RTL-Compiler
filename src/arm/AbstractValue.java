package arm;

public abstract class AbstractValue implements Value {
    private int value;

    public AbstractValue(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public abstract String toString();
}
