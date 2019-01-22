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

            switch (expCode) {
                case "insn":
                    Value target = parseTarget(jsonInsn.optJSONObject("target"));
                    List<Value> sources = parseSources(jsonInsn.getJSONArray("sources"));
                    insns.add(new DefaultInsn(expCode, uid, prevInsn, nextInsn, basicBlock,
                            target, sources));
                    break;
                case "barrier":
                    insns.add(new BarrierInsn(expCode, uid, prevInsn, nextInsn, basicBlock));
                    break;
                case "code_label":
                    insns.add(new CodeLabelInsn(expCode, uid, prevInsn, nextInsn, basicBlock));
                    break;
                case "jump_insn":
                    break;
                case "note":
                    String noteLineNumber = jsonInsn.getString("note_type");
                    insns.add(new NoteInsn(expCode, uid, prevInsn, nextInsn, basicBlock,
                            noteLineNumber));
                    break;
                case "call_insn":
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
        String val = jsonValue.getString("value");
        int offset = jsonValue.getInt("offset");
        Value value;

        if (val.startsWith("r")) {
            value = new RegisterValue(val, offset);
        } else {
            value = new ImmediateValue(val, offset);
        }

        return value;
    }
}