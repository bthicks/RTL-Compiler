package arm;

import java.util.List;

public abstract class AbstractInsn implements Insn {

    public abstract String toARM();

    //public abstract List<Integer> getTargets();

    //public abstract List<Integer> getSources();

    //public abstract void allocateTarget(String real);

    //public abstract void allocateSource(String virtual, String real);
}
