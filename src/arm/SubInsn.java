package arm;

public class SubInsn extends AbstractInsn {
    private Value r1;
    private Value r2;
    private Value operand2;

    public SubInsn(Value r1, Value r2, Value operand2, int uid) {
        this.r1 = r1;
        this.r2 = r2;
        this.operand2 = operand2;
        this.uid = uid;
    }

    @Override
    public String toARM() {
        return formatARM("sub", r1.toString() + ", " + r2.toString() + ", " + operand2.toString());
    }

    @Override
    public void allocateTarget(String real) {

    }

    @Override
    public void allocateSource(String virtual, String real) {

    }
}
