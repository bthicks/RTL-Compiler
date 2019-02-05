package arm;

public class ImmediateValue extends AbstractValue {

    public ImmediateValue(int value) {
        super(value);
    }

    @Override
    public String toString() {
        return "#" + this.getValue();
    }
}
