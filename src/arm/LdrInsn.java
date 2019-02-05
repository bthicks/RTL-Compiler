package arm;

public class LdrInsn extends AbstractInsn {
    private Value r1;
    private Value offset;

    public LdrInsn(Value r1, Value offset) {
        this.r1 = r1;
        this.offset = offset;
    }

    @Override
    public String toARM() {
        if (offset instanceof RegisterValue) {
            return "\tldr\t" + r1.toString() + ", [" + offset.toString() + "]\n";
        }
        else {
            return "\tldr\t" + r1.toString() + ", [fp, " + offset.toString() + "]\n";
        }
    }

    @Override
    public void allocateTarget(String real) {

    }

    @Override
    public void allocateSource(String virtual, String real) {

    }
}
