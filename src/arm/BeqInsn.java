package arm;

public class BeqInsn extends AbstractInsn {
    private final Value label;

    public BeqInsn(Value label)
    {
        this.label = label;
    }

    @Override
    public String toARM()
    {
        return "beq\t." + label.toString() + "\n";
    }
}
