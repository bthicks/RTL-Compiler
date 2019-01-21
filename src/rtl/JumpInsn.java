package RTL;

import java.util.ArrayList;
import java.util.List;

public class JumpInsn extends AbstractInsn {

    private Value target;
    private List<Value> sources;
    private int labelRef;

    public JumpInsn(String expCode, int uid, int prevInsn, int nextInsn, int basicBlock,
                    Value target, List<Value> sources, int labelRef) {
        super(expCode, uid, prevInsn, nextInsn, basicBlock);
        this.target = target;
        this.sources = sources;
        this.labelRef = labelRef;
    }

    public Value getTarget() {
        return target;
    }

    public List<Value> getSources() {
        return sources;
    }

    public int getLabelRef() {
        return labelRef;
    }

    @Override
    public String toString() {
        List<String> sourceStrings = new ArrayList<>();

        for (Value source : sources) {
            sourceStrings.add(source.toString());
        }

        return Integer.toString(this.getUid()) + ": " + target.toString()
                + "=" + String.join("+", sourceStrings) + " " + labelRef + "\n";
    }
}