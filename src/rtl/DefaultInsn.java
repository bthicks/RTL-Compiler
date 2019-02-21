package rtl;

import arm.*;
import arm.ImmediateValue;
import arm.RegisterValue;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

@Deprecated
public class DefaultInsn extends AbstractInsn {

    private Value target;
    private List<Value> sources;
    private String operation;

    public DefaultInsn(String expCode, int uid, int prevInsn, int nextInsn, int basicBlock,
                Value target, List<Value> sources, String operation) {
        super(expCode, uid, prevInsn, nextInsn, basicBlock);
        this.target = target;
        this.sources = sources;
        this.operation = operation;
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
        List<arm.Insn> insns = new LinkedList<>();
        arm.Value r0 = new arm.RegisterValue("0");
        arm.Value r1 = new arm.RegisterValue("1");
        arm.Value r2 = new arm.RegisterValue("2");

        for (Value source : sources) {
            remapValue(source, stack);
        }
        remapValue(target, stack);

        switch (operation) {
            case "add":
                insns.add(new LdrInsn(r1, new ImmediateValue(Integer.toString(stack.get(sources.get(0).getValue()))), this.getUid()));

                if (sources.get(1) instanceof rtl.RegisterValue) {
                    insns.add(new LdrInsn(r2, new ImmediateValue(Integer.toString(stack.get(sources.get(1).getValue()))), this.getUid()));
                } else {
                    insns.add(new MovInsn(r2, new ImmediateValue(Integer.toString(sources.get(1).getValue())), "", this.getUid()));
                }

                insns.add(new arm.AddInsn(r0, r1, r2, this.getUid()));
                insns.add(new StrInsn(r0, new ImmediateValue(Integer.toString(stack.get(target.getValue()))), this.getUid()));
                break;
            case "load":
                insns.add(new LdrInsn(r0, new ImmediateValue(Integer.toString(stack.get(sources.get(0).getValue()))), this.getUid()));
                if (target instanceof rtl.RegisterValue && target.getValue() <= 3) {
                    //insns.add(new StrInsn(r0, new RegisterValue(Integer.toString(target.getValue()))));
                } else {
                    insns.add(new StrInsn(r0, new ImmediateValue(Integer.toString(stack.get(target.getValue()))), this.getUid()));
                }
                break;
            case "store":
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
                break;
        }

        return insns;
    }
}