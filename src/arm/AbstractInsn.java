package arm;

import java.util.List;

public abstract class AbstractInsn implements Insn {

    protected int uid;

    public abstract String toARM();

    //public abstract List<Integer> getTargets();

    //public abstract List<Integer> getSources();

    public void allocateTarget(String real) {}

    public void allocateSource(String virtual, String real) {}
}
