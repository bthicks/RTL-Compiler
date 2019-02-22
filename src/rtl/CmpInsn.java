package rtl;

import arm.ImmediateValue;
import arm.LdrInsn;
import arm.MovInsn;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

public class CmpInsn extends AbstractInsn {

    private List<Value> sources;

    public CmpInsn(String expCode, int uid, int prevInsn, int nextInsn, int basicBlock,
                       List<Value> sources) {
        super(expCode, uid, prevInsn, nextInsn, basicBlock);
        this.sources = sources;
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

        return Integer.toString(this.getUid()) + ": cc:CC="
                + String.join(",", sourceStrings) + "\n";
    }

    @Override
    public String toDot() {
        List<String> sourceStrings = new ArrayList<>();

        for (Value source : sources) {
            sourceStrings.add(source.toString());
        }

        return "|\\ \\ \\ " + Integer.toString(this.getUid()) + ":\\ cc:CC="
                + String.join(",", sourceStrings) + "\\l\\\n";
    }

    @Override
    public List<arm.Insn> toARM(HashMap<Integer, Integer> stack) {
        List<arm.Insn> insns = new LinkedList<>();
        arm.Value r0, r1;

        if (sources.get(0) instanceof RegisterValue) {
            r0 = new arm.RegisterValue(Integer.toString(sources.get(0).getValue()));
        } else {
            r0 = new arm.ImmediateValue(Integer.toString(sources.get(0).getValue()));
        }

        if (sources.get(1) instanceof RegisterValue) {
            r1 = new arm.RegisterValue(Integer.toString(sources.get(1).getValue()));
        } else {
            r1 = new arm.ImmediateValue(Integer.toString(sources.get(1).getValue()));
        }

        insns.add(new arm.CmpInsn(r0, r1, this.getUid()));

        return insns;
    }

    /*@Override
    public List<arm.Insn> toARM(HashMap<Integer, Integer> stack) {
        List<arm.Insn> insns = new LinkedList<>();

        // load sources into r0 and r1
        // cmp r0 and r1
        for (int i = 0; i < 2; i++) {
            Value source = sources.get(i);

            if (source instanceof RegisterValue) {
                String offset = Integer.toString(stack.get(source.getValue()));
                insns.add(new LdrInsn(new arm.RegisterValue(Integer.toString(i)), new ImmediateValue(offset), this.getUid()));
            } else {
                insns.add(new MovInsn(new arm.RegisterValue(Integer.toString(i)),
                        new arm.ImmediateValue(Integer.toString(source.getValue())), "", this.getUid()));
            }
        }
        insns.add(new arm.CmpInsn(new arm.RegisterValue("0"), new arm.RegisterValue("1"), this.getUid()));

        return insns;
    }*/
}
