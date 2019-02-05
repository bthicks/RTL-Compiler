package arm;

public class SpecialValue extends AbstractValue {

    public SpecialValue(String value) {
        super(value);
    }

    @Override
    public String toString() {
        return this.getValue();
    }
}