package cfg;

import arm.Insn;

import java.util.*;

public class IntfGraph {

    private Map<String, HashSet<String>> intfGraph;

    // TODO: Add color field?
    public IntfGraph(List<BasicBlock> basicBlocks) {
        intfGraph = new HashMap<>();

        generateIntfGraph(basicBlocks);
    }

    public Map<String, HashSet<String>> getIntfGraph() {
        return intfGraph;
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

    /**
     * Remove an edge from the interference graph.
     * This method will also remove connections from other nodes to the returned key.
     *
     * @param key -- The key of the element in the graph.
     * @return A Node representing the element.
     */
    public Node removeEdge(String key) {
        Set<String> edges = intfGraph.remove(key);

        for (String edge : edges) {
            HashSet<String> adjacentEdges = intfGraph.get(edge);
            adjacentEdges.remove(key);
            intfGraph.put(edge, adjacentEdges);
        }

        return new Node(key, edges);
    }

    /**
     * Get the most constrained vertex (vertex with most edges) from the intfGraph.
     *
     * @return The most constrained vertex if there are nodes in the graph, else null.
     *
     * TODO:
     *  - Remove virtual registers first?
     *  - Add max constraint before needing to spill.
     */
    public String getMostConstrained() {
        int maxConstraints = Integer.MIN_VALUE;
        String maxConstraintKey = null;

        for (String key : intfGraph.keySet()) {
            if (maxConstraints < intfGraph.get(key).size()) {
                maxConstraintKey = key;
            }
        }

        return maxConstraintKey;
    }

    class Node {
        public String key;
        public Set<String> edges;

        public Node(String key, Set<String> edges) {
            this.key = key;
            this.edges = edges;
        }
    }
}
