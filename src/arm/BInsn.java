package arm;

public class BInsn extends AbstractInsn {
    private final Value label;
    private final String condition;

    public BInsn(Value label, String condition, int uid) {
        this.label = label;
        this.condition = condition;
        this.uid = uid;
    }

    @Override
    public String toARM() {
        return formatARM("b" + condition, label.toString());
    }
}
