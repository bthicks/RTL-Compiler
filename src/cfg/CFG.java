package cfg;

import rtl.Insn;

import java.util.LinkedList;
import java.util.List;

public class CFG {

    private String functionName;
    private List<BasicBlock> basicBlocks;
    private int maxVirtualRegister;

    public CFG(String functionName, int maxVirtualRegister) {
        this.functionName = functionName;
        this.basicBlocks = new LinkedList<>();
        this.maxVirtualRegister = maxVirtualRegister;
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
}
