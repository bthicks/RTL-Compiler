public abstract class AbstractValue implements Value {

    private String value;
    private int offset;

    public AbstractValue(String value, int offset) {
        this.value = value;
        this.offset = offset;
    }

    public String getValue() {
        return value;
    }

    public int getOffset() {
        return offset;
    }

    public abstract String toString();
}