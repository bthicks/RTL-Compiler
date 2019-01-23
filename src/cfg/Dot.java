package cfg;

import rtl.Insn;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class Dot {

    public static void toDot(String filename, CFG cfg) {
        filename = filename.concat(".dot");
        File file = new File(filename);

        try (FileWriter fw = new FileWriter(file, true)) {
            fw.write("digraph \"fib.c.234r.expand\" {\n" + "overlap=false;\n"
                            + "subgraph \"cluster_main\" {\n" + "style=\"dashed\";\n"
                            + "color=\"black\";\n" + "label=\"main ()\";\n"
                            + "subgraph cluster_0_1 {\n" + "style=\"filled\";\n"
                            + "color=\"darkgreen\";\n" + "fillcolor=\"grey88\";\n"
                            + "label=\"loop 1\";\n" + "labeljust=l;\n" + "penwidth=2;)\n");

            for (BasicBlock block : cfg.getBasicBlocks()) {
                fw.write("\tfn_0_basic_block_" + block.getLabel()
                        + " [shape=record,style=filled,fillcolor=lightgrey,label={");

                for (Insn insn : block.getInsns()) {
                    fw.write(insn.toDot());
                }

                fw.write("}\"];\n");
            }
        } catch (IOException e) {
            System.err.println("IOException: " + e.getMessage());
        }
    }
}
