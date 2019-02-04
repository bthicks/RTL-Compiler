package cfg;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

public class ARMGenerator {

    // generate ARM insns in CFG
    public static void toARM(List<CFG> program) {
        // for every function in program
        for (CFG cfg : program) {
            // for every RTL insn, add its corresponding ARM insns to list in basic block
            for (BasicBlock block : cfg.getBasicBlocks()) {
                for (rtl.Insn rtlInsn : block.getRtlInsns()) {
                    List<arm.Insn> armInsns = rtlInsn.toARM();

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
        armCode.append(".global\tmain\n");

        // for every function in program
        for (CFG cfg : program) {
            // set up stack

            // write ARM insns
            for (BasicBlock block : cfg.getBasicBlocks()) {
                for (arm.Insn insn : block.getArmInsns()) {
                    armCode.append(insn.toString());
                }
            }

            // tear down stack

        }

        // write ARM code to .s file
        try (FileWriter fw = new FileWriter(file)) {
            fw.write(armCode.toString());
        } catch (IOException e) {
            System.err.println("IOException: " + e.getMessage());
        }
    }
}
