package arm;

public class BInsn extends AbstractInsn {
    private final Value label;
    private final String condition;

    public BInsn(Value label, String condition)
    {
        this.label = label;
        this.condition = condition;
    }

    @Override
    public String toARM()
    {
        return "\tb" + condition + "\t." + label.toString() + "\n";
    }
}
