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
        if (condition.equals("l")) {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append(formatARM("push", "{r0, r1, r2, r3}"));
            stringBuilder.append(formatARM("b" + condition, label.toString()));
            stringBuilder.append(formatARM("pop", "{r0, r1, r2, r3}"));
            return stringBuilder.toString();
        }

        return formatARM("b" + condition, label.toString());
    }
}
