public class Barrier extends Insn {

    public Barrier(String expCode, int uid, int prevInsn, int nextInsn) {
        super(expCode, uid, prevInsn, nextInsn);
    }

    @Override
    public String toString() {
        return "";
    }
}