package arm;

public class LabelInsn extends AbstractInsn {
    private Value label;

    public LabelInsn(Value label) {
        this.label = label;
    }

    @Override
    public String toARM() {
        return label.toString() + ":\n";
    }
}
