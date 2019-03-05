package arm;

public class SpecialValue extends AbstractValue {

    public SpecialValue(String value) {
        super(value);
    }

    @Override
    public String toString() {
        return this.getValue();
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
        SpecialValue specialValue = (SpecialValue) o;
        // field comparison
        return value.equals(specialValue.value);
    }
}