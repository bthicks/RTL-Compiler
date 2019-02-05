import java.util.LinkedList;
import java.util.List;

import cfg.ARMGenerator;
import cfg.BasicBlock;
import cfg.CFG;
import rtl.CodeLabelInsn;
import rtl.Insn;
import rtl.JumpInsn;

public class Driver {

    public static void main(String[] args) {
        if (args.length != 1) {
            System.out.println("Usage: java Driver <filename>");
            System.exit(0);
        }

        String filename = args[0];
        List<Insn> insns = JSONParser.parse(filename);
        int maxVirtualRegister = JSONParser.parseMaxRegister(filename);

        CFG cfg = generateCFG("main", insns, maxVirtualRegister);
        List<CFG> program = new LinkedList<>();
        program.add(cfg);

        //DotGenerator.toDot(filename, cfg);
        ARMGenerator.toARM(program);
        ARMGenerator.writeARM(filename, program);

        cfg.printCFG();
    }

    private static CFG generateCFG(String functionName, List<Insn> insns, int maxVirtualRegister) {
        CFG cfg = new CFG(functionName, maxVirtualRegister);
        cfg.addBasicBlock(new BasicBlock(0));
        cfg.addBasicBlock(new BasicBlock(1));

        for (rtl.Insn insn : insns) {
            cfg.addRtlInsn(insn);

            // ignore successors for last insn and barrier insns
            if (insn.getNextInsn() == 0 || insn.getExpCode().equals("barrier")) {
                continue;
            }

            Insn next = getInsn(insn.getNextInsn(), insns);
            BasicBlock predecessor = cfg.getBasicBlock(insn.getBasicBlock());
            BasicBlock successor;

            // add successor for jump to new block
            if (insn instanceof JumpInsn) {
                int label = ((JumpInsn) insn).getLabelRef();
                successor = cfg.addBasicBlock(new BasicBlock(getLabelUid(label, insns)));

                predecessor.addSuccessor(successor);
                successor.addPredecessor((predecessor));
            }
            // add successor when next insn is in a different basic block
            if (insn.getBasicBlock() != next.getBasicBlock() && next.getBasicBlock() != -1) {
                int nextBlock = next.getBasicBlock();
                successor = cfg.addBasicBlock(new BasicBlock(nextBlock));

                predecessor.addSuccessor(successor);
                successor.addPredecessor((predecessor));
            }
        }

        // point entry block to first block
        int first = insns.get(0).getBasicBlock();
        cfg.getBasicBlock(0).addSuccessor(cfg.getBasicBlock(first));
        cfg.getBasicBlock(first).addPredecessor(cfg.getBasicBlock(0));

        // point last block to exit block
        int last = insns.get(insns.size() - 1).getBasicBlock();
        cfg.getBasicBlock(last).addSuccessor(cfg.getBasicBlock(1));
        cfg.getBasicBlock(1).addPredecessor(cfg.getBasicBlock(last));

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

    private static int getLabelUid(int label, List<Insn> insns) {
        for (Insn insn : insns) {
            if (insn instanceof CodeLabelInsn && insn.getUid() == label) {
                return insn.getBasicBlock();
            }
        }
        return -1;
    }
}