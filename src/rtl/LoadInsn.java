package rtl;

import arm.ImmediateValue;
import arm.Insn;
import arm.LdrInsn;
import arm.RegisterValue;
import arm.StrInsn;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

public class LoadInsn extends AbstractInsn {
    private Value target;
    private List<Value> sources;

    public LoadInsn(String expCode, int uid, int prevInsn, int nextInsn, int basicBlock,
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
        arm.Value r0, r1;

        for (Value source : sources) {
            remapValue(source, stack);
        }
        remapValue(target, stack);

        r0 = new arm.RegisterValue(Integer.toString(target.getValue()));
        r1 = new arm.ImmediateValue(Integer.toString(stack.get(sources.get(0).getValue())));

        insns.add(new LdrInsn(r0, r1, this.getUid()));

        return insns;*/
        return new LinkedList<>();
    }

    /*@Override
    public List<Insn> toARM(HashMap<Integer, Integer> stack) {
        List<arm.Insn> insns = new LinkedList<>();
        arm.Value r0 = new arm.RegisterValue("0");

        for (Value source : sources) {
            remapValue(source, stack);
        }
        remapValue(target, stack);

        insns.add(new LdrInsn(r0, new ImmediateValue(Integer.toString(stack.get(sources.get(0).getValue()))), this.getUid()));
        if (!(target instanceof rtl.RegisterValue && target.getValue() <= 3)) {
            insns.add(new StrInsn(r0, new ImmediateValue(Integer.toString(stack.get(target.getValue()))), this.getUid()));
        }

        return insns;
    }*/
}
