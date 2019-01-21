package cfg;

import rtl.Insn;

import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

public class BasicBlock {

    private int label;
    private List<Insn> insns;
    private Set<BasicBlock> predecessors;
    private Set<BasicBlock> successors;

    public BasicBlock(int label) {
        this.label = label;
        this.insns = new LinkedList<>();
        this.predecessors = new LinkedHashSet<>();
        this.successors = new LinkedHashSet<>();
    }

    public int getLabel() {
        return label;
    }

    public List<Insn> getInsns() {
        return insns;
    }

    public Set<BasicBlock> getPredecessors() {
        return predecessors;
    }

    public Set<BasicBlock> getSuccessors() {
        return successors;
    }

    public void addInsn(Insn insn) {
        insns.add(insn);
    }

    public void addPredecessor(BasicBlock predecessor) {
        predecessors.add(predecessor);
    }

    public void addSuccessor(BasicBlock successor) {
        successors.add(successor);
    }
}
