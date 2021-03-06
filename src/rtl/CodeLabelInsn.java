package rtl;

import arm.Insn;
import arm.LabelInsn;
import arm.LabelValue;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

public class CodeLabelInsn extends AbstractInsn {

    public CodeLabelInsn(String expCode, int uid, int prevInsn, int nextInsn, int basicBlock) {
        super(expCode, uid, prevInsn, nextInsn, basicBlock);
    }

    @Override
    public String toString() {
        return Integer.toString(this.getUid()) + ": L" + Integer.toString(this.getUid()) + ":\n";
    }

    @Override
    public String toDot() {
        return "|\\ \\ \\ " + Integer.toString(this.getUid()) + ":\\ L"
                + Integer.toString(this.getUid()) + ":\\l\\\n";
    }

    @Override
    public List<Insn> toARM(HashMap<Integer, Integer> stack) {
        List<arm.Insn> insns = new LinkedList<>();
        insns.add(new LabelInsn(new LabelValue(Integer.toString(this.getUid()))));

        return insns;
    }
}