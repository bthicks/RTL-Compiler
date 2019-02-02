package arm;

public class BInsn extends AbstractInsn {
    private final Value label;

    public BInsn(Value label)
    {
        this.label = label;
    }

    @Override
    public String toARM()
    {
        return "b\t." + label.toString() + "\n";
    }
}
