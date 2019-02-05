package arm;

public class LabelValue extends AbstractValue {

    public LabelValue(String value) {
        super(value);
    }

    @Override
    public String toString() {
        return "L" + this.getValue();
    }
}
