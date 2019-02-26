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

    public boolean isSpilled() {
        return spilled;
    }

    public boolean isReal() {
        return Integer.parseInt(this.value) <= 15;
    }
}
