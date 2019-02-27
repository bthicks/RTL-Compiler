package cfg;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ARMGenerator {

    // generate ARM insns in CFG
    public static void toARM(List<CFG> program) {
        // for every function in program
        for (CFG cfg : program) {
            HashMap<Integer, Integer> stack = getLookUpTable(cfg.getMaxVirtualRegister());

            // for every RTL insn, add its corresponding ARM insns to list in basic block
            for (BasicBlock block : cfg.getBasicBlocks()) {
                for (rtl.Insn rtlInsn : block.getRtlInsns()) {
                    List<arm.Insn> armInsns = rtlInsn.toARM(stack);

                    for (arm.Insn insn : armInsns) {
                        block.addArmInsn(insn);
                    }
                }
            }
        }
    }

    public static void allocateRegisters(List<CFG> program) {
        for (CFG cfg : program) {
            cfg.LVA();
            String spilledReg = cfg.colorGraph();

            while (spilledReg != null) {
                cfg.spillRegister(spilledReg);
                cfg.LVA();
                spilledReg = cfg.colorGraph();
            }

            cfg.allocateRegisters();
        }
    }

    // write ARM insns in CFG to .s file
    public static void writeARM(String filename, List<CFG> program) {
        filename = filename.replace(".json", ".s");
        File file = new File(filename);
        StringBuilder armCode = new StringBuilder();

        // append ARM file header
        armCode.append(".arch\tarmv7-a\n");
        armCode.append(".text\n");
        armCode.append(".global main\n\n");

        // for every function in program
        for (CFG cfg : program) {
            // stack setup
            armCode.append(cfg.getFunctionName()).append(":\n");

            /*armCode.append("\tpush\t{lr");
            for (String reg : cfg.getCalleeSaved()) {
                armCode.append(", r").append(reg);
            }
            armCode.append("}\n");*/

            armCode.append("\tmov\tfp, sp\n");
            armCode.append("\tsub\tsp, sp, #" + Integer.toString((cfg.getMaxVirtualRegister() - 104) * 4) + "\n");

            // write ARM insns
            for (BasicBlock block : cfg.getBasicBlocks()) {
                for (arm.Insn insn : block.getArmInsns()) {
                    armCode.append(insn.toARM());
                }
            }

            // stack teardown
            armCode.append("\tadd\tsp, sp, #" + Integer.toString((cfg.getMaxVirtualRegister() - 104) * 4) + "\n");

            /*armCode.append("\tpop\t{pc");
            for (String reg : cfg.getCalleeSaved()) {
                armCode.append(", r").append(reg);
            }
            armCode.append("}\n");*/
        }

        // write ARM code to .s file
        try (FileWriter fw = new FileWriter(file)) {
            fw.write(armCode.toString());
        } catch (IOException e) {
            System.err.println("IOException: " + e.getMessage());
        }
    }

    // returns mapping of virtual register numbers to their offset on the stack
    private static HashMap<Integer, Integer> getLookUpTable(int max) {
        HashMap<Integer, Integer> stack = new HashMap<>();
        int offset = -4;
        int min = 105; // 105 is the smallest virtual register seen in RTL

        while (max >= min) {
            stack.put(max, offset);
            offset -= 4;
            max--;
        }

        return stack;
    }
}
