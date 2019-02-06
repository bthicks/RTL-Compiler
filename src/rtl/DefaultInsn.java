package rtl;

import arm.*;
import arm.ImmediateValue;
import arm.RegisterValue;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

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

        switch (operation) {
            case "add":
                insns.add(new LdrInsn(r1, new ImmediateValue(Integer.toString(stack.get(sources.get(0).getValue())))));
                insns.add(new LdrInsn(r2, new ImmediateValue(Integer.toString(stack.get(sources.get(1).getValue())))));
                insns.add(new AddInsn(r0, r1, r2));
                insns.add(new StrInsn(r0, new ImmediateValue(Integer.toString(stack.get(target.getValue())))));
                break;
            case "load":
                insns.add(new LdrInsn(r0, new ImmediateValue(Integer.toString(stack.get(sources.get(0).getValue())))));
                insns.add(new StrInsn(r0, new ImmediateValue(Integer.toString(stack.get(target.getValue())))));
                break;
            case "move":
                //System.out.println(getUid() + ": " + sources.get(0).getValue());
                //insns.add(new MovInsn(r0, new ImmediateValue(Integer.toString(sources.get(0).getValue())), ""));
                //insns.add(new StrInsn(r0, new ImmediateValue(Integer.toString(stack.get(target.getValue())))));

                //insns.add(new LdrInsn(r1, new ImmediateValue(Integer.toString(sources.get(0).getValue()))));
                //insns.add(new MovInsn(r0, r1, ""));
                break;
            case "store":
                if (sources.get(0) instanceof rtl.RegisterValue) {
                    insns.add(new LdrInsn(r0, new ImmediateValue(Integer.toString(stack.get(sources.get(0).getValue())))));
                } else {
                    insns.add(new MovInsn(r0, new ImmediateValue(Integer.toString(sources.get(0).getValue())), ""));
                }

                if (target.getValue() == 105) {
                    int offset = target.getOffset();

                    // map RTL's memory address to a virtual register
                    if (stack.get(target.getValue() + offset / 4) == null) {
                        stack.put(target.getValue() + offset / 4, stack.get(sources.get(0).getValue()));
                    }

                    insns.add(new StrInsn(r0, new ImmediateValue(Integer.toString(stack.get(sources.get(0).getValue())))));
                } else {
                    insns.add(new StrInsn(r0, new ImmediateValue(Integer.toString(stack.get(target.getValue())))));
                }

                //System.out.println(getUid());

                //insns.add(new StrInsn(r0, new ImmediateValue(Integer.toString(stack.get(target.getValue())))));
                break;
        }

        return insns;
    }

    private static void remapValue(HashMap<Integer, Integer> stack, Value register) {
        if (!(register instanceof rtl.RegisterValue)) {
            return;
        }

        int value = register.getValue();
        int offset = register.getOffset();

        if (value == 105 && offset < 0) {
            register.setValue(stack.get(value + (offset / 4)));
        }
    }
}