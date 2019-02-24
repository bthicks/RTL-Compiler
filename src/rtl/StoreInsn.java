package rtl;

import arm.ImmediateValue;
import arm.Insn;
import arm.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

public class StoreInsn extends AbstractInsn {
    private Value target;
    private List<Value> sources;

    public StoreInsn(String expCode, int uid, int prevInsn, int nextInsn, int basicBlock,
                     Value target, List<Value> sources) {
        super(expCode, uid, prevInsn, nextInsn, basicBlock);
        this.target = target;
        this.sources = sources;
    }

    @Override
    public String toString() {
        List<String> sourceStrings = new ArrayList<>();

        for (Value source : sources) {
            sourceStrings.add(source.toString());
        }

        return this.getUid() + ": " + target.toString() + "=" + String.join("+", sourceStrings) + "\n";
    }

    @Override
    public String toDot() {
        return null;
    }

    @Override
    public List<Insn> toARM(HashMap<Integer, Integer> stack) {
        /*List<arm.Insn> insns = new LinkedList<>();
        arm.Value r0 = new arm.RegisterValue("0");
        arm.Value r1 = new arm.RegisterValue("1");

        for (Value source : sources) {
            remapValue(source, stack);
        }
        remapValue(target, stack);

        if (sources.get(0) instanceof rtl.RegisterValue) {
            insns.add(new LdrInsn(r0, new ImmediateValue(Integer.toString(stack.get(sources.get(0).getValue()))), this.getUid()));
        } else {
            insns.add(new MovInsn(r0, new ImmediateValue(Integer.toString(sources.get(0).getValue())), "", this.getUid()));
        }

        if (target.getValue() == 105) {
            int offset = target.getOffset();

            // map RTL's memory address to a virtual register
            if (stack.get(target.getValue() + offset / 4) == null) {
                stack.put(target.getValue() + offset / 4, stack.get(sources.get(0).getValue()));
            }

            insns.add(new StrInsn(r0, new ImmediateValue(Integer.toString(stack.get(sources.get(0).getValue()))), this.getUid()));
        } else {
            insns.add(new StrInsn(r0, new ImmediateValue(Integer.toString(stack.get(target.getValue()))), this.getUid()));
        }

        return insns;*/
        return new LinkedList<>();
    }
}
