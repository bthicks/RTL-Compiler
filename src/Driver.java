import java.util.List;

import cfg.BasicBlock;
import cfg.CFG;
import rtl.Insn;

public class Driver {

    public static void main(String[] args) {
        String filename = "fib/fib.json";
        List<Insn> insns = JSONParser.parse(filename);
        CFG cfg = new CFG("main");

        for (Insn insn : insns) {
            cfg.addInsn(insn);
        }

        for (BasicBlock block : cfg.getBasicBlocks()) {
            if (block.getLabel() == -1) {
                continue;
            }
            System.out.println(block.getLabel());

            for (Insn insn : block.getInsns()) {
                System.out.print("\t" + insn.toString());
            }
        }
    }
}