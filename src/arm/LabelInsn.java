package arm;

public class LabelInsn extends AbstractInsn {
    private String label;

    public LabelInsn(String label) {
        this.label = label;
    }

    @Override
    public String toARM() {
        return ".L" + label + ":\n";
    }
}
