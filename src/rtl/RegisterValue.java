package rtl;

public class RegisterValue extends AbstractValue {

    public RegisterValue(int value, int offset) {
        super(value, offset);
    }

    @Override
    public String toString() {
        int offset = this.getOffset();

        if (offset == 0) {
            return Integer.toString(this.getValue());
        } else {
            return "[" + this.getValue() + Integer.toString(offset) + "]";
        }
    }
}