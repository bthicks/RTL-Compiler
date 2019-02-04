package rtl;

import java.util.List;

public interface Insn {

    public String getExpCode();

    public int getUid();

    public int getPrevInsn();

    public int getNextInsn();

    public int getBasicBlock();

    public List<arm.Insn> getARMInsns();

    public String toString();

    public String toDot();

    public void generateARMInsns();

}