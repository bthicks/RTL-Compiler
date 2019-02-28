package rtl;

import arm.ImmediateValue;
import arm.Insn;
import arm.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

public class SubInsn extends AbstractInsn {
    private Value target;
    private List<Value> sources;

    public SubInsn(String expCode, int uid, int prevInsn, int nextInsn, int basicBlock,
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
        arm.Value r0, r1, r2;

        r0 = new arm.RegisterValue(Integer.toString(target.getValue()), false);
        r1 = new arm.RegisterValue(Integer.toString(sources.get(0).getValue()), false);

        if (sources.get(1) instanceof RegisterValue) {
            r2 = new arm.RegisterValue(Integer.toString(sources.get(1).getValue()), false);
        } else {
            r2 = new arm.ImmediateValue(Integer.toString(sources.get(1).getValue()));
        }

        insns.add(new arm.SubInsn(r0, r1, r2, this.getUid()));

        return insns;
    }

    /*@Override
    public List<Insn> toARM(HashMap<Integer, Integer> stack) {
        List<arm.Insn> insns = new LinkedList<>();
        arm.Value r0 = new arm.RegisterValue("0");
        arm.Value r1 = new arm.RegisterValue("1");
        arm.Value r2 = new arm.RegisterValue("2");

        for (Value source : sources) {
            remapValue(source, stack);
        }
        remapValue(target, stack);
        System.out.println(target.getValue());
        for (Value source : sources) {
            System.out.println(source.getValue());
        }
        insns.add(new LdrInsn(r1, new ImmediateValue(Integer.toString(stack.get(sources.get(0).getValue()))), this.getUid()));

        if (sources.get(1) instanceof rtl.RegisterValue) {
            insns.add(new LdrInsn(r2, new ImmediateValue(Integer.toString(stack.get(sources.get(1).getValue()))), this.getUid()));
        } else {
            insns.add(new MovInsn(r2, new ImmediateValue(Integer.toString(sources.get(1).getValue())), "", this.getUid()));
        }

        insns.add(new arm.AddInsn(r0, r1, r2, this.getUid()));
        insns.add(new StrInsn(r0, new ImmediateValue(Integer.toString(stack.get(target.getValue()))), this.getUid()));

        return insns;
    }*/
}
