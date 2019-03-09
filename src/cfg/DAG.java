package cfg;

import java.util.*;

public class DAG {
    private Set<Node> dag;

    public DAG(BasicBlock basicBlock) {
        this.dag = new HashSet<>();
        Map<String, Node> defs = new HashMap<>();

        for (arm.Insn insn : basicBlock.getArmInsns()) {
            Node node = new Node(insn, new HashMap<>(), new HashMap<>());

            for (String source : insn.getSources()) {
                if (defs.containsKey(source)) {
                    node.addPredecessor(defs.get(source), 1);
                    defs.get(source).addSuccessor(node, 1);
                }
            }

            defs.put(insn.getTarget(), node);
            this.dag.add(node);
        }
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();

        for (Node node : dag) {
            stringBuilder.append(node.toString());
        }

        return stringBuilder.toString();
    }

    public Set<Node> getDag() {
        return dag;
    }

    private class Node {
        private arm.Insn insn;
        private Map<Node, Integer> predecessors;
        private Map<Node, Integer> successors;

        private Node(arm.Insn insn, Map<Node, Integer> predecessors, Map<Node, Integer> successors) {
            this.insn = insn;
            this.predecessors = predecessors;
            this.successors = successors;
        }

        public void addPredecessor(Node insn, Integer weight) {
            this.predecessors.put(insn, weight);
        }

        public void addSuccessor(Node insn, Integer weight) {
            this.successors.put(insn, weight);
        }

        @Override
        public String toString() {
            StringBuilder stringBuilder = new StringBuilder();

            stringBuilder.append(insn.getUid()).append(": ");

            for (Node node : successors.keySet()) {
                stringBuilder.append(node.insn.getUid()).append(" ");
            }

            stringBuilder.append("\n");

            return stringBuilder.toString();
        }
    }
}
