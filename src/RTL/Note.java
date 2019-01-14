public class Note extends Insn {

    private String noteLineNumber;

    public Note(String expCode, int uid, int prevInsn, int nextInsn, int basicBlock,
                String noteLineNumber) {
        super(expCode, uid, prevInsn, nextInsn, basicBlock);
        this.noteLineNumber = noteLineNumber;
    }

    public String getNoteLineNumber() {
        return noteLineNumber;
    }

    @Override
    public String toString() {
        return Integer.toString(uid) + ": " + noteLineNumber +
                (noteLineNumber.equals("NOTE_INSN_BASIC_BLOCK") ? " " +
                        Integer.toString(basicBlock) : "") + "\n";
    }
}