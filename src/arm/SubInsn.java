package arm;

public class SubInsn extends AbstractInsn {
    private Value r1;
    private Value r2;
    private Value operand2;

    public SubInsn(Value r1, Value r2, Value operand2) {
        this.r1 = r1;
        this.r2 = r2;
        this.operand2 = operand2;
    }

    @Override
    public String toARM() {
        return "\tsub\t" + r1.toString() + ", " + r2.toString() + ", " + operand2.toString() + "\n";
    }

    @Override
    public void allocateTarget(String real) {

    }

    @Override
    public void allocateSource(String virtual, String real) {

    }
}
