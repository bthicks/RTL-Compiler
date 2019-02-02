package arm;

public class MovInsn extends AbstractInsn {
    private Value r1;
    private Value operand2;
    private String condition;

    public MovInsn(Value r1, Value operand2, String condition) {
        this.r1 = r1;
        this.operand2 = operand2;
        this.condition = condition;
    }

    @Override
    public String toARM() {
        return "mov" + condition + "\t" + r1.toString() + ", " + operand2.toString() + "\n";
    }

    @Override
    public void allocateTarget(String real) {

    }

    @Override
    public void allocateSource(String virtual, String real) {

    }
}
