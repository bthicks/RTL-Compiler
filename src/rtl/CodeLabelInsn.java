package rtl;

public class CodeLabelInsn extends AbstractInsn {

    public CodeLabelInsn(String expCode, int uid, int prevInsn, int nextInsn, int basicBlock) {
        super(expCode, uid, prevInsn, nextInsn, basicBlock);
    }

    @Override
    public String toString() {
        return Integer.toString(this.getUid()) + ": L" + Integer.toString(this.getUid()) + ":\n";
    }
}