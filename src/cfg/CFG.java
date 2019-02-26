package cfg;

import rtl.Insn;

import java.util.*;

public class CFG {
    private String functionName;
    private List<BasicBlock> basicBlocks;
    private int maxVirtualRegister;
    private IntfGraph intfGraph;
    private HashMap<String, String> registerMap;

    public CFG(String functionName, int maxVirtualRegister) {
        this.functionName = functionName;
        this.basicBlocks = new LinkedList<>();
        this.maxVirtualRegister = maxVirtualRegister;
        this.registerMap = new HashMap<>();
    }

    public String getFunctionName() {
        return functionName;
    }

    public List<BasicBlock> getBasicBlocks() {
        return basicBlocks;
    }

    public BasicBlock getBasicBlock(int label) {
        for (BasicBlock block : basicBlocks) {
            if (label == block.getLabel()) {
                return block;
            }
        }
        return null;
    }

    public int getMaxVirtualRegister() {
        return maxVirtualRegister;
    }

    public BasicBlock addBasicBlock(BasicBlock basicBlock) {
        for (BasicBlock block : basicBlocks) {
            if (block.getLabel() == basicBlock.getLabel()) {
                return block;
            }
        }
        basicBlocks.add(basicBlock);
        return basicBlock;
    }

    public boolean containsBasicBlock(int label) {
        for (BasicBlock block : basicBlocks) {
            if (label == block.getLabel()) {
                return true;
            }
        }
        return false;
    }

    public void addRtlInsn(Insn insn) {
        int label = insn.getBasicBlock();

        for (BasicBlock block : basicBlocks) {
            if (label == block.getLabel()) {
                block.addRtlInsn(insn);
                return;
            }
        }

        BasicBlock block = new BasicBlock(label);
        block.addRtlInsn(insn);
        this.addBasicBlock(block);
    }

    public void printCFG() {
        for (BasicBlock block : this.basicBlocks) {
            if (block.getLabel() == -1) {
                continue;
            }
            System.out.println(block.getLabel());
            System.out.print("Predecessors: ");
            for (BasicBlock predecessor : block.getPredecessors()) {
                System.out.print(predecessor.getLabel() + " ");
            }
            System.out.println();
            System.out.print("Successors: ");
            for (BasicBlock successor : block.getSuccessors()) {
                System.out.print(successor.getLabel() + " ");
            }
            System.out.println();

            for (Insn insn : block.getRtlInsns()) {
                System.out.print("\t" + insn.toString());
            }
        }
    }

    public void sortCFG() {
        Collections.sort(basicBlocks, (a, b) -> a.getLabel() - b.getLabel());
    }

    public void LVA() {
        boolean changed;

        // Reverse list of basic blocks to run algorithm from bottom up
        Collections.reverse(basicBlocks);

        // Generate def and use sets in each block
        for (BasicBlock block : basicBlocks) {
            block.generateDefAndUseSets();
        }

        // Dataflow iterative analysis
        do {
            changed = false;

            for (BasicBlock block : basicBlocks) {
                if (block.LVA()) {
                    changed = true;
                }
            }
        } while (changed);

        // Generate interference graph
        this.intfGraph = new IntfGraph(basicBlocks);

        // Un-reverse list
        Collections.reverse(basicBlocks);
    }

    public String colorGraph() {
        List<String> colors = new LinkedList<>();
        Stack<String> stack = new Stack<>();

        // Registers r0-r10 available
        // r11 = fp, r12 = ip, r13 = sp, r14 = lr, r15 = pc
        for (int i = 0; i < 13; i++) {
            colors.add(Integer.toString(i));
        }

        // Convert interference graph to list and sort by number of edges
        LinkedList<Map.Entry<String, HashSet<String>>> intfList = new LinkedList<>(intfGraph.entrySet());
        Collections.sort(intfList, (a, b) -> a.getValue().size() - b.getValue().size());
        String mostConstrained = intfList.peekFirst().getKey();

        while (!intfList.isEmpty()) {
            // Remove node with most edges from list
            Map.Entry<String, HashSet<String>> v1 = intfList.removeFirst();

            // Only remove real registers once all virtuals have been removed
            if (Integer.parseInt(v1.getKey()) <= 15 && containsVirtual(intfList)) {
                intfList.add(v1);
                continue;
            }

            // Remove edges between node and its neighbors
            for (Map.Entry<String, HashSet<String>> v2 : intfList) {
                v2.getValue().remove(v1);
            }

            // Push register number onto stack
            stack.push(v1.getKey());
        }

        HashMap<String, HashSet<String>> newGraph = new HashMap<>();

        while (!stack.isEmpty()) {
            String v1 = stack.pop();

            newGraph.put(v1, new HashSet<>());

            for (String v2 : intfGraph.get(v1)) {
                if (newGraph.containsKey(v2)) {
                    newGraph.get(v1).add(v2);
                    newGraph.get(v2).add(v1);
                }
            }

            // If real register, color as same register number
            if (Integer.parseInt(v1) <= 15) {
                registerMap.put(v1, v1);
            } else {
                // Get list of taken colors
                LinkedList<String> availableColors = new LinkedList<>(colors);
                for (String intf : newGraph.get(v1)) {
                    availableColors.remove(registerMap.get(intf));
                }

                if (availableColors.size() > 0) {
                    registerMap.put(v1, availableColors.getFirst());
                } else {  // Need to spill
                    return mostConstrained;
                }
            }
        }
        return null;
    }

    // Helper function for colorGraph
    private boolean containsVirtual(List<Map.Entry<String, HashSet<String>>> list) {
        for (Map.Entry<String, HashSet<String>> node : list) {
            if (Integer.parseInt(node.getKey()) > 15) {
                return true;
            }
        }
        return false;
    }

    public void allocateRegisters() {
        for (BasicBlock basicBlock : basicBlocks) {
            for (arm.Insn insn : basicBlock.getArmInsns()) {
                String target = insn.getTarget();
                List<String> sources = insn.getSources();

                // change target register from virtual to real
                insn.allocateTarget(registerMap.get(target));

                // change source registers from virtual to real
                for (String source : sources) {
                    insn.allocateSource(source, registerMap.get(source));
                }
            }
        }
    }
}
