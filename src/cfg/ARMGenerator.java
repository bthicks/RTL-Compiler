package cfg;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

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

    public static void scheduleInsns(List<CFG> program) {
        for (CFG cfg : program) {
            for (BasicBlock basicBlock : cfg.getBasicBlocks()) {
                System.out.println("Basic Block: " + basicBlock.getLabel());
                DAG dag = new DAG(basicBlock);
                System.out.println(dag.toString());
                basicBlock.setArmInsns(dag.topographicalSort());
            }
        }
    }

    // write ARM insns in CFG to .s file
    public static void writeARM(String filename, List<CFG> program) {
        filename = filename.replace(".json", ".s");
        File file = new File(filename);
        StringBuilder armCode = new StringBuilder();
        List<String> globals = new LinkedList<>();

        // get function names for .global
        for (CFG cfg : program) {
            globals.add(cfg.getFunctionName());
        }

        // append ARM file header
        armCode.append(".arch\tarmv7-a\n");
        armCode.append(".text\n");
        armCode.append(".global " + String.join(", ", globals) + "\n\n");

        // for every function in program
        for (CFG cfg : program) {
            // stack setup
            armCode.append(cfg.getFunctionName()).append(":\n");

            List<String> calleeSaved = new ArrayList<>(cfg.getCalleeSaved());
            Collections.sort(calleeSaved, (a, b) -> Integer.parseInt(a) - Integer.parseInt(b));

            armCode.append("\tpush\t{");

            for (String reg : calleeSaved) {
                armCode.append("r").append(reg).append(", ");
            }
            armCode.append("lr}\n");

            if (cfg.getSpillOffset() > 0) {
                armCode.append("\tsub\tsp, sp, #" + Integer.toString(cfg.getSpillOffset()) + "\n");
            }

            // write ARM insns
            for (BasicBlock block : cfg.getBasicBlocks()) {
                for (arm.Insn insn : block.getArmInsns()) {
                    armCode.append(insn.toARM());
                }
            }

            // stack teardown
            if (cfg.getSpillOffset() > 0) {
                armCode.append("\tadd\tsp, sp, #" + Integer.toString(cfg.getSpillOffset()) + "\n");
            }

            armCode.append("\tpop\t{");
            for (String reg : calleeSaved) {
                armCode.append("r").append(reg).append(", ");
            }
            armCode.append("pc}\n");
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
