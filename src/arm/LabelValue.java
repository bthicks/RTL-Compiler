package arm;

public class LabelValue extends AbstractValue {

    public LabelValue(int value) {
        super(value);
    }

    @Override
    public String toString() {
        return "L" + Integer.toString(this.getValue());
    }
}
