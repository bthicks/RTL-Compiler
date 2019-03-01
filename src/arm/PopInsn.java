package arm;

import java.util.List;

public class PopInsn extends AbstractInsn {
    private List<Value> registers;

    public PopInsn(List<Value> registers) {
        this.registers = registers;
    }

    @Override
    public String toARM() {
        String s = String.join(", ", registers.toString()).replaceAll("\\[|\\]", "");

        return "\tpop\t{" + s + "}\n";
    }

    @Override
    public void allocateTarget(String real) {

    }

    @Override
    public void allocateSource(String virtual, String real) {

    }
}
