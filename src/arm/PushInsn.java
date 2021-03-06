package arm;

import java.util.List;

public class PushInsn extends AbstractInsn {
    private List<Value> registers;

    public PushInsn(List<Value> registers) {
        this.registers = registers;
    }

    @Override
    public String toARM() {
        String s = String.join(", ", registers.toString()).replaceAll("\\[|\\]", "");

        return "\tpush\t{" + s + "}\n";
    }

    @Override
    public void allocateTarget(String real) {

    }

    @Override
    public void allocateSource(String virtual, String real) {

    }
}
