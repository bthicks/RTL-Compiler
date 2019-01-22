package rtl;

import java.util.ArrayList;
import java.util.List;

public class CallInsn extends AbstractInsn {

    private Value target;
    private List<Value> sources;

    public CallInsn(String expCode, int uid, int prevInsn, int nextInsn, int basicBlock,
                Value target, List<Value> sources) {
        super(expCode, uid, prevInsn, nextInsn, basicBlock);
        this.target = target;
        this.sources = sources;
    }

    public Value getTarget() {
        return target;
    }

    public List<Value> getSources() {
        return sources;
    }

    @Override
    public String toString() {
        List<String> sourceStrings = new ArrayList<>();

        for (Value source : sources) {
            sourceStrings.add(source.toString());
        }

        return Integer.toString(this.getUid()) + ": " + target.toString()
                + "=" + String.join("+", sourceStrings) + "\n";
    }
}