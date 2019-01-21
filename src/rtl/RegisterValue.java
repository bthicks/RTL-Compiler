package RTL;

public class RegisterValue extends AbstractValue {

    public RegisterValue(String value, int offset) {
        super(value, offset);
    }

    @Override
    public String toString() {
        int offset = this.getOffset();

        if (offset == 0) {
            return this.getValue();
        } else {
            return "[" + this.getValue() + "-" + Integer.toString(offset) + "]";
        }
    }
}