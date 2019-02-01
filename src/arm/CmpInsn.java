package arm;

public class CmpInsn extends AbstractInsn {
    private String r1;
    private String operand2;

    public CmpInsn(String r1, String operand2) {
        this.r1 = r1;
        this.operand2 = operand2;
    }

    @Override
    public String toARM() {
        return "cmp\t" + r1 + ", " + operand2 + "\n";
    }

    @Override
    public void allocateTarget(String real) {

    }

    @Override
    public void allocateSource(String virtual, String real) {

    }
}
