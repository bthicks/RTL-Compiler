package arm;

public class LabelValue extends AbstractValue {

    public LabelValue(String value) {
        super(value);
    }

    @Override
    public String toString() {
        return "L" + this.getValue();
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
        LabelValue labelValue = (LabelValue) o;
        // field comparison
        return value.equals(labelValue.value);
    }
}
