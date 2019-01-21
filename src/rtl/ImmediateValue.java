package rtl;

public class ImmediateValue extends AbstractValue {

    public ImmediateValue(String value, int offset) {
        super(value, offset);
    }

    @Override
    public String toString() {
        return this.getValue();
    }
}