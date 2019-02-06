package rtl;

import arm.BInsn;
import arm.LabelValue;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

public class JumpInsn extends AbstractInsn {

    private Value target;
    private List<Value> sources;
    private int labelRef;
    private String condition;

    public JumpInsn(String expCode, int uid, int prevInsn, int nextInsn, int basicBlock,
                    Value target, List<Value> sources, int labelRef, String condition) {
        super(expCode, uid, prevInsn, nextInsn, basicBlock);
        this.target = target;
        this.sources = sources;
        this.labelRef = labelRef;
        this.condition = condition;
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

    public String getCondition() {
        return condition;
    }

    @Override
    public String toString() {
        List<String> sourceStrings = new ArrayList<>();

        /*for (Value source : sources) {
            sourceStrings.add(source.toString());
        }*/

        /*return Integer.toString(this.getUid()) + ": cc:CC" + target.toString()
                + "=" + String.join("+", sourceStrings) + "\n";*/
        return Integer.toString(this.getUid()) + ": cc:CC=L" + Integer.toString(labelRef)
                + " cond=" + condition + " \n";
    }

    @Override
    public String toDot() {
        List<String> sourceStrings = new ArrayList<>();

        for (Value source : sources) {
            String nonescaped = source.toString();
            StringBuilder escaped = new StringBuilder();

            for (int i = 0; i < nonescaped.length(); i++) {
                if (nonescaped.charAt(i) == '{' || nonescaped.charAt(i) == '}'
                        || nonescaped.charAt(i) == '<' || nonescaped.charAt(i) == '>') {
                    escaped.append("\\");
                }
                escaped.append(nonescaped.charAt(i));
            }

            sourceStrings.add(escaped.toString());
        }

        return "|\\ \\ \\ " + Integer.toString(this.getUid()) + ":\\ " + target.toString()
                + "=" + String.join("+", sourceStrings) + "\\l\\\n";
    }

    @Override
    public List<arm.Insn> toARM(HashMap<Integer, Integer> stack) {
        List<arm.Insn> insns = new LinkedList<>();
        insns.add(new BInsn(new arm.LabelValue(Integer.toString(labelRef)), condition, this.getUid()));

        return insns;
    }
}