package cfg;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;

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

    // write ARM insns in CFG to .s file
    public static void writeARM(String filename, List<CFG> program) {
        filename = filename.replace(".json", ".s");
        File file = new File(filename);
        StringBuilder armCode = new StringBuilder();

        // append ARM file header
        armCode.append(".arch\tarmv7-a\n");
        armCode.append(".text\n");
        armCode.append(".global\tmain\n\n");

        // for every function in program
        for (CFG cfg : program) {
            // stack setup
            armCode.append("." + cfg.getFunctionName() + ":\n");
            armCode.append("\tpush\t{fp, lr}\n");
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
            armCode.append("\tpop\t{fp, pc}\n");
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
        int offset = 4;
        int min = 105; // 105 is the smallest virtual register seen in RTL

        while (max >= min) {
            stack.put(max, offset);
            offset += 4;
            max--;
        }

        return stack;
    }
}
