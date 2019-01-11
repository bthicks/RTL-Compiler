import java.util.ArrayList;

public class Instruction {

    private String uid;
    private String prevInsn;
    private String nextInsn;
    private String basicBlock;
    private String target;
    private List<String> sources;

    public Instruction(String uid, String prevInsn, String nextInsn, String basicBlock,
                       String target, List<String> sources) {
        this.uid = uid;
        this.prevInsn = prevInsn;
        this.nextInsn = nextInsn;
        this.basicBlock = basicBlock;
        this.target = target;
        this.sources = sources;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getPrevInsn() {
        return prevInsn;
    }

    public void setPrevInsn(String prevInsn) {
        this.prevInsn = prevInsn;
    }

    public String getNextInsn() {
        return nextInsn;
    }

    public void setNextInsn(String nextInsn) {
        this.nextInsn = nextInsn;
    }

    public String getBasicBlock() {
        return basicBlock;
    }

    public void setBasicBlock(String basicBlock) {
        this.basicBlock = basicBlock;
    }
}