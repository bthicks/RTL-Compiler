package arm;

import java.util.List;

public abstract class AbstractInsn implements Insn {

    protected int uid;

    protected String formatARM(String instruction, String parameters) {
        return String.format("\t%4s %15s %10s\n", instruction, parameters, "@ insn " + uid);
    }

    public abstract String toARM();

    //public abstract List<Integer> getTargets();

    //public abstract List<Integer> getSources();

    public void allocateTarget(String real) {}

    public void allocateSource(String virtual, String real) {}
}
