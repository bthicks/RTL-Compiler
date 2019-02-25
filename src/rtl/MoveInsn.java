package rtl;

import arm.Insn;
import arm.MovInsn;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

public class MoveInsn extends AbstractInsn {
    private Value target;
    private List<Value> sources;

    public MoveInsn(String expCode, int uid, int prevInsn, int nextInsn, int basicBlock,
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
        List<arm.Insn> insns = new LinkedList<>();
        arm.Value r0, r1;

        r0 = new arm.RegisterValue(Integer.toString(target.getValue()), false);

        if (sources.get(0) instanceof RegisterValue) {
            r1 = new arm.RegisterValue(Integer.toString(sources.get(0).getValue()), false);
        } else {
            r1 = new arm.ImmediateValue(Integer.toString(sources.get(0).getValue()));
        }

        insns.add(new MovInsn(r0, r1, "", this.getUid()));

        return insns;
    }

    /*@Override
    public List<Insn> toARM(HashMap<Integer, Integer> stack) {
        List<arm.Insn> insns = new LinkedList<>();
        arm.Value r0 = new arm.RegisterValue("0");
        arm.Value r1 = new arm.RegisterValue("1");

        for (Value source : sources) {
            remapValue(source, stack);
        }
        remapValue(target, stack);

        if (sources.get(0) instanceof rtl.RegisterValue) {
            if (target.getValue() <= 3) {
                insns.add(new LdrInsn(r1, new ImmediateValue(Integer.toString(stack.get(sources.get(0).getValue()))), this.getUid()));
                insns.add(new MovInsn(r0, r1, "", this.getUid()));
            } else {
                insns.add(new LdrInsn(r1, new ImmediateValue(Integer.toString(stack.get(sources.get(0).getValue()))), this.getUid()));
                insns.add(new MovInsn(r0, r1, "", this.getUid()));
                insns.add(new StrInsn(r0, new ImmediateValue(Integer.toString(stack.get(target.getValue()))), this.getUid()));
            }
        } else {
            insns.add(new MovInsn(r0, new ImmediateValue(Integer.toString(sources.get(0).getValue())), "", this.getUid()));
            insns.add(new StrInsn(r0, new ImmediateValue(Integer.toString(stack.get(target.getValue()))), this.getUid()));
        }

        return insns;
    }*/
}
