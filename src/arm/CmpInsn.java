package arm;

import java.util.LinkedList;
import java.util.List;

public class CmpInsn extends AbstractInsn {
    private Value r1;
    private Value operand2;
    private List<String> sources;

    public CmpInsn(Value r1, Value operand2, int uid) {
        this.r1 = r1;
        this.operand2 = operand2;
        this.uid = uid;
        this.sources = new LinkedList<>();

        if (r1 instanceof RegisterValue) {
            sources.add(r1.getValue());
        }
        if (operand2 instanceof RegisterValue) {
            sources.add(operand2.getValue());
        }
    }

    @Override
    public String toARM() {
        return formatARM("cmp", r1.toString() + ", " + operand2.toString());
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
        if (r1.getValue().equals(virtual) && r1 instanceof RegisterValue) {
            r1.setValue(real);
        }
        if (operand2.getValue().equals(virtual) && operand2 instanceof RegisterValue) {
            operand2.setValue(real);
        }
    }
}
