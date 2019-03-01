package arm;

import java.util.ArrayList;
import java.util.List;

public abstract class AbstractInsn implements Insn {

    protected int uid;

    protected String formatARM(String instruction, String parameters) {
        return String.format("\t%-4s\t%-20s\t%-10s\n", instruction, parameters, "@ insn " + uid);
    }

    public abstract String toARM();

    public int getUid() {
        return uid;
    }

    public String getTarget() {
        return null;
    }

    public List<String> getSources() {
        return new ArrayList<>();
    }

    public void allocateTarget(String real) {}

    public void allocateSource(String virtual, String real) {}
}
