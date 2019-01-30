package arm;

public class MovInsn extends AbstractInsn {
    private String r1;
    private String operand2;

    public MovInsn(String r1, String operand2) {
        this.r1 = r1;
        this.operand2 = operand2;
    }

    public String toARM() {
        return "mov\t" + r1 + ", " + operand2 + "\n";
    }

}
