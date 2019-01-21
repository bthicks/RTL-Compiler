package cfg;

import rtl.Insn;

import java.util.LinkedList;
import java.util.List;

public class CFG {

    private String functionName;
    private List<BasicBlock> basicBlocks;

    public CFG(String functionName) {
        this.functionName = functionName;
        this.basicBlocks = new LinkedList<>();
    }

    public String getFunctionName() {
        return functionName;
    }

    public List<BasicBlock> getBasicBlocks() {
        return basicBlocks;
    }

    public void addInsn(Insn insn) {
        int label = insn.getBasicBlock();

        for (BasicBlock block : basicBlocks) {
            if (label == block.getLabel()) {
                block.addInsn(insn);
                return;
            }
        }

        BasicBlock block = new BasicBlock(label);
        block.addInsn(insn);
        this.addBasicBlock(block);
    }

    public void addBasicBlock(BasicBlock basicBlock) {
        basicBlocks.add(basicBlock);
    }

    public boolean containsBasicBlock(int label) {
        for (BasicBlock block : basicBlocks) {
            if (label == block.getLabel()) {
                return true;
            }
        }
        return false;
    }
}
