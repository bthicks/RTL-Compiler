package arm;

import java.util.List;

public class PopInsn extends AbstractInsn {
    private List<Value> registers;

    public PopInsn(List<Value> registers) {
        this.registers = registers;
    }

    @Override
    public String toARM() {
        return "pop\t{" + String.join(",", registers.toString()) + "}\n";
    }

    @Override
    public void allocateTarget(String real) {

    }

    @Override
    public void allocateSource(String virtual, String real) {

    }
}
