package arm;

public class RegisterValue extends AbstractValue {
    private boolean spilled;

    @Deprecated
    public RegisterValue(String value, boolean spilled) {
        super(value);
        this.spilled = spilled;
    }

    public RegisterValue(String value) {
        super(value);
    }

    @Override
    public String toString() {
        return "r" + this.getValue();
    }

    @Override
    public boolean equals(Object o) {
        // self check
        if (this == o)
            return true;
        // null check
        if (o == null)
            return false;
        // type check and cast
        if (getClass() != o.getClass())
            return false;
        RegisterValue registerValue = (RegisterValue) o;
        // field comparison
        return value.equals(registerValue.value);
    }

    public boolean isSpilled() {
        return spilled;
    }

    public boolean isReal() {
        return Integer.parseInt(this.value) <= 15;
    }
}
