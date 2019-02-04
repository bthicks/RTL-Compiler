package rtl;

import java.util.LinkedList;
import java.util.List;

public abstract class AbstractInsn implements Insn {

    private String expCode;
    private int uid;
    private int prevInsn;
    private int nextInsn;
    private int basicBlock;
    private List<arm.Insn> armInsns;

    public AbstractInsn(String expCode, int uid, int prevInsn, int nextInsn, int basicBlock) {
        this.expCode = expCode;
        this.uid = uid;
        this.prevInsn = prevInsn;
        this.nextInsn = nextInsn;
        this.basicBlock = basicBlock;
        this.armInsns = new LinkedList<>();
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

    public List<arm.Insn> getARMInsns() {
        return armInsns;
    }

    public abstract String toString();

    public abstract String toDot();

    public abstract void generateARMInsns();
}