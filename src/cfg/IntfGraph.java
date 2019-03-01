package cfg;

import arm.BInsn;
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
            Set<String> liveSet = new HashSet<>(block.getLiveOut());

            // Add each register in live out set to graph with edges between them
            for (String reg : liveSet) {
                if (!intfGraph.containsKey(reg)) {
                    intfGraph.put(reg, new HashSet<>());
                }

                for (String v2 : liveSet) {
                    addEdge(reg, v2);
                }
            }

            // Visit insns bottom->top
            Collections.reverse(insns);

            for (arm.Insn insn : insns) {
                String target = insn.getTarget();

                // Remove target from live set
                liveSet.remove(target);

                if (target != null) {
                    // Add target node to graph
                    if (!intfGraph.containsKey(target)) {
                        intfGraph.put(target, new HashSet<>());
                    }
                    // Add edge from target (v1) to each element in live set (v2)
                    for (String v2 : liveSet) {
                        addEdge(target, v2);
                    }
                }

                // Add each source to live set
                for (String source : insn.getSources()) {
                    liveSet.add(source);

                    // Add source node to graph
                    if (!intfGraph.containsKey(source)) {
                        intfGraph.put(source, new HashSet<>());
                    }
                }

                // Add interferences for call insns
                if (insn instanceof BInsn && ((BInsn) insn).getCondition().equals("l")) {
                    for (int i = 0; i < 4; i++) {
                        if (!intfGraph.containsKey(Integer.toString(i))) {
                            intfGraph.put(Integer.toString(i), new HashSet<>());
                        }

                        for (int j = i + 1; j < 4; j++) {
                            addEdge(Integer.toString(i), Integer.toString(j));
                        }

                        for (String v2 : liveSet) {
                            addEdge(Integer.toString(i), v2);
                        }
                    }
                }
            }

            // Un-reverse insns
            Collections.reverse(insns);
        }
    }

    public Set<Map.Entry<String, HashSet<String>>> entrySet() {
        return intfGraph.entrySet();
    }

    public void addEdge(String v1, String v2) {
        if (v1.equals(v2)) {
            return;
        }

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
