package arm;

public class BgeInsn extends AbstractInsn {
    private final Value label;

    public BgeInsn(Value label)
    {
        this.label = label;
    }

    @Override
    public String toARM()
    {
        return "bge\t." + label.toString() + "\n";
    }
}
