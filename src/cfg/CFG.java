package cfg;

import arm.*;
import rtl.Insn;

import java.util.*;

public class CFG {
    private String functionName;
    private List<BasicBlock> basicBlocks;
    private int maxVirtualRegister;
    private IntfGraph intfGraph;
    private HashMap<String, String> registerMap;
    private Set<String> spilledRegisters;
    private Set<String> calleeSaved;
    private int spillOffset;

    public CFG(String functionName, int maxVirtualRegister) {
        this.functionName = functionName;
        this.basicBlocks = new LinkedList<>();
        this.maxVirtualRegister = maxVirtualRegister;
        this.registerMap = new HashMap<>();
        this.spilledRegisters = new HashSet<>();
        this.spillOffset = 0;
    }

    public IntfGraph getIntfGraph() {
        return intfGraph;
    }

    public Set<String> getCalleeSaved() {
        return calleeSaved;
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

    public Set<String> getSpilledRegisters() {
        return spilledRegisters;
    }

    public int getSpillOffset() {
        return spillOffset;
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

    public void printInsns() {
        for (BasicBlock block : this.basicBlocks) {
            for (arm.Insn insn : block.getArmInsns()) {
                System.out.println(insn.toARM());
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
            block.clearLiveRanges();
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

        registerMap.clear();

        // Registers r0-r12 available
        // r11 = fp, r12 = ip, r13 = sp, r14 = lr, r15 = pc
        for (int i = 0; i < 13; i++) {
            colors.add(Integer.toString(i));
        }

        // Convert interference graph to list and sort by number of edges
        LinkedList<Map.Entry<String, HashSet<String>>> intfList = new LinkedList<>(intfGraph.entrySet());
        Collections.sort(intfList, (a, b) -> b.getValue().size() - a.getValue().size());

        // Get most constrained register that hasn't been spilled
        String mostConstrained = getMostConstrainedReg(intfList);

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
                // Get list of available colors
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

    private String getMostConstrainedReg(LinkedList<Map.Entry<String, HashSet<String>>> intfList) {
        for (Map.Entry<String, HashSet<String>> entry : intfList) {
            String reg = entry.getKey();

            if (!spilledRegisters.contains(reg) && Integer.parseInt(reg) > 15) {
                return reg;
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
        calleeSaved = new LinkedHashSet<>();
        List<arm.Insn> redundantInsns = new LinkedList<>();

        for (BasicBlock basicBlock : basicBlocks) {
            for (arm.Insn insn : basicBlock.getArmInsns()) {
                String target = insn.getTarget();
                List<String> sources = insn.getSources();

                // change target register from virtual to real
                insn.allocateTarget(registerMap.get(target));
                if (target != null && Integer.parseInt(registerMap.get(target)) > 3) {
                    calleeSaved.add(registerMap.get(target));
                }

                // change source registers from virtual to real
                for (String source : sources) {
                    insn.allocateSource(source, registerMap.get(source));
                    if (source != null && Integer.parseInt(registerMap.get(source)) > 3) {
                        calleeSaved.add(registerMap.get(source));
                    }
                }

                // keep track of redundant instructions
                if (insn instanceof MovInsn && insn.getSources().size() > 0) {
                    target = registerMap.get(target);
                    String source = registerMap.get(sources.get(0));

                    if (target.equals(source)) {
                        redundantInsns.add(insn);
                    }
                }
            }

            // remove redundant insns
            for (arm.Insn insn : redundantInsns) {
                basicBlock.removeArmInsn(insn);
            }
        }
    }

    public void spillRegister(String spilled) {
        int spillNum = 0;

        for (BasicBlock basicBlock : basicBlocks) {
            List<arm.Insn> newInsns = new LinkedList<>();

            for (arm.Insn insn : basicBlock.getArmInsns()) {

                for (String source : insn.getSources()) {
                    if (source.equals(spilled)) {
                        String spilledboi = spilled + Integer.toString(spillNum++);

                        spilledRegisters.add(spilledboi);
                        insn.allocateSource(spilled, spilledboi);

                        // add load before this insn
                        newInsns.add(new LdrInsn(new RegisterValue(spilledboi),
                                new ImmediateValue(Integer.toString(spillOffset)), insn.getUid()));
                    }
                }

                newInsns.add(insn);

                if (insn.getTarget() != null && insn.getTarget().equals(spilled)) {
                    String spilledgorl = spilled + Integer.toString(spillNum++);

                    spilledRegisters.add(spilledgorl);
                    insn.allocateTarget(spilledgorl);

                    // add store after this insn
                    newInsns.add(new StrInsn(new RegisterValue(spilledgorl),
                            new ImmediateValue(Integer.toString(spillOffset)), insn.getUid()));
                }
            }

            basicBlock.setArmInsns(newInsns);
        }

        spillOffset += 4;
    }
}
