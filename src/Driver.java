import java.util.List;

import cfg.BasicBlock;
import cfg.CFG;
import rtl.CodeLabelInsn;
import rtl.Insn;
import rtl.JumpInsn;

public class Driver {

    public static void main(String[] args) {
        String filename = "fib/fib.json";
        List<Insn> insns = JSONParser.parse(filename);
        CFG cfg = generateCFG("main", insns);

        cfg.printCFG();
    }

    private static CFG generateCFG(String functionName, List<Insn> insns) {
        CFG cfg = new CFG(functionName);

        for (Insn insn : insns) {
            cfg.addInsn(insn);

            if (insn.getNextInsn() == 0 || insn.getExpCode().equals("barrier")) {
                continue;
            }
            Insn next = getInsn(insn.getNextInsn(), insns);

            if (insn instanceof JumpInsn) {
                BasicBlock predecessor = cfg.getBasicBlock(insn.getBasicBlock());
                int label = ((JumpInsn) insn).getLabelRef();

                BasicBlock successor = cfg.addBasicBlock(new BasicBlock(getBasicBlock(label, insns)));
                predecessor.addSuccessor(successor);
                successor.addPredecessor((predecessor));
            }
            if (insn.getBasicBlock() != next.getBasicBlock() && next.getBasicBlock() != -1) {
                BasicBlock predecessor = cfg.getBasicBlock(insn.getBasicBlock());
                int nextBlock = next.getBasicBlock();

                BasicBlock successor = cfg.addBasicBlock(new BasicBlock(nextBlock));
                predecessor.addSuccessor(successor);
                successor.addPredecessor((predecessor));
            }
        }

        return cfg;
    }

    private static Insn getInsn(int uid, List<Insn> insns) {
        for (Insn insn : insns) {
            if (insn.getUid() == uid) {
                return insn;
            }
        }
        return null;
    }

    private static int getBasicBlock(int label, List<Insn> insns) {
        for (Insn insn : insns) {
            if (insn instanceof CodeLabelInsn && insn.getUid() == label) {
                return insn.getBasicBlock();
            }
        }
        return -1;
    }
}