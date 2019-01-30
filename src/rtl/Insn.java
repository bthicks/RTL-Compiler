package rtl;

public interface Insn {

    public String getExpCode();

    public int getUid();

    public int getPrevInsn();

    public int getNextInsn();

    public int getBasicBlock();

    public String toString();

    public String toDot();

    public String toARM();
}