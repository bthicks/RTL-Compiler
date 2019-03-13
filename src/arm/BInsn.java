package arm;

import java.util.ArrayList;
import java.util.List;

public class BInsn extends AbstractInsn {
    private final Value label;
    private final String condition;
//    private String target;
//    private List<String> sources;

    public BInsn(Value label, String condition, int uid) {
        this.label = label;
        this.condition = condition;
        this.uid = uid;
//        this.sources = new ArrayList<>();

//        if (condition.equals("l")) {  // Call Insn
//            for (int i = 0; i < 4; i++) {
//                this.sources.add(Integer.toString(i));
//            }
//        }
    }

    @Override
    public String toARM() {
        return formatARM("b" + condition, label.toString());
    }

    public String getCondition() {
        return condition;
    }

    public boolean isConditional() {
        return !condition.equals("");
    }

//    @Override
//    public List<String> getSources() {
//        return sources;
//    }
}
