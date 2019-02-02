package arm;

public class BltInsn extends AbstractInsn {
    private final Value label;

    public BltInsn(Value label)
    {
        this.label = label;
    }

    @Override
    public String toARM()
    {
        return "blt\t." + label.toString() + "\n";
    }
}
