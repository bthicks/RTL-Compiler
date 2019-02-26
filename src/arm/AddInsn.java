package arm;

import java.util.LinkedList;
import java.util.List;

public class AddInsn extends AbstractInsn {
    private Value r1;
    private Value r2;
    private Value operand2;
    private String target;
    private List<String> sources;

    public AddInsn(Value r1, Value r2, Value operand2, int uid) {
        this.r1 = r1;
        this.r2 = r2;
        this.operand2 = operand2;
        this.uid = uid;
        this.sources = new LinkedList<>();

        if (r1 instanceof RegisterValue) {
            this.target = r1.getValue();
        } else {
            this.r1 = null;
        }

        if (r2 instanceof RegisterValue) {
            sources.add(r2.getValue());
        }
        if (operand2 instanceof RegisterValue) {
            sources.add(operand2.getValue());
        }
    }

    @Override
    public String toARM() {
        return formatARM("add", r1.toString() + ", " + r2.toString() + ", " + operand2.toString());
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
        for (int i = 0; i < sources.size(); i++) {
            if (sources.get(i).equals(virtual)) {
                sources.set(i, real);
            }
        }

        if (r2.getValue().equals(virtual) && r2 instanceof RegisterValue) {
            r2.setValue(real);
        }
        if (operand2.getValue().equals(virtual) && operand2 instanceof RegisterValue) {
            operand2.setValue(real);
        }
    }
}
