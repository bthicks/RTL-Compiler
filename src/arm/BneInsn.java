package arm;

public class BneInsn extends AbstractInsn {
    private final Value label;

    public BneInsn(Value label)
    {
        this.label = label;
    }

    @Override
    public String toARM()
    {
        return "bne\t." + label.toString() + "\n";
    }
}
