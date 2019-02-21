package cfg;

import java.util.*;

public class BasicBlock {

    private int label;
    private List<rtl.Insn> rtlInsns;
    private List<arm.Insn> armInsns;
    private Set<BasicBlock> predecessors;
    private Set<BasicBlock> successors;
    private Set<String> liveIn;
    private Set<String> liveOut;
    private Set<String> defs;
    private Set<String> uses;

    public BasicBlock(int label) {
        this.label = label;
        this.rtlInsns = new LinkedList<>();
        this.armInsns = new LinkedList<>();
        this.predecessors = new LinkedHashSet<>();
        this.successors = new LinkedHashSet<>();
        this.liveIn = new HashSet<>();
        this.liveOut = new HashSet<>();
        this.defs = new HashSet<>();
        this.uses = new HashSet<>();
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

    public Set<String> getLiveIn() {
        return liveIn;
    }

    public Set<String> getLiveOut() {
        return liveOut;
    }

    public Set<String> getDefs() {
        return defs;
    }

    public Set<String> getUses() {
        return uses;
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

    public void generateDefAndUseSets() {
        for (arm.Insn insn : armInsns) {
            for (String source : insn.getSources()) {
                if (!defs.contains(source)) {
                    uses.add(source);
                }
            }
            if (insn.getTarget() != null) {
                defs.add(insn.getTarget());
            }
        }
    }

    public boolean LVA() {
        Set<String> prevLiveIn = liveIn;
        Set<String> prevLiveOut = liveOut;

        for (BasicBlock successor : successors) {
            liveOut.addAll(successor.getLiveIn());
        }

        liveIn.addAll(liveOut);
        liveIn.removeAll(defs);
        liveIn.addAll(uses);


        // Return true if either the live in or live out set changed
        return ((liveIn != prevLiveIn) || (liveOut != prevLiveOut));
    }
}
