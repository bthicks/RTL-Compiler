public class Insn {

    private String expCode;
    private int uid;
    private int prevInsn;
    private int nextInsn;
    private int basicBlock;
    private String pattern;

    public Insn(String expCode, int uid, int prevInsn, int nextInsn) {
        this.expCode = expCode;
        this.uid = uid;
        this.prevInsn = prevInsn;
        this.nextInsn = nextInsn;
    }

    public Insn(String expCode, int uid, int prevInsn, int nextInsn, int basicBlock) {
        this.expCode = expCode;
        this.uid = uid;
        this.prevInsn = prevInsn;
        this.nextInsn = nextInsn;
        this.basicBlock = basicBlock;
    }

    public Insn(String expCode, int uid, int prevInsn, int nextInsn, int basicBlock,
                String pattern) {
        this.expCode = expCode;
        this.uid = uid;
        this.prevInsn = prevInsn;
        this.nextInsn = nextInsn;
        this.basicBlock = basicBlock;
        this.pattern = pattern;
    }

    public String getExpCode() {
        return expCode;
    }

    public int getInsnUid() {
        return insnUid;
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

    public String getPattern() {
        return pattern;
    }

    public String toString() {
        switch (pattern) {
            case "set": return "";
            case "use": return "";
        }
    }
}