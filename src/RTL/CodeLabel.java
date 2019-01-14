public class CodeLabel extends Insn {

    public CodeLabel(String expCode, int uid, int prevInsn, int nextInsn, int basicBlock) {
        super(expCode, uid, prevInsn, nextInsn, basicBlock);
    }

    @Override
    public String toString() {
        return Integer.toString(uid) + ": L" + Integer.toString(uid) + ":\n";
    }
}