package rtl;

import arm.Insn;

import java.util.List;

public class NoteInsn extends AbstractInsn {

    private String noteLineNumber;

    public NoteInsn(String expCode, int uid, int prevInsn, int nextInsn, int basicBlock,
                String noteLineNumber) {
        super(expCode, uid, prevInsn, nextInsn, basicBlock);
        this.noteLineNumber = noteLineNumber;
    }

    public String getNoteLineNumber() {
        return noteLineNumber;
    }

    @Override
    public String toString() {
        return Integer.toString(this.getUid()) + ": " + noteLineNumber
                + (noteLineNumber.equals("NOTE_INSN_BASIC_BLOCK") ?
                " " + Integer.toString(this.getBasicBlock()) : "") + "\n";
    }

    @Override
    public String toDot() {
        return "|\\ \\ \\ " + Integer.toString(this.getUid()) + ":\\ " + noteLineNumber
                + (noteLineNumber.equals("NOTE_INSN_BASIC_BLOCK")
                ? "\\ " + Integer.toString(this.getBasicBlock()) : "") + "\\l\\\n";
    }

    @Override
    public List<Insn> toARM() {
        return null;
    }
}