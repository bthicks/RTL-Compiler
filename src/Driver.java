import java.util.List;

import rtl.Insn;

// TODO: barrier block = -1
// TODO: reformat json file
// TODO: sources as array

public class Driver {

    public static void main(String[] args) {
        String filename = "fib/fib.json";
        List<Insn> insns = JSONParser.parse(filename);

        for (Insn insn : insns) {
            System.out.print(insn.toString());
        }
    }
}