package arm;

public class StrInsn extends AbstractInsn {
    private String r1;
    private String address;

    public StrInsn(String r1, String address) {
        this.r1 = r1;
        this.address = address;
    }

    public String toARM() {
        if (address.startsWith("#")) {
            return "str\t" + r1 + ", [fp, " + address + "]\n";
        }
        else {
            return "str\t" + r1 + ", [" + address + "]\n";
        }

    }
}
