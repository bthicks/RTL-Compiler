package arm;

public class CmpInsn extends AbstractInsn {
    private Value r1;
    private Value operand2;

    public CmpInsn(Value r1, Value operand2, int uid) {
        this.r1 = r1;
        this.operand2 = operand2;
        this.uid = uid;
    }

    @Override
    public String toARM() {
        return formatARM("cmp", r1.toString() + ", " + operand2.toString());
    }

    @Override
    public void allocateTarget(String real) {

    }

    @Override
    public void allocateSource(String virtual, String real) {

    }
}
