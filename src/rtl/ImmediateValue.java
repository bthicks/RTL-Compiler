package rtl;

public class ImmediateValue extends AbstractValue {

    public ImmediateValue(int value, int offset) {
        super(value, offset);
    }

    @Override
    public String toString() {
        return Integer.toString(this.getValue());
    }
}