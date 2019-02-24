package arm;

import java.util.LinkedList;
import java.util.List;

public class MovInsn extends AbstractInsn {
    private Value r1;
    private Value operand2;
    private String condition;
    private String target;
    private List<String> sources;

    public MovInsn(Value r1, Value operand2, String condition, int uid) {
        this.r1 = r1;
        this.operand2 = operand2;
        this.condition = condition;
        this.uid = uid;
        this.sources = new LinkedList<>();

        if (r1 instanceof RegisterValue) {
            target = r1.getValue();
        } else {
            target = null;
        }

        if (operand2 instanceof RegisterValue) {
            sources.add(operand2.getValue());
        }
    }

    @Override
    public String toARM() {
        return formatARM("mov" + condition, r1.toString() + ", " + operand2.toString());
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
        target = real;
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
