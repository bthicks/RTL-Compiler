package rtl;

import java.util.HashMap;
import java.util.List;

public abstract class AbstractInsn implements Insn {

    private String expCode;
    private int uid;
    private int prevInsn;
    private int nextInsn;
    private int basicBlock;

    public AbstractInsn(String expCode, int uid, int prevInsn, int nextInsn, int basicBlock) {
        this.expCode = expCode;
        this.uid = uid;
        this.prevInsn = prevInsn;
        this.nextInsn = nextInsn;
        this.basicBlock = basicBlock;
    }

    public String getExpCode() {
        return expCode;
    }

    public int getUid() {
        return uid;
    }

    public int getPrevInsn() {
        return prevInsn;
    }

    public int getNextInsn() {
        return nextInsn;
    }

    public int getBasicBlock() {
        return basicBlock;
    }

    public abstract String toString();

    public abstract String toDot();

    public abstract List<arm.Insn> toARM(HashMap<Integer, Integer> stack);

    protected static void remapValue(Value register, HashMap<Integer, Integer> stack) {
        if (!(register instanceof rtl.RegisterValue)) {
            return;
        }

        int value = register.getValue();
        int offset = register.getOffset();

        if (stack.get(value + (offset / 4)) == null) {
            return;
        }

        if (value == 105 && offset < 0) {
            register.setValue(value + (offset / 4));
        }
    }
}