package rtl;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

public class BarrierInsn extends AbstractInsn {

    public BarrierInsn(String expCode, int uid, int prevInsn, int nextInsn, int basicBlock) {
        super(expCode, uid, prevInsn, nextInsn, basicBlock);
    }

    @Override
    public String toString() {
        return "";
    }

    @Override
    public String toDot() {
        return "";
    }

    @Override
    public List<arm.Insn> toARM(HashMap<Integer, Integer> stack) {
        return new LinkedList<>();
    }
}