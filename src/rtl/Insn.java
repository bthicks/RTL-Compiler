package rtl;

import java.util.HashMap;
import java.util.List;

public interface Insn {

    public String getExpCode();

    public int getUid();

    public int getPrevInsn();

    public int getNextInsn();

    public int getBasicBlock();

    public String toString();

    public String toDot();

    public List<arm.Insn> toARM(HashMap<Integer, Integer> stack);

}