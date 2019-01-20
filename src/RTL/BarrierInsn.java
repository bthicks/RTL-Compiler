public class BarrierInsn extends AbstractInsn {

    public BarrierInsn(String expCode, int uid, int prevInsn, int nextInsn, int basicBlock) {
        super(expCode, uid, prevInsn, nextInsn, basicBlock);
    }

    @Override
    public String toString() {
        return "";
    }
}