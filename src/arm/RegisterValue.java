package arm;

public class RegisterValue extends AbstractValue {

    public RegisterValue(int value) {
        super(value);
    }

    @Override
    public String toString() {
        return "r" + this.getValue();
    }
}
