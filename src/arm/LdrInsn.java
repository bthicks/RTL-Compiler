package arm;

public class LdrInsn extends AbstractInsn {
    private Value r1;
    private Value offset;

    public LdrInsn(Value r1, Value offset, int uid) {
        this.r1 = r1;
        this.offset = offset;
        this.uid = uid;
    }

    @Override
    public String toARM() {
        if (offset instanceof RegisterValue) {
            return formatARM("ldr", r1.toString() + ", [" + offset.toString() + "]");
        }
        else {
            return formatARM("ldr", r1.toString() + ", [fp, " + offset.toString() + "]");
        }
    }

    @Override
    public void allocateTarget(String real) {

    }

    @Override
    public void allocateSource(String virtual, String real) {

    }
}
