public class JumpInsn extends Insn {

    private String labelRef;

    public JumpInsn(String expCode, int uid, int prevInsn, int nextInsn, int basicBlock,
                    String labelRef) {
        super(expCode, uid, prevInsn, nextInsn, basicBlock);
        this.labelRef = labelRef;
    }

    public String getLabelRef() {
        return labelRef;
    }

    @Override
    public String toString() {
        return Integer.toString(uid) + ": pc=" + labelRef + "\n";
    }
}