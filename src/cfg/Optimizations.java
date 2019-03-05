package cfg;

import arm.Insn;
import arm.MovInsn;
import rtl.MoveInsn;

import java.util.ArrayList;
import java.util.List;

public class Optimizations {
    /**
     * Remove redundant move instructions from the cfg.
     *
     * @param program -- The program.
     */
    public static void removeRedundants(List<CFG> program) {
        for (CFG cfg : program) {
            for (BasicBlock basicBlock : cfg.getBasicBlocks()) {
                List<Insn> insns = new ArrayList<>();
                for (arm.Insn insn : basicBlock.getArmInsns()) {
                    if (insn instanceof arm.MovInsn) {
                        if (((MovInsn) insn).getR1().equals(((MovInsn) insn).getOperand2())) {
                            continue;
                        }
                    }
                    insns.add(insn);
                }

                basicBlock.setArmInsns(insns);
            }
        }
    }
}
