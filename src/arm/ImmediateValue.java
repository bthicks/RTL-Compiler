package arm;

public class ImmediateValue extends AbstractValue {

    public ImmediateValue(String value) {
        super(value);
    }

    @Override
    public String toString() {
        return "#" + this.getValue();
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
        ImmediateValue immediateValue = (ImmediateValue) o;
        // field comparison
        return value.equals(immediateValue.value);
    }
}
