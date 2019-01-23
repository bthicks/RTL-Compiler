package cfg;

import rtl.Insn;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class Dot {

    public static void toDot(String filename, CFG cfg) {
        filename = filename.replace(".json", ".dot");
        File file = new File(filename);

        try (FileWriter fw = new FileWriter(file)) {
            fw.write("digraph \"fib.c.234r.expand\" {\n"
                    + "overlap=false;\n"
                    + "subgraph \"cluster_main\" {\n"
                    + "\tlabel=\"main ()\";\n");

            for (BasicBlock block : cfg.getBasicBlocks()) {
                if (block.getLabel() == -1) {
                    continue;
                }
                else if (block.getLabel() == 0) {
                    fw.write("\tbb_0 [shape=Mdiamond,label=\"ENTRY\"];\n");
                    continue;
                }
                else if (block.getLabel() == 1) {
                    fw.write("\tbb_1 [shape=Mdiamond,label=\"EXIT\"];\n");
                    continue;
                }

                fw.write("\tbb_" + block.getLabel() + " [shape=record,label=\n\"{");

                for (Insn insn : block.getInsns()) {
                    fw.write(insn.toDot());
                }

                fw.write("}\"];\n\n");
            }

            for (BasicBlock block : cfg.getBasicBlocks()) {
                for (BasicBlock successor : block.getSuccessors()) {
                    fw.write("\tbb_" + block.getLabel() + " -> bb_" + successor.getLabel()
                            + " [constraint=true];\n");
                }
            }

            fw.write("}\n}\n");
        } catch (IOException e) {
            System.err.println("IOException: " + e.getMessage());
        }
    }
}
