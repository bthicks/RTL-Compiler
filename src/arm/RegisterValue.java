package arm;

public class RegisterValue extends AbstractValue {

    public RegisterValue(String value) {
        super(value);
    }

    @Override
    public String toString() {
        return "r" + this.getValue();
    }
}
