package rtl;

public abstract class AbstractValue implements Value {

    private int value;
    private int offset;

    public AbstractValue(int value, int offset) {
        this.value = value;
        this.offset = offset;
    }

    public int getValue() {
        return value;
    }

    public int getOffset() {
        return offset;
    }

    public abstract String toString();

    public void setValue(int value) {
        this.value = value;
    }
}