package rtl;

import arm.BInsn;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

public class CallInsn extends AbstractInsn {

    private Value target;
    private List<Value> sources;
    private String function;

    public CallInsn(String expCode, int uid, int prevInsn, int nextInsn, int basicBlock,
                Value target, List<Value> sources, String function) {
        super(expCode, uid, prevInsn, nextInsn, basicBlock);
        this.target = target;
        this.sources = sources;
        this.function = function;
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

    @Override
    public String toDot() {
        List<String> sourceStrings = new ArrayList<>();

        for (Value source : sources) {
            sourceStrings.add(source.toString());
        }

        return "|\\ \\ \\ " + Integer.toString(this.getUid()) + ":\\ " + target.toString()
                + "=" + String.join("+", sourceStrings) + "\\l\\\n";
    }

    @Override
    public List<arm.Insn> toARM(HashMap<Integer, Integer> stack) {
        // TODO
        return new LinkedList<>();
    }
}