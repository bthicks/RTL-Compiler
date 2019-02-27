package cfg;

import arm.Insn;

import java.util.*;

public class IntfGraph {

    private HashMap<String, HashSet<String>> intfGraph;

    public IntfGraph(List<BasicBlock> basicBlocks) {
        intfGraph = new LinkedHashMap<>();

        generateIntfGraph(basicBlocks);
    }

    public void add(String key) {
        intfGraph.put(key, new HashSet<>());
    }

    public void remove(String key) {
        intfGraph.remove(key);
    }

    public boolean containsKey(String key) {
        return intfGraph.containsKey(key);
    }

    public Set<String> keySet() {
        return intfGraph.keySet();
    }

    public Collection<HashSet<String>> values() {
        return intfGraph.values();
    }

    public HashSet<String> get(String key) {
        return intfGraph.get(key);
    }

    public boolean isEmpty() {
        return intfGraph.isEmpty();
    }

    private void generateIntfGraph(List<BasicBlock> basicBlocks) {
        for (BasicBlock block : basicBlocks) {
            List<Insn> insns = block.getArmInsns();
            Set<String> liveOut = block.getLiveOut();

            // Add each register in live out set to graph with edges between them
            for (String reg : liveOut) {
                if (!intfGraph.containsKey(reg)) {
                    intfGraph.put(reg, new HashSet<>());
                }

                for (String v2 : liveOut) {
                    addEdge(reg, v2);
                }
            }

            // Visit insns bottom->top
            Collections.reverse(insns);

            for (arm.Insn insn : insns) {
                String target = insn.getTarget();

                // Remove target from live set
                liveOut.remove(target);

                if (target != null) {
                    // Add target node to graph
                    if (!intfGraph.containsKey(target)) {
                        intfGraph.put(target, new HashSet<>());
                    }
                    // Add edge from target (v1) to each element in live set (v2)
                    for (String v2 : liveOut) {
                        addEdge(target, v2);
                    }
                }

                // Add each source to live set
                liveOut.addAll(insn.getSources());
            }

            // Un-reverse insns
            Collections.reverse(insns);
        }
    }

    public Set<Map.Entry<String, HashSet<String>>> entrySet() {
        return intfGraph.entrySet();
    }

    public void addEdge(String v1, String v2) {
        if (!intfGraph.containsKey(v1)) {
            intfGraph.put(v1, new HashSet<>());
        }
        if (!intfGraph.containsKey(v2)) {
            intfGraph.put(v2, new HashSet<>());
        }

        intfGraph.get(v1).add(v2);
        intfGraph.get(v2).add(v1);
    }

    public void removeEdge(String v1, String v2) {
        intfGraph.get(v1).remove(v2);
        intfGraph.get(v2).remove(v1);
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();

        stringBuilder.append("IntfGraph:\n");
        for (String key : intfGraph.keySet()) {
            stringBuilder.append(key).append(": ");

            for (String val : intfGraph.get(key)) {
                stringBuilder.append(val).append(" ");
            }
            stringBuilder.append("\n");
        }

        return stringBuilder.toString();
    }
}
