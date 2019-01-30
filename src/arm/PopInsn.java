package arm;

import java.util.List;

public class PopInsn extends AbstractInsn {
    private List<String> registers;

    public PopInsn(List<String> registers) {
        this.registers = registers;
    }

    public String toARM() {
        return "pop\t{" + String.join(",", registers) + "}\n";
    }
}
