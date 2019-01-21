package RTL;

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
}