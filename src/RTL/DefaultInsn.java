import java.util.ArrayList;
import java.util.List;

public class DefaultInsn extends AbstractInsn {

    private AbstractValue target;
    private List<AbstractValue> sources;

    public DefaultInsn(String expCode, int uid, int prevInsn, int nextInsn, int basicBlock,
                AbstractValue target, List<AbstractValue> sources) {
        super(expCode, uid, prevInsn, nextInsn, basicBlock);
        this.target = target;
        this.sources = sources;
    }

    public AbstractValue getTarget() {
        return target;
    }

    public List<AbstractValue> getSources() {
        return sources;
    }

    @Override
    public String toString() {
        List<String> sourceStrings = new ArrayList<>();

        for (AbstractValue source : sources) {
            sourceStrings.add(source.toString());
        }

        return Integer.toString(this.getUid()) + ": " + target.toString()
                + "=" + String.join("+", sourceStrings) + "\n";
    }
}