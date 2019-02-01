package arm;

import java.util.List;

public class PushInsn extends AbstractInsn {
    private List<String> registers;

    public PushInsn(List<String> registers) {
        this.registers = registers;
    }

    @Override
    public String toARM() {
        return "push\t{" + String.join(",", registers) + "}\n";
    }

    @Override
    public void allocateTarget(String real) {

    }

    @Override
    public void allocateSource(String virtual, String real) {

    }
}
