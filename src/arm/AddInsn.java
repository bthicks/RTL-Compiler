package arm;

public class AddInsn extends AbstractInsn {
    private String r1;
    private String r2;
    private String operand2;

    public AddInsn(String r1, String r2, String operand2) {
        this.r1 = r1;
        this.r2 = r2;
        this.operand2 = operand2;
    }

    @Override
    public String toARM() {
        return "add\t" + r1 + ", " + r2 + ", " + operand2 + "\n";
    }

    @Override
    public void allocateTarget(String real) {

    }

    @Override
    public void allocateSource(String virtual, String real) {

    }
}
