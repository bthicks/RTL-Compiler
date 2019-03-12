package cfg;

import arm.BInsn;
import arm.LdrInsn;

import java.util.*;

public class DAG {
    private Set<Node> dag;

    public DAG(BasicBlock basicBlock) {
        this.dag = new HashSet<>();
        Map<String, Node> defs = new HashMap<>();

        for (arm.Insn insn : basicBlock.getArmInsns()) {
            Node node = new Node(insn, new HashMap<>(), new HashMap<>());

            if (insn instanceof BInsn) {
                for (int i = 0; i < 4; i++) {
                    String source = Integer.toString(i);
                    if (defs.containsKey(source)) {
                        int weight = defs.get(source).insn instanceof LdrInsn ? 3 : 1;
                        node.addPredecessor(defs.get(source), weight);
                        defs.get(source).addSuccessor(node, weight);
                    }
                }

                defs.put("0", node);
            } else {
                for (String source : insn.getSources()) {
                    if (defs.containsKey(source)) {
                        int weight = defs.get(source).insn instanceof LdrInsn ? 3 : 1;
                        node.addPredecessor(defs.get(source), weight);
                        defs.get(source).addSuccessor(node, weight);
                    }
                }

                defs.put(insn.getTarget(), node);
            }

            this.dag.add(node);
        }
    }

    public List<arm.Insn> topographicalSort() {
        Queue<Node> readyList = new PriorityQueue<>();
        List<arm.Insn> schedule = new LinkedList<>();

        for (Node node : dag) {
            if (node.predecessors.isEmpty()) {
                readyList.add(node);
            }
        }

        for (Node node : readyList) {
            for (Node successor : node.successors.keySet()) {
                successor.removePredesessor(node);
            }
            dag.remove(node);
        }
        while (!readyList.isEmpty()) {
            schedule.add(readyList.remove().insn);

            for (Node node : dag) {
                if (node.predecessors.isEmpty()) {
                    readyList.add(node);
                }
            }

            for (Node node : readyList) {
                for (Node successor : node.successors.keySet()) {
                    successor.removePredesessor(node);
                }
                dag.remove(node);
            }
        }

        System.out.println(schedule);

        return schedule;
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

    private class Node implements Comparable<Node> {
        private arm.Insn insn;
        private Map<Node, Integer> predecessors;
        private Map<Node, Integer> successors;

        private Node(arm.Insn insn, Map<Node, Integer> predecessors, Map<Node, Integer> successors) {
            this.insn = insn;
            this.predecessors = predecessors;
            this.successors = successors;
        }

        public void removePredesessor(Node insn) {
            this.predecessors.remove(insn);
        }

        public void addPredecessor(Node insn, Integer weight) {
            this.predecessors.put(insn, weight);
        }

        public void addSuccessor(Node insn, Integer weight) {
            this.successors.put(insn, weight);
        }

        public int longestPathToLeaf() {
            int total = 0;

            if (successors.isEmpty()) {
                return total;
            }

            for (Node node : successors.keySet()) {
                total = Math.max(total, successors.get(node) + node.longestPathToLeaf());
            }

            return total;
        }

        public int getNumSuccessors() {
            return successors.size();
        }

        @Override
        public String toString() {
            StringBuilder stringBuilder = new StringBuilder();

            stringBuilder.append(insn.getUid()).append(": ");

            for (Node node : successors.keySet()) {
                stringBuilder.append("(").append(node.insn.getUid()).append(", ").append(successors.get(node)).append(") ");
            }

            stringBuilder.append("\n");

            return stringBuilder.toString();
        }

        @Override
        public int compareTo(Node o) {
            int longestPathDifference = o.longestPathToLeaf() - this.longestPathToLeaf();
            if (longestPathDifference != 0) {
                return longestPathDifference;
            }

            int numSucessorsDifference = o.getNumSuccessors() - this.getNumSuccessors();
            if (numSucessorsDifference != 0) {
                return numSucessorsDifference;
            }

            Random random = new Random();
            return random.nextInt(3) - 1;  // Randomly generate a number from [-1, 1].
        }
    }
}
