package arm;

public class LdrInsn extends AbstractInsn {
    private String r1;
    private String offset;

    public LdrInsn(String r1, String offset) {
        this.r1 = r1;
        this.offset = offset;
    }

    @Override
    public String toARM() {
        if (offset.startsWith("r")) {
            return "ldr\t" + r1 + ", [" + offset + "]\n";
        }
        else {
            return "ldr\t" + r1 + ", [fp, " + offset + "]\n";
        }
    }

    @Override
    public void allocateTarget(String real) {

    }

    @Override
    public void allocateSource(String virtual, String real) {

    }
}
