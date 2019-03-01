package arm;

public class BInsn extends AbstractInsn {
    private final Value label;
    private final String condition;
    private String target;

    public BInsn(Value label, String condition, int uid) {
        this.label = label;
        this.condition = condition;
        this.uid = uid;

        if (condition.equals("l")) {
            this.target = "0";
        } else {
            this.target = null;
        }
    }

    @Override
    public String toARM() {
        return formatARM("b" + condition, label.toString());
    }

    @Override
    public String getTarget() {
        return target;
    }
}
