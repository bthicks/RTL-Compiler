package arm;

import java.util.List;

public interface Insn {
    public String toARM();

    public String getTarget();

    public List<String> getSources();

    public void allocateTarget(String real);

    public void allocateSource(String virtual, String real);
}
