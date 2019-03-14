package cfg;

import arm.*;

import java.util.*;

public class DAG {
    private Set<Node> dag;
    private arm.Insn label;

    public DAG(BasicBlock basicBlock) {
        this.dag = new HashSet<>();

        if (basicBlock.getArmInsns().size() > 0) {
            this.label = basicBlock.getArmInsns().remove(0);
        }

        Map<String, Node> defs = new HashMap<>();
        Set<Node> changesConditions = new HashSet<>();
        Map<String, Node> uses = new HashMap<>();

        for (arm.Insn insn : basicBlock.getArmInsns()) {
            Node node = new Node(insn, new HashMap<>(), new HashMap<>());

            System.out.println("UID: " + insn.getUid() + " --- defs: " + defs);
            System.out.println("UID: " + insn.getUid() + " --- uses: " + uses);

            if (insn instanceof CmpInsn || (insn instanceof BInsn && ((BInsn) insn).getCondition() != null && !((BInsn) insn).getCondition().equals("l"))) {
                for (Node conditionChanger : changesConditions) {
                    node.addPredecessor(conditionChanger, 0);
                    conditionChanger.addSuccessor(node, 0);
                }
            }

            if (insn instanceof StrInsn) {
                if (uses.containsKey(((StrInsn) insn).getAddress())) {
                    node.addPredecessor(uses.get(((StrInsn) insn).getAddress()), 0);
                    uses.get(((StrInsn) insn).getAddress()).addSuccessor(node, 0);
                    uses.remove(((StrInsn) insn).getAddress());
                }
                defs.put(((StrInsn) insn).getAddress(), node);
            }

            if (insn instanceof LdrInsn) {
                if (defs.containsKey(((LdrInsn) insn).getAddress())) {
                    node.addPredecessor(defs.get(((LdrInsn) insn).getAddress()), 0);
                    defs.get(((LdrInsn) insn).getAddress()).addSuccessor(node, 0);
                }
                uses.put(((LdrInsn) insn).getAddress(), node);
            }

            if (insn instanceof BInsn && ((BInsn) insn).getCondition().equals("l")) {
                for (int i = 0; i < 4; i++) {
                    String source = Integer.toString(i);
                    if (defs.containsKey(source)) {
                        int weight = defs.get(source).insn instanceof LdrInsn ? 3 : 1;
                        node.addPredecessor(defs.get(source), weight);
                        defs.get(source).addSuccessor(node, weight);

                    }

                    if (uses.containsKey(source)) {
                        int weight = 0;
                        node.addPredecessor(uses.get(source), weight);
                        uses.get(source).addSuccessor(node, weight);
                    }
                }

                for (int i = 0; i < 4; i++) {
                    uses.put(Integer.toString(i), node);
                    defs.put(Integer.toString(i), node);
                }
            } else {
                for (String source : insn.getSources()) {
                    if (defs.containsKey(source)) {
                        int weight = defs.get(source).insn instanceof LdrInsn ? 3 : 1;
                        node.addPredecessor(defs.get(source), weight);
                        defs.get(source).addSuccessor(node, weight);

                    }
                    if (uses.containsKey(insn.getTarget())) {
                        int weight = 0;
                        node.addPredecessor(uses.get(insn.getTarget()), weight);
                        uses.get(insn.getTarget()).addSuccessor(node, weight);
                    }
                }

                for (String source : insn.getSources()) {
                    uses.put(source, node);
                }

                defs.put(insn.getTarget(), node);
                uses.remove(insn.getTarget());
            }

            if (insn instanceof MovInsn || insn instanceof AddInsn || insn instanceof SubInsn || insn instanceof CmpInsn) {
                changesConditions.add(node);
            }

            this.dag.add(node);
        }
    }

    public List<arm.Insn> topographicalSort() {
        Queue<Node> readyList = new PriorityQueue<>();
        List<arm.Insn> schedule = new LinkedList<>();

        if (label != null) {
            schedule.add(label);
        }

        for (Node node : dag) {
            if (node.predecessors.isEmpty()) {
                readyList.add(node);
            }
        }

        for (Node node : readyList) {
            dag.remove(node);
        }

        while (!readyList.isEmpty()) {
            Node scheduled = readyList.remove();
            for (Node successor : scheduled.successors.keySet()) {
                successor.removePredesessor(scheduled);
            }

            schedule.add(scheduled.insn);

            for (Node node : dag) {
                if (node.predecessors.isEmpty()) {
                    readyList.add(node);
                }
            }

            for (Node node : readyList) {
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
