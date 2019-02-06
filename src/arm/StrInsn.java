package arm;

public class StrInsn extends AbstractInsn {
    private Value r1;
    private Value address;

    public StrInsn(Value r1, Value address, int uid) {
        this.r1 = r1;
        this.address = address;
        this.uid = uid;
    }

    @Override
    public String toARM() {
        if (address instanceof ImmediateValue) {
            return formatARM("str", r1.toString() + ", [fp, " + address.toString() + "]");
        }
        else {
            return formatARM("str", r1.toString() + ", [" + address.toString() + "]");
        }
    }

    @Override
    public void allocateTarget(String real) {

    }

    @Override
    public void allocateSource(String virtual, String real) {

    }
}
