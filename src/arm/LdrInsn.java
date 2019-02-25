package arm;

import java.util.LinkedList;
import java.util.List;

public class LdrInsn extends AbstractInsn {
    private Value r1;
    private Value offset;
    private String target;
    private List<String> sources;

    public LdrInsn(Value r1, Value offset, int uid) {
        this.r1 = r1;
        this.offset = offset;
        this.uid = uid;
        this.sources = new LinkedList<>();

        if (r1 instanceof RegisterValue) {
            target = r1.getValue();
        } else {
            target = null;
        }

        if (offset instanceof RegisterValue) {
            sources.add(offset.getValue());
        }
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
    public String getTarget() {
        return target;
    }

    @Override
    public List<String> getSources() {
        return sources;
    }

    @Override
    public void allocateTarget(String real) {
        this.r1.setValue(real);
        this.target = real;
    }

    @Override
    public void allocateSource(String virtual, String real) {
        for (String source : sources) {
            if (source.equals(virtual)) {
                source = real;
            }
        }
        if (offset.getValue().equals(virtual) && offset instanceof RegisterValue) {
            offset.setValue(real);
        }
    }
}
