package arm;

import java.util.LinkedList;
import java.util.List;

public class StrInsn extends AbstractInsn {
    private Value r1;
    private Value address;
    private List<String> sources;

    public StrInsn(Value r1, Value address, int uid) {
        this.r1 = r1;
        this.address = address;
        this.uid = uid;
        this.sources = new LinkedList<>();

        if (r1 instanceof RegisterValue) {
            sources.add(r1.getValue());
        }
        if (address instanceof RegisterValue) {
            sources.add(address.getValue());
        }
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
    public List<String> getSources() {
        return sources;
    }

    @Override
    public void allocateSource(String virtual, String real) {
        for (String source : sources) {
            if (source.equals(virtual)) {
                source = real;
            }
        }
    }
}
