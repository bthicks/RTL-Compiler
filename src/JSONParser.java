import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;
import rtl.*;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class JSONParser {

    public static List<Insn> parse(String filename) {
        try {
            BufferedReader reader = new BufferedReader(new FileReader(filename));
            JSONTokener tokener = new JSONTokener(reader);
            JSONArray jsonInsns = new JSONArray(tokener);

            return parseInsns(jsonInsns);
        }
        catch (FileNotFoundException e) {
            System.exit(0);
            return null;
        }
    }

    private static List<Insn> parseInsns(JSONArray jsonInsns) {
        List<Insn> insns = new LinkedList<>();

        for (int i = 0; i < jsonInsns.length(); i++) {
            JSONObject jsonInsn = jsonInsns.getJSONObject(i);
            String expCode = jsonInsn.getString("type");
            int uid = jsonInsn.getInt("uid");
            int prevInsn = jsonInsn.getInt("prev");
            int nextInsn = jsonInsn.getInt("next");
            int basicBlock = jsonInsn.getInt("block");
            Value target;
            List<Value> sources;
            String condition;

            switch (expCode) {
                case "insn":
                    target = parseTarget(jsonInsn.optJSONObject("target"));
                    sources = parseSources(jsonInsn.getJSONArray("sources"));
                    insns.add(new DefaultInsn(expCode, uid, prevInsn, nextInsn, basicBlock, target,
                            sources));
                    break;
                case "jump_insn":
                    target = parseTarget(jsonInsn.optJSONObject("target"));
                    sources = parseSources(jsonInsn.getJSONArray("sources"));
                    condition = jsonInsn.getString("condition");
                    int labelRef = jsonInsn.getInt("label_ref");
                    insns.add(new JumpInsn(expCode, uid, prevInsn, nextInsn, basicBlock, target,
                              sources, labelRef, condition));
                    break;
                case "call_insn/i":
                case "call_insn":
                    target = parseTarget(jsonInsn.optJSONObject("target"));
                    sources = parseSources(jsonInsn.getJSONArray("sources"));
                    insns.add(new CallInsn(expCode, uid, prevInsn, nextInsn, basicBlock, target,
                            sources));
                    break;
                case "cmp_insn":
                    sources = parseSources(jsonInsn.getJSONArray("sources"));
                    insns.add(new CmpInsn(expCode, uid, prevInsn, nextInsn, basicBlock, sources));
                case "code_label":
                    insns.add(new CodeLabelInsn(expCode, uid, prevInsn, nextInsn, basicBlock));
                    break;
                case "barrier":
                    insns.add(new BarrierInsn(expCode, uid, prevInsn, nextInsn, basicBlock));
                    break;
                case "note":
                    String noteLineNumber = jsonInsn.getString("note_type");
                    insns.add(new NoteInsn(expCode, uid, prevInsn, nextInsn, basicBlock,
                            noteLineNumber));
                    break;
            }
        }

        return insns;
    }

    private static Value parseTarget(JSONObject jsonTarget) {
        return parseValue(jsonTarget);
    }

    private static List<Value> parseSources(JSONArray jsonSources) {
        List<Value> sources = new ArrayList<>();

        for (int i = 0; i < jsonSources.length(); i++) {
            sources.add(parseValue(jsonSources.getJSONObject(i)));
        }

        return sources;
    }

    private static Value parseValue(JSONObject jsonValue) {
        String type = jsonValue.getString("type");
        int val = jsonValue.getInt("value");
        int offset = jsonValue.getInt("offset");
        Value value;

        if (type.equals("register")) {
            value = new RegisterValue(val, offset);
        } else {
            value = new ImmediateValue(val, offset);
        }

        return value;
    }
}
