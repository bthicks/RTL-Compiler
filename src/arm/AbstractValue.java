package arm;

public abstract class AbstractValue {
    private int value;

    public AbstractValue(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public abstract String toString();
}
