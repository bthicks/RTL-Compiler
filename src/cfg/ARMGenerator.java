package cfg;

import rtl.Insn;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

public class ARMGenerator {

    public static void toARM(String filename, List<CFG> program) {
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

            // generate insns
            for (BasicBlock block : cfg.getBasicBlocks()) {
                for (Insn rtlInsn : block.getInsns()) {
                    armCode.append(rtlInsn.toARM());
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
