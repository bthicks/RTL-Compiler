package arm;

public interface Insn {
    public String toARM();

    //public Value getTarget();

    //public List<Value> getSources();

    public void allocateTarget(String real);

    public void allocateSource(String virtual, String real);
}
