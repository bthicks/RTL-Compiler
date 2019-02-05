package cfg;

import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

public class BasicBlock {

    private int label;
    private List<rtl.Insn> rtlInsns;
    private List<arm.Insn> armInsns;
    private Set<BasicBlock> predecessors;
    private Set<BasicBlock> successors;

    public BasicBlock(int label) {
        this.label = label;
        this.rtlInsns = new LinkedList<>();
        this.armInsns = new LinkedList<>();
        this.predecessors = new LinkedHashSet<>();
        this.successors = new LinkedHashSet<>();
    }

    public int getLabel() {
        return label;
    }

    public List<rtl.Insn> getRtlInsns() {
        return rtlInsns;
    }

    public List<arm.Insn> getArmInsns() {
        return armInsns;
    }

    public Set<BasicBlock> getPredecessors() {
        return predecessors;
    }

    public Set<BasicBlock> getSuccessors() {
        return successors;
    }

    public void addRtlInsn(rtl.Insn insn) {
        rtlInsns.add(insn);
    }

    public void addArmInsn(arm.Insn insn) {
        armInsns.add(insn);
    }

    public void addPredecessor(BasicBlock predecessor) {
        predecessors.add(predecessor);
    }

    public void addSuccessor(BasicBlock successor) {
        successors.add(successor);
    }
}
