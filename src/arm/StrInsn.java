package arm;

public class StrInsn extends AbstractInsn {
    private Value r1;
    private Value address;

    public StrInsn(Value r1, Value address) {
        this.r1 = r1;
        this.address = address;
    }

    @Override
    public String toARM() {
        if (address instanceof ImmediateValue) {
            return "\tstr\t" + r1.toString() + ", [fp, " + address.toString() + "]\n";
        }
        else {
            return "\tstr\t" + r1.toString() + ", [" + address.toString() + "]\n";
        }
    }

    @Override
    public void allocateTarget(String real) {

    }

    @Override
    public void allocateSource(String virtual, String real) {

    }
}
