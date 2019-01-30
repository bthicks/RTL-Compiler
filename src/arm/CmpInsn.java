package arm;

public class CmpInsn extends AbstractInsn {
    private String r1;
    private String operand2;

    public CmpInsn(String r1, String operand2) {
        this.r1 = r1;
        this.operand2 = operand2;
    }

    public String toARM() {
        return "cmp\t" + r1 + ", " + operand2 + "\n";
    }
}
