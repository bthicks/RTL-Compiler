package cfg;

import arm.Insn;

import java.util.*;

public class IntfGraph {

    private HashMap<String, HashSet<String>> intfGraph;

    public IntfGraph(List<BasicBlock> basicBlocks) {
        intfGraph = new HashMap<>();

        generateIntfGraph(basicBlocks);
    }

    private void generateIntfGraph(List<BasicBlock> basicBlocks) {
        for (BasicBlock block : basicBlocks) {
            List<Insn> insns = block.getArmInsns();
            Set<String> liveOut = block.getLiveOut();

            // Add each register in live out set to graph
            for (String reg : liveOut) {
                if (!intfGraph.containsKey(reg)) {
                    intfGraph.put(reg, new HashSet<>());
                }
            }

            // Visit insns bottom->top
            Collections.reverse(insns);

            for (arm.Insn insn : insns) {
                // Remove target from live set
                liveOut.remove(insn.getTarget());

                // Add edge from target (v1) to each element in live set (v2)
                for (String v2 : liveOut) {
                    addEdge(insn.getTarget(), v2);
                }

                // Add each source to live set
                liveOut.addAll(insn.getSources());
            }

            // Un-reverse insns
            Collections.reverse(insns);
        }
    }

    private void addEdge(String v1, String v2) {
        if (!intfGraph.containsKey(v1)) {
            intfGraph.put(v1, new HashSet<>());
        }
        if (!intfGraph.containsKey(v2)) {
            intfGraph.put(v2, new HashSet<>());
        }

        intfGraph.get(v1).add(v2);
        intfGraph.get(v2).add(v1);
    }
}
