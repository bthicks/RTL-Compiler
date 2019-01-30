package rtl;

import java.util.ArrayList;
import java.util.List;

public class CmpInsn extends AbstractInsn {

    private List<Value> sources;

    public CmpInsn(String expCode, int uid, int prevInsn, int nextInsn, int basicBlock,
                       List<Value> sources) {
        super(expCode, uid, prevInsn, nextInsn, basicBlock);
        this.sources = sources;
    }

    public List<Value> getSources() {
        return sources;
    }

    @Override
    public String toString() {
        List<String> sourceStrings = new ArrayList<>();

        for (Value source : sources) {
            sourceStrings.add(source.toString());
        }

        return Integer.toString(this.getUid()) + ": cc:CC="
                + String.join(",", sourceStrings) + "\n";
    }

    @Override
    public String toDot() {
        List<String> sourceStrings = new ArrayList<>();

        for (Value source : sources) {
            sourceStrings.add(source.toString());
        }

        return "|\\ \\ \\ " + Integer.toString(this.getUid()) + ":\\ cc:CC="
                + String.join(",", sourceStrings) + "\\l\\\n";
    }
}
